package com.proteahealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.proteahealth.model.Featurebanner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class BlogActivity extends AppCompatActivity {

    private List<Article> allArticles = new ArrayList<>();
    private List<Article> visibleArticles = new ArrayList<>();

    private Button btnAll;
    private Button btnDiabetes;
    private Button btnBloodPressure;
    private Button btnNutrition;

    private ArticleAdapter articleAdapter;

    private RecyclerView recyclerQuestions;
    private QuestionAdapter questionAdapter;

    private final List<Question> questions = new ArrayList<>();

    private SharedPreferences questionStorage;

    private String selectedCategory = "All";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_blog);

        View root = findViewById(R.id.blogRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {

            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    insets.top,
                    view.getPaddingRight(),
                    view.getPaddingBottom()
            );

            return windowInsets;
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // ----------------------------------------------------
        // LOCAL QUESTION STORAGE
        // ----------------------------------------------------

        questionStorage = getSharedPreferences(
                "protea_blog_local_drafts",
                MODE_PRIVATE
        );


        // ----------------------------------------------------
        // FEATURE BANNER
        // ----------------------------------------------------

        ViewPager2 banner = findViewById(R.id.banner2);

        String[] titles = {
                "Take Control of Your Health",
                "Know Your Medication",
                "Ask. Learn. Improve."
        };

        String[] descriptions = {
                "Small healthy habits today can make a big difference tomorrow.",
                "Understand your medicines, dosage and possible side effects.",
                "Connect with healthcare professionals and learn from others."
        };

        if (banner != null) {
            banner.setAdapter(new Featurebanner(titles, descriptions));
        }


        // ----------------------------------------------------
        // ARTICLES / HEALTH TIPS
        // ----------------------------------------------------

        loadSampleArticles();

        ListView listArticles = findViewById(R.id.listArticles);

        visibleArticles.clear();
        visibleArticles.addAll(allArticles);

        articleAdapter = new ArticleAdapter(
                this,
                visibleArticles
        );

        listArticles.setAdapter(articleAdapter);

        listArticles.setOnItemClickListener(
                (parent, view, position, id) -> {

                    if (position >= 0 && position < visibleArticles.size()) {

                        Article article = visibleArticles.get(position);

                        showHealthTip(article);
                    }
                }
        );


        // ----------------------------------------------------
        // SEARCH
        // ----------------------------------------------------

        SearchView searchView = findViewById(R.id.search_bar);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        filterArticles(query);

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        filterArticles(newText);

                        return true;
                    }
                }
        );


        // ----------------------------------------------------
        // CATEGORY BUTTONS
        // ----------------------------------------------------

        btnAll = findViewById(R.id.btnAll);
        btnDiabetes = findViewById(R.id.btnDiabetes);
        btnBloodPressure = findViewById(R.id.btnBloodPressure);
        btnNutrition = findViewById(R.id.btnNutrition);


        btnAll.setOnClickListener(v -> {

            selectedCategory = "All";

            updateCategoryButtons();

            filterArticles("");
        });


        btnDiabetes.setOnClickListener(v -> {

            selectedCategory = "Diabetes";

            updateCategoryButtons();

            filterArticles("");
        });


        btnBloodPressure.setOnClickListener(v -> {

            selectedCategory = "Blood Pressure";

            updateCategoryButtons();

            filterArticles("");
        });


        btnNutrition.setOnClickListener(v -> {

            selectedCategory = "Nutrition";

            updateCategoryButtons();

            filterArticles("");
        });


        updateCategoryButtons();


        // ----------------------------------------------------
        // COMMUNITY QUESTIONS
        // ----------------------------------------------------

        recyclerQuestions = findViewById(R.id.recyclerQuestions);

        recyclerQuestions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadQuestions();

        questionAdapter = new QuestionAdapter(
                this,
                questions
        );

        recyclerQuestions.setAdapter(questionAdapter);


        // ----------------------------------------------------
        // ASK QUESTION
        // ----------------------------------------------------

        AppCompatButton btnAskQuestion =
                findViewById(R.id.btnAskQuestion);

        btnAskQuestion.setOnClickListener(
                view -> showQuestionForm()
        );


        // ----------------------------------------------------
        // MY QUESTIONS
        // ----------------------------------------------------

        AppCompatButton btnMyQuestions =
                findViewById(R.id.btnMyQuestions);

        btnMyQuestions.setOnClickListener(
                view -> showMyQuestions()
        );
    }


    // ========================================================
    // CATEGORY BUTTONS
    // ========================================================

    private void updateCategoryButtons() {

        resetButton(btnAll);
        resetButton(btnDiabetes);
        resetButton(btnBloodPressure);
        resetButton(btnNutrition);

        if (selectedCategory.equals("All")) {
            selectButton(btnAll);

        } else if (selectedCategory.equals("Diabetes")) {
            selectButton(btnDiabetes);

        } else if (selectedCategory.equals("Blood Pressure")) {
            selectButton(btnBloodPressure);

        } else if (selectedCategory.equals("Nutrition")) {
            selectButton(btnNutrition);
        }
    }


    private void resetButton(Button button) {

        button.setTextColor(Color.rgb(50, 50, 50));
        button.setBackgroundResource(
                R.drawable.input_background
        );
    }


    private void selectButton(Button button) {

        button.setTextColor(Color.WHITE);
        button.setBackgroundResource(
                R.drawable.input_background
        );
    }


    // ========================================================
    // ARTICLES
    // ========================================================

    private void loadSampleArticles() {

        allArticles.clear();

        allArticles.add(
                new Article(
                        "Diabetes",
                        "Understanding Blood Sugar",
                        "Learn why monitoring your blood sugar is important and how small lifestyle changes can help.",
                        "5 min read"
                )
        );

        allArticles.add(
                new Article(
                        "Blood Pressure",
                        "Understanding Your Blood Pressure",
                        "Learn what blood pressure numbers mean and why regular monitoring matters.",
                        "4 min read"
                )
        );

        allArticles.add(
                new Article(
                        "Nutrition",
                        "Building a Balanced Plate",
                        "A simple guide to combining vegetables, protein and healthy carbohydrates.",
                        "3 min read"
                )
        );

        allArticles.add(
                new Article(
                        "Medication",
                        "Why Medication Adherence Matters",
                        "Taking medication according to your healthcare professional's instructions can help manage chronic conditions.",
                        "4 min read"
                )
        );

        allArticles.add(
                new Article(
                        "Wellness",
                        "The Importance of Sleep",
                        "Good quality sleep supports physical health, concentration and emotional wellbeing.",
                        "3 min read"
                )
        );
    }


    private void filterArticles(String query) {

        String search = query == null
                ? ""
                : query.trim().toLowerCase();

        visibleArticles.clear();

        for (Article article : allArticles) {

            boolean matchesCategory =
                    selectedCategory.equals("All")
                            || article.category.equalsIgnoreCase(selectedCategory);

            boolean matchesSearch =
                    search.isEmpty()
                            || article.title.toLowerCase().contains(search)
                            || article.summary.toLowerCase().contains(search)
                            || article.category.toLowerCase().contains(search);

            if (matchesCategory && matchesSearch) {

                visibleArticles.add(article);
            }
        }

        articleAdapter.notifyDataSetChanged();
    }


    // ========================================================
    // HEALTH TIP POPUP
    // ========================================================

    private void showHealthTip(Article article) {
        new AlertDialog.Builder(this)
                .setTitle(article.title)
                .setMessage(
                        article.category + " • " + article.metadata + "\n\n"
                                + article.summary + "\n\n"
                                + "💡 Health tip: Always speak to your healthcare professional before making major changes to your medication or treatment."
                )
                .setPositiveButton("Close", null)
                .show();
    }


    // ========================================================
    // QUESTION FORM
    // ========================================================

    private void showQuestionForm() {

        View view = LayoutInflater.from(this)
                .inflate(
                        R.layout.dialog_ask_question,
                        null
                );

        Spinner spinner =
                view.findViewById(R.id.spinnerQuestionCategory);

        EditText etQuestion =
                view.findViewById(R.id.etQuestion);

        CheckBox checkAnonymous =
                view.findViewById(R.id.checkAnonymous);


        String[] categories = {
                "Diabetes",
                "Blood pressure",
                "Nutrition",
                "Medication",
                "General health"
        };


        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories
                );

        spinner.setAdapter(spinnerAdapter);


        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Ask a Health Question")
                        .setView(view)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Post Question",
                                null
                        )
                        .create();


        dialog.setOnShowListener(d -> {

            Button positive =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            positive.setTextColor(
                    Color.rgb(216, 27, 96)
            );


            positive.setOnClickListener(v -> {

                String question =
                        etQuestion.getText()
                                .toString()
                                .trim();


                if (TextUtils.isEmpty(question)) {

                    etQuestion.setError(
                            "Please enter your question"
                    );

                    return;
                }


                String category =
                        spinner.getSelectedItem()
                                .toString();


                boolean anonymous =
                        checkAnonymous.isChecked();


                saveQuestion(
                        question,
                        category,
                        anonymous
                );


                dialog.dismiss();
            });
        });


        dialog.show();
    }


    // ========================================================
    // SAVE QUESTION
    // ========================================================

    private void saveQuestion(
            String question,
            String category,
            boolean anonymous
    ) {

        try {

            JSONArray array =
                    readQuestions();


            JSONObject object =
                    new JSONObject();


            object.put(
                    "id",
                    System.currentTimeMillis()
            );

            object.put(
                    "text",
                    question
            );

            object.put(
                    "category",
                    category
            );

            object.put(
                    "anonymous",
                    anonymous
            );

            object.put(
                    "createdAt",
                    System.currentTimeMillis()
            );

            object.put(
                    "status",
                    "Waiting for a healthcare professional"
            );


            array.put(object);


            questionStorage
                    .edit()
                    .putString(
                            "questions",
                            array.toString()
                    )
                    .apply();


            // Refresh community feed
            loadQuestions();

            questionAdapter.notifyDataSetChanged();


            Toast.makeText(
                    this,
                    "Question posted successfully",
                    Toast.LENGTH_SHORT
            ).show();


        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Could not save question",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ========================================================
    // LOAD QUESTIONS
    // ========================================================

    private void loadQuestions() {

        questions.clear();


        // Sample community questions
        questions.add(
                new Question(
                        "Sarah M.",
                        "Diabetes",
                        "I've recently been diagnosed with diabetes. What are some simple lifestyle changes I can start with?",
                        "Dr. Naledi M.",
                        "Start by focusing on consistent meals, regular physical activity and taking your prescribed medication as directed. Your healthcare team can help you create a plan that works for you.",
                        true,
                        12
                )
        );


        questions.add(
                new Question(
                        "Anonymous",
                        "Blood Pressure",
                        "How often should someone with high blood pressure check their blood pressure?",
                        "Dr. Khumalo",
                        "The frequency depends on your individual treatment plan. Ask your healthcare professional how often you should monitor it at home.",
                        true,
                        8
                )
        );


        questions.add(
                new Question(
                        "Thando",
                        "Nutrition",
                        "What does a balanced plate actually look like?",
                        "",
                        "",
                        false,
                        5
                )
        );


        // Add locally saved questions
        try {

            JSONArray array =
                    readQuestions();


            for (int i = 0; i < array.length(); i++) {

                JSONObject object =
                        array.getJSONObject(i);


                String text =
                        object.optString(
                                "text",
                                ""
                        );


                String category =
                        object.optString(
                                "category",
                                "General health"
                        );


                boolean anonymous =
                        object.optBoolean(
                                "anonymous",
                                false
                        );


                String name =
                        anonymous
                                ? "Anonymous"
                                : "You";


                questions.add(
                        new Question(
                                name,
                                category,
                                text,
                                "",
                                "",
                                false,
                                0
                        )
                );
            }

        } catch (Exception ignored) {
        }
    }


    // ========================================================
    // READ QUESTIONS
    // ========================================================

    private JSONArray readQuestions() {

        String raw =
                questionStorage.getString(
                        "questions",
                        "[]"
                );

        try {

            return new JSONArray(raw);

        } catch (Exception e) {

            return new JSONArray();
        }
    }


    // ========================================================
    // MY QUESTIONS
    // ========================================================

    private void showMyQuestions() {
        Intent intent = new Intent(this, QuestionsActivity.class);
        startActivity(intent);
    }


    private void showSavedQuestion(JSONObject object) {

        String category =
                object.optString(
                        "category",
                        ""
                );


        String text =
                object.optString(
                        "text",
                        ""
                );


        boolean anonymous =
                object.optBoolean(
                        "anonymous",
                        false
                );


        String status =
                object.optString(
                        "status",
                        "Saved"
                );


        String message =
                "Category: "
                        + category
                        + "\n\n"
                        + text
                        + "\n\n"
                        + "Posted as: "
                        + (anonymous
                        ? "Anonymous"
                        : "Your name")
                        + "\n\n"
                        + "Status: "
                        + status;


        new AlertDialog.Builder(this)
                .setTitle("My Question")
                .setMessage(message)
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    // ========================================================
    // ARTICLE MODEL
    // ========================================================

    static class Article {

        String category;
        String title;
        String summary;
        String metadata;


        Article(
                String category,
                String title,
                String summary,
                String metadata
        ) {

            this.category = category;
            this.title = title;
            this.summary = summary;
            this.metadata = metadata;
        }
    }


    // ========================================================
    // ARTICLE ADAPTER
    // ========================================================

    static class ArticleAdapter extends BaseAdapter {

        private final Context context;
        private final List<Article> articles;


        ArticleAdapter(
                Context context,
                List<Article> articles
        ) {

            this.context = context;
            this.articles = articles;
        }


        @Override
        public int getCount() {

            return articles.size();
        }


        @Override
        public Object getItem(int position) {

            return articles.get(position);
        }


        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent
        ) {

            if (convertView == null) {

                convertView =
                        LayoutInflater.from(context)
                                .inflate(
                                        R.layout.item_blog_article,
                                        parent,
                                        false
                                );
            }


            Article article =
                    articles.get(position);


            TextView category =
                    convertView.findViewById(
                            R.id.tvArticleCategory
                    );


            TextView title =
                    convertView.findViewById(
                            R.id.tvArticleTitle
                    );


            TextView summary =
                    convertView.findViewById(
                            R.id.tvArticleSummary
                    );


            TextView metadata =
                    convertView.findViewById(
                            R.id.tvArticleMetadata
                    );


            TextView tileLabel =
                    convertView.findViewById(
                            R.id.tvArticleTileLabel
                    );


            category.setText(
                    article.category
            );

            title.setText(
                    article.title
            );

            summary.setText(
                    article.summary
            );

            metadata.setText(
                    article.metadata
            );

            tileLabel.setText(
                    article.category.substring(
                            0,
                            Math.min(
                                    1,
                                    article.category.length()
                            )
                    ).toUpperCase()
            );


            return convertView;
        }
    }


    // ========================================================
    // QUESTION MODEL
    // ========================================================

    static class Question {

        String userName;
        String category;
        String question;
        String doctorName;
        String doctorAnswer;
        boolean verified;
        int likes;


        Question(
                String userName,
                String category,
                String question,
                String doctorName,
                String doctorAnswer,
                boolean verified,
                int likes
        ) {

            this.userName = userName;
            this.category = category;
            this.question = question;
            this.doctorName = doctorName;
            this.doctorAnswer = doctorAnswer;
            this.verified = verified;
            this.likes = likes;
        }
    }


    // ========================================================
    // QUESTION ADAPTER
    // ========================================================

    static class QuestionAdapter
            extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

        private final Context context;
        private final List<Question> questions;


        QuestionAdapter(
                Context context,
                List<Question> questions
        ) {

            this.context = context;
            this.questions = questions;
        }


        @NonNull
        @Override
        public QuestionViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            View view =
                    LayoutInflater.from(context)
                            .inflate(
                                    R.layout.items_question,
                                    parent,
                                    false
                            );

            return new QuestionViewHolder(view);
        }


        @Override
        public void onBindViewHolder(
                @NonNull QuestionViewHolder holder,
                int position
        ) {

            Question question =
                    questions.get(position);


            holder.tvName.setText(
                    question.userName
            );

            holder.tvCategory.setText(
                    question.category
            );

            holder.tvQuestion.setText(
                    question.question
            );


            if (!TextUtils.isEmpty(
                    question.doctorAnswer
            )) {

                holder.doctorAnswerContainer
                        .setVisibility(View.VISIBLE);

                holder.tvDoctorName.setText(
                        question.doctorName
                );

                holder.tvDoctorAnswer.setText(
                        question.doctorAnswer
                );

                holder.tvVerified.setVisibility(
                        question.verified
                                ? View.VISIBLE
                                : View.GONE
                );

            } else {

                holder.doctorAnswerContainer
                        .setVisibility(View.GONE);
            }


            holder.tvLikes.setText(
                    "♡ " + question.likes
            );


            holder.tvLikes.setOnClickListener(v -> {

                question.likes++;

                holder.tvLikes.setText(
                        "♥ " + question.likes
                );
            });
        }


        @Override
        public int getItemCount() {

            return questions.size();
        }


        static class QuestionViewHolder
                extends RecyclerView.ViewHolder {

            TextView tvName;
            TextView tvCategory;
            TextView tvQuestion;
            TextView tvDoctorName;
            TextView tvDoctorAnswer;
            TextView tvVerified;
            TextView tvLikes;

            LinearLayout doctorAnswerContainer;


            QuestionViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvQuestionUser);
                tvCategory = itemView.findViewById(R.id.tvQuestionTag);
                tvQuestion = itemView.findViewById(R.id.tvQuestionTitle);
                tvDoctorName = itemView.findViewById(R.id.tvQuestionUser);
                tvDoctorAnswer = itemView.findViewById(R.id.tvDoctorAnswer);
                tvVerified = itemView.findViewById(R.id.tvQuestionTag);
                tvLikes = itemView.findViewById(R.id.tvCommentCount);
                doctorAnswerContainer = itemView.findViewById(R.id.layoutDoctorAnswer);
            }
        }
    }
}