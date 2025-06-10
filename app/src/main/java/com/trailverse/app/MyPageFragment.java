package com.trailverse.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trailverse.app.adapter.ReviewAdapter;
import com.trailverse.app.model.ReviewItem;
import com.trailverse.app.model.MyPageViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyPageFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private MyPageViewModel viewModel;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // RecyclerView & Adapter 초기화
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setAdapter(reviewAdapter);

        // ViewModel 준비
        viewModel = new ViewModelProvider(this).get(MyPageViewModel.class);

        // SharedPreferences에서 userId 꺼내기
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "morty");

        // LiveData 옵저빙: 서버에서 받아온 리뷰 리스트 갱신 시 어댑터에 반영
        viewModel.getReviewItems().observe(getViewLifecycleOwner(), items -> {
            reviewAdapter.updateList(items);
        });

        // 실제 리뷰 리스트 로드 요청
        viewModel.loadReviews(userId);

        return view;
    }
}
