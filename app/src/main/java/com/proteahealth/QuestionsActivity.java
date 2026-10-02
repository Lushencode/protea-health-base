package com.proteahealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.adapter.QuestionAdapter;
import com.proteahealth.data.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    RecyclerView recyclerQuestions;
    Button btnAskQuestion;

    List<Question> questionList;
    QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questions);

        recyclerQuestions =
                findViewById(R.id.rvQuestions);

        btnAskQuestion =
                findViewById(R.id.btnAskQuestion);

        questionList = new ArrayList<>();

        // Temporary sample questions
        questionList.add(
                new Question(
                        "Patient",
                        "Can I take my medication before a blood test?",
                        "10 minutes ago",
                        1
                )
        );

        questionList.add(
                new Question(
                        "Patient",
                        "What should I eat if I have diabetes?",
                        "30 minutes ago",
                        2
                )
        );

        questionList.add(
                new Question(
                        "Patient",
                        "How often should I check my blood pressure?",
                        "1 hour ago",
                        0
                )
        );

        adapter = new QuestionAdapter(questionList);

        recyclerQuestions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerQuestions.setAdapter(adapter);

        btnAskQuestion.setOnClickListener(v -> showQuestionDialog());
    }

    private void showQuestionDialog() {
        View form = getLayoutInflater().inflate(
                R.layout.dialog_ask_question,
                null
        );

        Spinner categorySpinner =
                form.findViewById(R.id.spinnerQuestionCategory);

        EditText questionInput =
                form.findViewById(R.id.etQuestion);

        CheckBox anonymousCheck =
                form.findViewById(R.id.checkAnonymous);

        String[] categories = {
                "Choose a category",
                "Diabetes",
                "Blood pressure",
                "General health"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        categorySpinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(this)
                .setTitle("Ask a Question")
                .setView(form)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String questionText = questionInput.getText().toString().trim();
                    if (!questionText.isEmpty()) {
                        questionList.add(0, new Question(
                                anonymousCheck.isChecked() ? "Anonymous" : "Patient",
                                questionText,
                                "Just now",
                                0
                        ));
                        adapter.notifyItemInserted(0);
                        recyclerQuestions.scrollToPosition(0);
                        Toast.makeText(this, "Question submitted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
