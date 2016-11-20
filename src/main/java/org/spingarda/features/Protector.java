package org.spingarda.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.CommentOperations;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Ivan on 19/11/2016.
 */
@Service
@Scope("prototype")
public class Protector {
    private static final Pattern cleanStringPattern = Pattern.compile("[^\\w\\s]");

    @Autowired
    private Validator validator;

    @Autowired
    private MathUtils mathUtils;

    private Facebook facebook;
    private CommentOperations commentOperations;

    private Map<String, FacebookUser> users;

    private ProtectorParams params;

    private boolean running;

    private String log;

    public void run(Facebook userFacebook, ProtectorParams params) throws Exception {
        this.commentOperations = userFacebook.commentOperations();
        this.facebook = new FacebookTemplate(userFacebook.pageOperations().getAccessToken(params.getPage()));

        this.params = params;

        this.users = new HashMap<>();

        setRunning(true);
        setLog("");

        try {
            do {
                effectiveRun();

                if (params.getKeepRunning() != null && params.getKeepRunning()) {
                    Thread.sleep(params.getKeepRunningInterval());
                }
            } while (params.getKeepRunning() != null && params.getKeepRunning());

            setRunning(false);
            setLog(getLog() + "Done.\n");
        } catch (Exception e) {
            logError(e.getMessage());
            setRunning(false);
            throw e;
        }
    }

    public void effectiveRun() {
        String pageId = params.getPage();
        logInfo("Obtaining page posts list " + pageId);
        PagingParameters parameters = new PagingParameters(
                params.getPostsLimit(),
                params.getPostsOffset(),
                0L,
                new Date().getTime());

        List<FacebookComment> processedComments = Observable
                .just(facebook.feedOperations().getFeed(pageId, parameters))
                .flatMap(posts -> {
                    logInfo(posts.size() + " posts found");
                    return Observable.from(posts);
                })
                .map(FacebookPost::new)
                .flatMap(post -> Observable.just(post)
                        .subscribeOn(Schedulers.computation())
                        .map(this::getComments))
                .flatMap(Observable::from)
                .toList()
                .toBlocking()
                .first();

        if (params.getCommentsInComments() != null && params.getCommentsInComments()) {
            List<FacebookComment> commentsInComments = Observable
                    .just(processedComments)
                    .flatMap(Observable::from)
                    .flatMap(comment -> Observable.just(comment)
                            .subscribeOn(Schedulers.computation())
                            .map(this::getComments))
                    .flatMap(Observable::from)
                    .toList()
                    .toBlocking()
                    .first();
            processedComments.addAll(commentsInComments);
        }


        computeUsers(processedComments);

        processedComments.parallelStream()
                .filter(comment -> canHide(comment))
                .forEach(this::hideComment);
    }

    public FacebookComment hideComment(FacebookComment comment) {
        String commentLog = comment.getOriginal().getId() + ": " + comment.getOriginal().getMessage();

        if (params.getHideOrDel().equals("hide")) {
            MultiValueMap<String, Object> updates = new LinkedMultiValueMap<>();
            updates.set("is_hidden", "true");
            logHide(commentLog);
            facebook.post(comment.getOriginal().getId(), updates);
        } else if (params.getHideOrDel().equals("del")) {
            logDel(commentLog);
            commentOperations.deleteComment(comment.getOriginal().getId());
        } else {
            logDebug(commentLog);
        }
        return comment;
    }

    public List<FacebookComment> getComments(FacebookComment parent) {
        return getComments(parent.getOriginal().getId(),
                parent.getOriginal().getMessage(),
                comment -> constructComment(parent, comment));
    }

    public List<FacebookComment> getComments(FacebookPost post) {
        return getComments(post.getOriginal().getId(),
                post.getOriginal().getMessage(),
                comment -> constructComment(post, comment));
    }

