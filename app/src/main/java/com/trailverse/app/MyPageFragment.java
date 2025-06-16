
package com.trailverse.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trailverse.app.adapter.ReviewAdapter;
import com.trailverse.app.model.ReviewDto;
import com.trailverse.app.model.ReviewItem;
import com.trailverse.app.network.ApiClient;
import com.trailverse.app.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // RecyclerView 초기화
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setAdapter(reviewAdapter);

        // SharedPreferences에서 userId 가져오기
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "defaultUser");
        Log.d("MyPage", "userId: " + userId);

        // 서버에서 리뷰 목록 불러오기
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ReviewDto>> call = apiService.getReviewList(userId);

        call.enqueue(new Callback<List<ReviewDto>>() {
            @Override
            public void onResponse(Call<List<ReviewDto>> call, Response<List<ReviewDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewDto> reviewDtoList = response.body();

                    if (reviewDtoList.isEmpty()) {
                        Toast.makeText(getContext(), "작성된 리뷰가 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                    List<ReviewItem> reviewItems = new ArrayList<>();
                    for (ReviewDto dto : reviewDtoList) {
                        String name = dto.getRouteName() != null ? dto.getRouteName() : "이름 없는 경로";
                        String text = dto.getReviewText() != null ? dto.getReviewText() : "작성할 리뷰가 없습니다.";
                        reviewItems.add(new ReviewItem(name, text));
                    }

                    reviewAdapter.updateList(reviewItems);
                } else {
                    Toast.makeText(getContext(), "리뷰 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReviewDto>> call, Throwable t) {
                Toast.makeText(getContext(), "통신 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
