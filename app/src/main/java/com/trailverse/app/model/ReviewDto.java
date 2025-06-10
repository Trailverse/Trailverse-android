package com.trailverse.app.model;

public class ReviewDto {
    private Long reviewId;
    private Long routeId;
    private String userId;
    private String reviewText;

    public Long getReviewId() {
        return reviewId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getReviewText() {
        return reviewText;
    }
}