    public List<FacebookComment> getComments(String parentId, String parentMessage, Function<Comment, FacebookComment> mapFunction) {
        try {
            PagingParameters parameters = new PagingParameters(
                    params.getCommentsLimit(),
                    params.getCommentsOffset(),
                    0L,
                    new Date().getTime());

            List<Comment> comments = commentOperations.getComments(parentId, parameters);

            logInfo("Obtained comments for "
                    + parentId + ": " + parentMessage + " == "
                    + comments.size() + " comments found");

            return comments.stream()
                    .map(mapFunction)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logError(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public FacebookComment constructComment(FacebookComment parent, Comment comment) {
        FacebookComment facebookComment = new FacebookComment(comment);
        facebookComment.setParent(parent);
        facebookComment.setUserId(comment.getFrom().getId());

        parent.getComments().add(facebookComment);

        return facebookComment;
    }

    public FacebookComment constructComment(FacebookPost post, Comment comment) {
        FacebookComment facebookComment = new FacebookComment(comment);
        facebookComment.setPost(post);
        facebookComment.setUserId(comment.getFrom().getId());

        post.getComments().add(facebookComment);

        post.getUserIds().add(comment.getFrom().getId());

        return facebookComment;
    }

    public void computeUsers(List<FacebookComment> processedComments) {
        processedComments.forEach(comment -> {
            FacebookUser user = Optional.ofNullable(users.get(comment.getUserId()))
                    .orElse(new FacebookUser(comment.getUserId()));

            user.getComments().add(comment);
            user.getPosts().add(comment.getPost());
            user.getCommentDates().add(comment.getOriginal().getCreatedTime());

            comment.setUser(user);
            comment.getPost().getUsers().add(user);

            users.put(user.getUserId(), user);
        });
    }

    public boolean checkUser(FacebookUser user) {
        if (user.isChecked()) {
            return user.isBot();
        }

        user.setChecked(true);

        List<Date> commentDates = user.getCommentDates().stream().sorted().collect(Collectors.toList());
        List<Double> timeDiffs = new ArrayList<>();
        List<Double> lowerTimeDiffs = new ArrayList<>();

        for (int i = 0, j = 1; j < commentDates.size(); i++, j++) {
            Date date1 = commentDates.get(i);
            Date date2 = commentDates.get(j);

            Long diff = date2.getTime() - date1.getTime();
            timeDiffs.add((double) diff);
        }

        if (params.getLowerCommentInterval() != null) {
            Double timeDiffAvg = mathUtils.calcAvg(timeDiffs);

            timeDiffs.forEach(diff -> {
                if (Math.abs(diff - timeDiffAvg) < params.getLowerCommentInterval()) {
                    lowerTimeDiffs.add(diff);
                }
            });
        }

        Double lowerCommentsIntervalRate = 100 * ((double) lowerTimeDiffs.size() / timeDiffs.size());

        Double timeDiffVariation = 100 * mathUtils.calcVariation(timeDiffs);

        String userStatus = "Checking user " + user.getUserId()
                + ", comments: " + user.getComments().size()
                + ", posts commented: " + user.getPosts().size()
                + ", lower comment intervals rate: " + lowerCommentsIntervalRate
                + ", comment interval variation: " + timeDiffVariation;

        boolean postsCommentedLimit = validator.validatePostCommentedLimit(user, params);
        if (postsCommentedLimit) {
            userStatus += " |> The user exceeded the commented posts limit";
        }

        boolean commentsLimit = validator.validateCommentsLimit(user, params);
        if (commentsLimit) {
            userStatus += " |> The user exceeded the comments limit";
        }

        boolean commentsByPostLimit = validator.validateCommentsByPostLimit(user, params);
        if (commentsByPostLimit) {
            userStatus += " |> The user exceeded the comments by posts limit";
        }

        boolean commentIntervalLimit = validator.validateLowerCommentIntervalLimit(lowerCommentsIntervalRate, params);
        if (commentIntervalLimit) {
            userStatus += " |> The biggest part of user comments has a low interval variation";
        }

        boolean commentIntervalVariationLimit = validator.validateCommentIntervalVariation(timeDiffVariation, params);
        if (commentIntervalVariationLimit) {
            userStatus += " |> The user comments with a low time variation";
        }

        logInfo(userStatus);

        user.setBot(postsCommentedLimit
                || commentsLimit
                || commentsByPostLimit
                || commentIntervalLimit
                || commentIntervalVariationLimit);

        return user.isBot();
    }

    protected String cleanMessage(String message) {
        return cleanStringPattern.matcher(message)
                .replaceAll("")
                .toLowerCase();
    }

    public boolean containsDangerWord(FacebookComment comment) {
        List<String> messages = Arrays.asList(cleanMessage(comment.getOriginal().getMessage()).split(" "));
        String[] blockedWords = params.getBlockedWords().split(",");

        for (String blockedWord : blockedWords) {
            if (messages.contains(blockedWord.toLowerCase().trim())) {
                return true;
            }
        }

        return false;
    }

    public boolean isFromBannedUser(FacebookComment comment) {
        if (params.getBannedUsers() == null) {
            return false;
        }

        String userId = comment.getUserId();
        String[] bannedUsers = params.getBannedUsers().split(",");

        for (String bannedUser : bannedUsers) {
            if (userId.equals(bannedUser.trim())) {
                return true;
            }
        }

        return false;
    }

    public boolean canHide(FacebookComment comment) {

        if (containsDangerWord(comment)) {
            return true;
        }

        if (isFromBannedUser(comment)) {
            return true;
        }

        FacebookUser user = comment.getUser();

        return checkUser(user);
    }

    private synchronized void logError(String message) {
        log += "ERROR: " + message + "\n";
    }

    private synchronized void logInfo(String message) {
        log += "INFO: " + message + "\n";
    }

    private synchronized void logHide(String message) {
        log += "HIDE: " + message + "\n";
    }

    private synchronized void logDel(String message) {
        log += "DELETE: " + message + "\n";
    }

    private synchronized void logDebug(String message) {
        log += "DEBUG: " + message + "\n";
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public CommentOperations getCommentOperations() {
        return commentOperations;
    }

    public void setCommentOperations(CommentOperations commentOperations) {
        this.commentOperations = commentOperations;
    }

    public ProtectorParams getParams() {
        return params;
    }

    public void setParams(ProtectorParams params) {
        this.params = params;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public MathUtils getMathUtils() {
        return mathUtils;
    }

    public void setMathUtils(MathUtils mathUtils) {
        this.mathUtils = mathUtils;
    }

    public Map<String, FacebookUser> getUsers() {
        return users;
    }

    public void setUsers(Map<String, FacebookUser> users) {
        this.users = users;
    }
}
