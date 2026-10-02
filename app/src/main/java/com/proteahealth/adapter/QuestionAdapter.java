package com.proteahealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;
import com.proteahealth.data.Question;

import java.util.List;

public class QuestionAdapter
        extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_question, parent, false);

        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull QuestionViewHolder holder,
            int position) {

        Question question = questionList.get(position);

        String name = question.getPatientName();
        holder.tvQuestionUser.setText(name);
        if (name != null && !name.isEmpty()) {
            holder.tvQuestionAvatar.setText(name.substring(0, 1).toUpperCase());
        }

        holder.tvQuestionTitle.setText(question.getQuestion());
        holder.tvQuestionDate.setText(question.getDate());

        int count = question.getResponseCount();
        holder.tvCommentCount.setText(count + (count == 1 ? " Comment" : " Comments"));

        holder.layoutComments.setOnClickListener(v -> {

            // Open comments screen
            // We will connect this to your CommentsActivity
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvQuestionAvatar;
        TextView tvQuestionUser;
        TextView tvQuestionDate;
        TextView tvQuestionTitle;
        TextView tvCommentCount;
        View layoutComments;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuestionAvatar =
                    itemView.findViewById(R.id.tvQuestionAvatar);

            tvQuestionUser =
                    itemView.findViewById(R.id.tvQuestionUser);

            tvQuestionDate =
                    itemView.findViewById(R.id.tvQuestionDate);

            tvQuestionTitle =
                    itemView.findViewById(R.id.tvQuestionTitle);

            tvCommentCount =
                    itemView.findViewById(R.id.tvCommentCount);

            layoutComments =
                    itemView.findViewById(R.id.layoutComments);
        }
    }
}
