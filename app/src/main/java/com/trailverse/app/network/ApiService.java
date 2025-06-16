package com.trailverse.app.network;

import com.trailverse.app.model.HikingSessionRequestDto;
import com.trailverse.app.model.LoginRequest;
import com.trailverse.app.model.ReviewDto;
import com.trailverse.app.model.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/trailvers/login")
    Call<UserDto> login(@Body LoginRequest request);

    @POST("/api/v1/hiking/sessions")
    Call<Void> saveHikingSession(@Body HikingSessionRequestDto dto);

    @GET("/review/myPage")
    Call<List<ReviewDto>> getReviewList(@Query("userId") String userId);

}
