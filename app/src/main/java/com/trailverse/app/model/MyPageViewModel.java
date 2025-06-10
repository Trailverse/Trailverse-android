package com.trailverse.app.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trailverse.app.model.ReviewDto;
import com.trailverse.app.model.ReviewItem;
import com.trailverse.app.network.ApiClient;
import com.trailverse.app.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyPageViewModel extends ViewModel {
    private final MutableLiveData<List<ReviewItem>> reviewItems = new MutableLiveData<>();

    public LiveData<List<ReviewItem>> getReviewItems() {
        return reviewItems;
    }

    public void loadReviews(String userId) {
        Log.d("MyPageViewModel", "userId 전달됨: " + userId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getReviewList(userId).enqueue(new Callback<List<ReviewDto>>() {
            @Override
            public void onResponse(Call<List<ReviewDto>> call, Response<List<ReviewDto>> response) {
                Log.d("MyPageViewModel", "응답 성공? " + response.isSuccessful());  // ✅ 추가
                Log.d("MyPageViewModel", "리뷰 개수: " + (response.body() != null ? response.body().size() : "null"));

                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewItem> list = new ArrayList<>();
                    for (ReviewDto dto : response.body()) {
                        String routeName = "등산로" + dto.getRouteId();
                        String desc = "리뷰 미작성 등산로입니다.";
                        list.add(new ReviewItem(routeName, desc));
                    }
                    reviewItems.postValue(list);
                }
            }

            @Override
            public void onFailure(Call<List<ReviewDto>> call, Throwable t) {
                Log.e("MyPageViewModel", "리뷰 목록 로딩 실패: " + t.getMessage());
            }
        });
    }

}
