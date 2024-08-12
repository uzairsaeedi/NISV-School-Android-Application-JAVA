package com.nisvschoolug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>{
    private List<FeedbackModel> feedbackList;

    public FeedbackAdapter(List<FeedbackModel> feedbackList) {
        this.feedbackList = feedbackList;
    }
    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackModel feedback = feedbackList.get(position);
        holder.bind(feedback);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        private TextView txtEmail, txtMessage;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.text_view_email_content);
            txtMessage = itemView.findViewById(R.id.text_view_message_content);
        }

        public void bind(FeedbackModel feedback) {
            txtEmail.setText(feedback.getEmail());
            txtMessage.setText(feedback.getMessage());
        }
    }
}
