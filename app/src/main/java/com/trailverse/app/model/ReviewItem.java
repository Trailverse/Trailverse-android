package com.trailverse.app.model;

public class ReviewItem {
    private String routeName;
    private String description;  // 예: "2025.06.01에 개척한 등산로에요!"

    public ReviewItem(String routeName, String description) {
        this.routeName = routeName;
        this.description = description;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getDescription() {
        return description;
    }
}
