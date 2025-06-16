package com.trailverse.app.model;

import java.util.List;

public class HikingSessionRequestDto {
    private String userId;
    private String startTime;
    private String endTime;
    private double totalDistance;
    private List<PathPointDto> path;  // ✅ 서버와 맞춤

    public HikingSessionRequestDto(String userId, String startTime, String endTime,
                                   double totalDistance, List<PathPointDto> path) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalDistance = totalDistance;
        this.path = path;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public List<PathPointDto> getPath() { return path; }
    public void setPath(List<PathPointDto> path) { this.path = path; }
}
