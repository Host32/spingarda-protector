package org.spingarda.features;

import org.springframework.social.facebook.api.Post;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ivan on 19/11/2016.
 */
public class FacebookPost {

    private Post original;

    private Set<FacebookComment> comments;

    private Set<String> userIds;
    private Set<FacebookUser> users;

    public FacebookPost(Post original) {
        this.original = original;
        this.comments = new HashSet<>();
        this.userIds = new HashSet<>();
        this.users = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacebookPost that = (FacebookPost) o;

        return original.getId().equals(that.original.getId());

    }

    @Override
    public int hashCode() {
        return original.getId().hashCode();
    }

    public Post getOriginal() {
        return original;
    }

    public void setOriginal(Post original) {
        this.original = original;
    }

    public Set<FacebookComment> getComments() {
        return comments;
    }

    public void setComments(Set<FacebookComment> comments) {
        this.comments = comments;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<String> userIds) {
        this.userIds = userIds;
    }

    public Set<FacebookUser> getUsers() {
        return users;
    }

    public void setUsers(Set<FacebookUser> users) {
        this.users = users;
    }
}
