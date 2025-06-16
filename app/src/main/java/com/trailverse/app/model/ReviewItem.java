package com.trailverse.app.model;

public class ReviewItem {
    private String routeName;
    private String description;

    public ReviewItem(String routeName, String description) {
        this.routeName = routeName;
        this.description = description;
    }

    public String getRouteName() { return routeName; }
    public String getDescription() { return description; }
}
