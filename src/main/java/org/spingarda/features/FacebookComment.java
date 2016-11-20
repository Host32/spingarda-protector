package org.spingarda.features;


import org.springframework.social.facebook.api.Comment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ivan on 19/11/2016.
 */
public class FacebookComment {
    private Comment original;

    private String userId;

    private FacebookUser user;

    private FacebookPost post;

    private FacebookComment parent;

    private Set<FacebookComment> comments;

    private boolean hide;

    public FacebookComment(Comment original) {
        this.original = original;
        this.comments = new HashSet<>();
        this.hide = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacebookComment that = (FacebookComment) o;

        return original.getId().equals(that.original.getId());

    }

    @Override
    public int hashCode() {
        return original.getId().hashCode();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Comment getOriginal() {
        return original;
    }

    public void setOriginal(Comment original) {
        this.original = original;
    }

    public FacebookPost getPost() {
        return post;
    }

    public void setPost(FacebookPost post) {
        this.post = post;
    }

    public Set<FacebookComment> getComments() {
        return comments;
    }

    public void setComments(Set<FacebookComment> comments) {
        this.comments = comments;
    }

    public FacebookComment getParent() {
        return parent;
    }

    public void setParent(FacebookComment parent) {
        this.parent = parent;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public FacebookUser getUser() {
        return user;
    }

    public void setUser(FacebookUser user) {
        this.user = user;
    }
}

