package org.spingarda.features;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ivan on 19/11/2016.
 */
public class FacebookUser {
    private String userId;

    private Set<FacebookComment> comments;

    private Set<FacebookPost> posts;

    private Set<Date> commentDates;

    private boolean checked = false;

    private boolean bot = false;

    public FacebookUser(String userId) {
        this.userId = userId;
        comments = new HashSet<>();
        posts = new HashSet<>();
        commentDates = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacebookUser that = (FacebookUser) o;

        return userId.equals(that.userId);

    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<FacebookComment> getComments() {
        return comments;
    }

    public void setComments(Set<FacebookComment> comments) {
        this.comments = comments;
    }

    public Set<FacebookPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<FacebookPost> posts) {
        this.posts = posts;
    }

    public Set<Date> getCommentDates() {
        return commentDates;
    }

    public void setCommentDates(Set<Date> commentDates) {
        this.commentDates = commentDates;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }
}
