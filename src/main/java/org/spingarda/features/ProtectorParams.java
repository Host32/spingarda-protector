package org.spingarda.features;

/**
 * Created by Ivan on 19/11/2016.
 */
public class ProtectorParams {
    private String page;

    private Integer postsLimit;

    private Integer postsOffset;

    private Integer commentsLimit;

    private Integer commentsOffset;

    private Integer commentsNumber;

    private Integer postsCommentedNumber;

    private Integer commentsByPost;

    private Long lowerCommentInterval;

    private Double lowerCommentIntervalRate;

    private Double commentIntervalVariation;

    private String blockedWords;

    private String hideOrDel;

    private String bannedUsers;

    private Boolean keepRunning;

    private Long keepRunningInterval;

    private Boolean commentsInComments;

    public Boolean getCommentsInComments() {
        return commentsInComments;
    }

    public void setCommentsInComments(Boolean commentsInComments) {
        this.commentsInComments = commentsInComments;
    }

    public Long getKeepRunningInterval() {
        return keepRunningInterval;
    }

    public void setKeepRunningInterval(Long keepRunningInterval) {
        this.keepRunningInterval = keepRunningInterval;
    }

    public Boolean getKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(Boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public Double getLowerCommentIntervalRate() {
        return lowerCommentIntervalRate;
    }

    public void setLowerCommentIntervalRate(Double lowerCommentIntervalRate) {
        this.lowerCommentIntervalRate = lowerCommentIntervalRate;
    }

    public Double getCommentIntervalVariation() {
        return commentIntervalVariation;
    }

    public void setCommentIntervalVariation(Double commentIntervalVariation) {
        this.commentIntervalVariation = commentIntervalVariation;
    }

    public String getBannedUsers() {
        return bannedUsers;
    }

    public void setBannedUsers(String bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public String getHideOrDel() {
        return hideOrDel;
    }

    public void setHideOrDel(String hideOrDel) {
        this.hideOrDel = hideOrDel;
    }

    public String getBlockedWords() {
        return blockedWords;
    }

    public void setBlockedWords(String blockedWords) {
        this.blockedWords = blockedWords;
    }

    public Long getLowerCommentInterval() {
        return lowerCommentInterval;
    }

    public void setLowerCommentInterval(Long lowerCommentInterval) {
        this.lowerCommentInterval = lowerCommentInterval;
    }

    public Integer getCommentsByPost() {
        return commentsByPost;
    }

    public void setCommentsByPost(Integer commentsByPost) {
        this.commentsByPost = commentsByPost;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Integer getPostsLimit() {
        return postsLimit;
    }

    public void setPostsLimit(Integer postsLimit) {
        this.postsLimit = postsLimit;
    }

    public Integer getPostsOffset() {
        return postsOffset;
    }

    public void setPostsOffset(Integer postsOffset) {
        this.postsOffset = postsOffset;
    }

    public Integer getCommentsLimit() {
        return commentsLimit;
    }

    public void setCommentsLimit(Integer commentsLimit) {
        this.commentsLimit = commentsLimit;
    }

    public Integer getCommentsOffset() {
        return commentsOffset;
    }

    public void setCommentsOffset(Integer commentsOffset) {
        this.commentsOffset = commentsOffset;
    }

    public Integer getCommentsNumber() {
        return commentsNumber;
    }

    public void setCommentsNumber(Integer commentsNumber) {
        this.commentsNumber = commentsNumber;
    }

    public Integer getPostsCommentedNumber() {
        return postsCommentedNumber;
    }

    public void setPostsCommentedNumber(Integer postsCommentedNumber) {
        this.postsCommentedNumber = postsCommentedNumber;
    }

    @Override
    public String toString() {
        return "ProtectorParams{" +
                "page='" + page + '\'' +
                ", postsLimit=" + postsLimit +
                ", postsOffset=" + postsOffset +
                ", commentsLimit=" + commentsLimit +
                ", commentsOffset=" + commentsOffset +
                ", commentsNumber=" + commentsNumber +
                ", postsCommentedNumber=" + postsCommentedNumber +
                ", commentsByPost=" + commentsByPost +
                ", lowerCommentInterval=" + lowerCommentInterval +
                ", blockedWords='" + blockedWords + '\'' +
                ", hideOrDel='" + hideOrDel + '\'' +
                '}';
    }
}
