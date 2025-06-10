package com.trailverse.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trailverse.app.R;
import com.trailverse.app.model.ReviewItem;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewItem> reviewList;

    public ReviewAdapter(List<ReviewItem> reviewList) {
        this.reviewList = reviewList;
    }

    // 🔽 여기에 추가!
    public void updateList(List<ReviewItem> newList) {
        this.reviewList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_card, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewItem item = reviewList.get(position);
        holder.routeNameText.setText(item.getRouteName());
        holder.routeDescriptionText.setText(item.getDescription());

        holder.writeButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), item.getRouteName() + " 리뷰쓰기 클릭됨", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView routeNameText;
        TextView routeDescriptionText;
        TextView writeButton;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            routeNameText = itemView.findViewById(R.id.tvRouteName);
            routeDescriptionText = itemView.findViewById(R.id.tvRouteDescription);
            writeButton = itemView.findViewById(R.id.btnWriteReview);
        }
    }
}
