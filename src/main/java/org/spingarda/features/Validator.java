package org.spingarda.features;

import org.springframework.stereotype.Service;

/**
 * Created by Ivan on 20/11/2016.
 */
@Service
public class Validator {


    public boolean validatePostCommentedLimit(FacebookUser user, ProtectorParams params) {
        return params.getPostsCommentedNumber() != null && user.getPosts().size() >= params.getPostsCommentedNumber();
    }

    public boolean validateCommentsLimit(FacebookUser user, ProtectorParams params) {
        return params.getCommentsNumber() != null && user.getComments().size() >= params.getCommentsNumber();
    }

    public boolean validateCommentsByPostLimit(FacebookUser user, ProtectorParams params) {
        return params.getCommentsByPost() != null && user.getComments().size() / user.getPosts().size() >= params.getCommentsByPost();
    }

    public boolean validateLowerCommentIntervalLimit(Double lowerCommentIntervalRate, ProtectorParams params) {
        return params.getLowerCommentInterval() != null
                && params.getLowerCommentIntervalRate() != null
                && lowerCommentIntervalRate >= params.getLowerCommentIntervalRate();
    }

    public boolean validateCommentIntervalVariation(Double variation, ProtectorParams params) {
        return params.getCommentIntervalVariation() != null && variation <= params.getCommentIntervalVariation();
    }

}
