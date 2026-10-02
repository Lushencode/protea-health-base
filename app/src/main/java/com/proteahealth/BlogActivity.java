package com.proteahealth;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.proteahealth.model.Featurebanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BlogActivity extends AppCompatActivity {

    private final List<Article> allArticles = new ArrayList<>();
    private final List<Article> visibleArticles = new ArrayList<>();

    private Button btnAll;
    private Button btnDiabetes;
    private Button btnBloodPressure;

    private ArticleAdapter articleAdapter;
    private SharedPreferences questionStorage;

    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blog);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.blogRoot),
                (view, windowInsets) -> {
                    Insets bars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.ime()
                    );

                    view.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            bars.bottom
                    );

                    return windowInsets;
                }
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        questionStorage = getSharedPreferences(
                "protea_blog_local_drafts",
                MODE_PRIVATE
        );

        // Banner
        String[] titles = {
                "Simple",
                "Understand",
                "Easy Prescription Refills"
        };

        String[] descriptions = {
                "Trusted Information",
                "Your on health, one step at a time.",
                "Upload your prescription and manage your pharmacy refills with ease."
        };

        ViewPager2 featureViewPager = findViewById(R.id.banner2);
        if (featureViewPager != null) {
            featureViewPager.setAdapter(new Featurebanner(titles, descriptions));
        }

        loadSampleArticles();

        ListView listArticles = findViewById(R.id.listArticles);
        if (listArticles != null) {
            articleAdapter = new ArticleAdapter();
            listArticles.setAdapter(articleAdapter);

            listArticles.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        if (position < visibleArticles.size()) {
                            openArticle(visibleArticles.get(position));
                        }
                    }
            );
        }

        SearchView searchBar = findViewById(R.id.search_bar);
        if (searchBar != null) {
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterArticlesByQuery(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterArticlesByQuery(newText);
                    return true;
                }
            });
        }

        if (btnAll != null) btnAll.setOnClickListener(view -> filterArticles("All"));
        if (btnDiabetes != null) btnDiabetes.setOnClickListener(view -> filterArticles("Diabetes"));
        if (btnBloodPressure != null) btnBloodPressure.setOnClickListener(view -> filterArticles("Blood pressure"));



        findViewById(R.id.recyclerQuestions).setOnClickListener(
                view -> showQuestionForm()
        );

        findViewById(R.id.btnMyQuestions).setOnClickListener(
                view -> showMyQuestions()
        );

        if (savedInstanceState != null) {
            selectedCategory = savedInstanceState.getString(
                    "selected_category",
                    "All"
            );
        }

        filterArticles(selectedCategory);
    }

    private void loadSampleArticles() {
        // Replace this source with the group's article repository/API.
        // These sample bodies deliberately make no clinical claims.

        allArticles.add(new Article(
                "sample-diabetes-1",
                "Diabetes",
                "Small changes that help control your sugar",
                "Easy food and routine tips",
                "Sample article",
                "This is sample content for the article-reading screen.\n\n"
                        + "The published version of this article will be "
                        + "loaded from the shared health-content service.\n\n"
                        + "Its approved text, author, reading time and review "
                        + "details should come from that service.",
                "#EF5B62"
        ));

        allArticles.add(new Article(
                "sample-bp-1",
                "Blood pressure",
                "How to check your blood pressure at home",
                "A simple step-by-step guide",
                "Sample article",
                "This is sample content for the blood-pressure article.\n\n"
                        + "The final article will contain the healthcare "
                        + "professional's approved guidance.\n\n"
                        + "This screen already supports opening and scrolling "
                        + "through the complete article text.",
                "#159EA5"
        ));

        allArticles.add(new Article(
                "sample-bp-2",
                "Blood pressure",
                "Preparing for your next clinic visit",
                "Questions and notes for your appointment",
                "Sample article",
                "This is a second sample article in the Blood pressure "
                        + "category.\n\n"
                        + "It allows you to check that the category filter "
                        + "shows multiple matching articles.\n\n"
                        + "Replace this content with approved published "
                        + "content when connecting the backend.",
                "#0C514A"
        ));
    }

    private void filterArticlesByQuery(String query) {
        visibleArticles.clear();
        if (query == null || query.trim().isEmpty()) {
            filterArticles(selectedCategory);
            return;
        }
        String lowerQuery = query.toLowerCase(Locale.ENGLISH).trim();
        for (Article article : allArticles) {
            if (article.title.toLowerCase(Locale.ENGLISH).contains(lowerQuery)
                    || article.category.toLowerCase(Locale.ENGLISH).contains(lowerQuery)
                    || article.subtitle.toLowerCase(Locale.ENGLISH).contains(lowerQuery)) {
                visibleArticles.add(article);
            }
        }
        if (articleAdapter != null) {
            articleAdapter.notifyDataSetChanged();
        }
    }

    private void filterArticles(String category) {
        selectedCategory = category;
        visibleArticles.clear();

        for (Article article : allArticles) {
            if (category.equals("All")
                    || article.category.equals(category)) {
                visibleArticles.add(article);
            }
        }

        updateFilterButton(btnAll, category.equals("All"));
        updateFilterButton(btnDiabetes, category.equals("Diabetes"));
        updateFilterButton(
                btnBloodPressure,
                category.equals("Blood pressure")
        );

        if (articleAdapter != null) {
            articleAdapter.notifyDataSetChanged();
        }
    }

    private void updateFilterButton(Button button, boolean selected) {
        if (button == null) return;
        int background = Color.parseColor(
                selected ? "#0C514A" : "#E0E0E0"
        );

        button.setBackgroundTintList(
                ColorStateList.valueOf(background)
        );

        button.setTextColor(
                Color.parseColor(selected ? "#FFFFFF" : "#333333")
        );

        button.setSelected(selected);
    }

    private void openArticle(Article article) {
        // AlertDialog automatically scrolls long article text.
        new AlertDialog.Builder(this)
                .setTitle(article.title)
                .setMessage(
                        article.category + "\n"
                                + article.metadata + "\n\n"
                                + article.body
                )
                .setPositiveButton("Close", null)
                .show();
    }

    private void showQuestionForm() {
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

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(categoryAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ask a question")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save on device", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(view -> {

                            String question = questionInput.getText()
                                    .toString().trim();

                            if (categorySpinner.getSelectedItemPosition() == 0) {
                                Toast.makeText(
                                        this,
                                        "Please choose a category.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            if (question.isEmpty()) {
                                questionInput.setError(
                                        "Please enter your question."
                                );
                                questionInput.requestFocus();
                                return;
                            }

                            boolean saved = saveQuestion(
                                    question,
                                    categorySpinner.getSelectedItem().toString(),
                                    anonymousCheck.isChecked()
                            );

                            if (saved) {
                                dialog.dismiss();

                                Toast.makeText(
                                        this,
                                        "Saved on this device. Not sent yet.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        })
        );

        dialog.show();
    }

    private boolean saveQuestion(
            String text,
            String category,
            boolean anonymous
    ) {
        try {
            JSONArray questions = readQuestions();

            JSONObject question = new JSONObject();
            question.put("id", UUID.randomUUID().toString());
            question.put("text", text);
            question.put("category", category);
            question.put("anonymous", anonymous);
            question.put("createdAt", System.currentTimeMillis());
            question.put("status", "Saved on device — not sent");

            questions.put(question);

            // Local prototype storage only.
            // Production storage must follow the group's security design.
            questionStorage.edit()
                    .putString("questions", questions.toString())
                    .apply();

            return true;

        } catch (JSONException exception) {
            Toast.makeText(
                    this,
                    "Could not save the question. Existing drafts were kept.",
                    Toast.LENGTH_LONG
            ).show();

            return false;
        }
    }

    private JSONArray readQuestions() throws JSONException {
        return new JSONArray(
                questionStorage.getString("questions", "[]")
        );
    }

    private void showMyQuestions() {
        try {
            JSONArray questions = readQuestions();

            if (questions.length() == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("My questions")
                        .setMessage("You have no questions saved on this device.")
                        .setPositiveButton("Close", null)
                        .show();
                return;
            }

            String[] titles = new String[questions.length()];

            for (int i = 0; i < questions.length(); i++) {
                JSONObject question = questions.getJSONObject(i);

                titles[i] = question.getString("category")
                        + ": " + question.getString("text");
            }

            new AlertDialog.Builder(this)
                    .setTitle("My questions · Local drafts")
                    .setItems(titles, (dialog, position) -> {
                        try {
                            showSavedQuestion(
                                    questions.getJSONObject(position)
                            );
                        } catch (JSONException exception) {
                            showReadError();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();

        } catch (JSONException exception) {
            showReadError();
        }
    }

    private void showSavedQuestion(JSONObject question)
            throws JSONException {

        boolean anonymous = question.getBoolean("anonymous");

        String identity = anonymous
                ? "Posting preference: Anonymous"
                : "Posting preference: Use my profile name";

        new AlertDialog.Builder(this)
                .setTitle(question.getString("category"))
                .setMessage(
                        identity + "\n\n"
                                + question.getString("text") + "\n\n"
                                + "Status: " + question.getString("status")
                )
                .setPositiveButton("Close", null)
                .show();
    }

    private void showReadError() {
        Toast.makeText(
                this,
                "Could not read the saved questions.",
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("selected_category", selectedCategory);
        super.onSaveInstanceState(outState);
    }

    private static class Article {
        final String id;
        final String category;
        final String title;
        final String summary;
        final String metadata;
        final String body;
        final String colour;
        public String subtitle;

        Article(
                String id,
                String category,
                String title,
                String summary,
                String metadata,
                String body,
                String colour
        ) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.summary = summary;
            this.metadata = metadata;
            this.body = body;
            this.colour = colour;
        }
    }

    private class ArticleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return visibleArticles.size();
        }

        @Override
        public Article getItem(int position) {
            return visibleArticles.get(position);
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
                convertView = LayoutInflater.from(BlogActivity.this)
                        .inflate(R.layout.item_blog_article, parent, false);
            }

            Article article = getItem(position);

            TextView category =
                    convertView.findViewById(R.id.tvArticleCategory);
            TextView title =
                    convertView.findViewById(R.id.tvArticleTitle);
            TextView summary =
                    convertView.findViewById(R.id.tvArticleSummary);
            TextView metadata =
                    convertView.findViewById(R.id.tvArticleMetadata);
            TextView tileLabel =
                    convertView.findViewById(R.id.tvArticleTileLabel);

            category.setText(
                    article.category.toUpperCase(Locale.ENGLISH)
            );
            title.setText(article.title);
            summary.setText(article.summary);
            metadata.setText(article.metadata);

            tileLabel.setText(
                    article.category.equals("Blood pressure")
                            ? "Blood\npressure"
                            : article.category
            );

            View tile = convertView.findViewById(
                    R.id.articleCategoryTile
            );

            GradientDrawable background =
                    (GradientDrawable) tile.getBackground().mutate();

            background.setColor(Color.parseColor(article.colour));
            tile.setBackground(background);

            return convertView;
        }
    }
}
