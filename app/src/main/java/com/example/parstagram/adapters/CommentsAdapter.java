package com.example.parstagram.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parstagram.Helper;
import com.example.parstagram.R;
import com.example.parstagram.models.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    Context context;
    List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivUserImage;
        TextView tvUsername;
        TextView tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
        }

        public void bind(Comment comment) {
            Helper.loadCircleCropImage(context, ivUserImage, comment.getUser().getParseFile("image"));
            tvUsername.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());
        }
    }
}
