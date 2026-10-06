package com.proteahealth;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HealthTipActivity extends AppCompatActivity {

    private static final String[] CATEGORIES = {
            "Diabetes",
            "Blood Pressure",
            "Nutrition",
            "Medication",
            "Wellness"
    };

    private SharedPreferences storage;
    private JSONArray tips = new JSONArray();

    private Spinner spinnerTips;
    private Spinner spinnerCategory;

    private EditText etTitle;
    private EditText etBody;

    private TextView tvTitleCount;
    private TextView tvBodyCount;
    private TextView tvImageName;

    private TextView previewTile;
    private TextView previewCategory;
    private TextView previewStatus;
    private TextView previewTitle;
    private TextView previewBody;
    private ImageView imgPreview;

    private long currentId = -1;
    private String currentStatus = "Draft";
    private String imagePath = "";
    private String imageName = "";

    private ActivityResultLauncher<String> imagePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_healthtip);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        storage = getSharedPreferences("protea_health_tips", MODE_PRIVATE);

        spinnerTips = findViewById(R.id.spinnerTips);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        etTitle = findViewById(R.id.etTitle);
        etBody = findViewById(R.id.etBody);

        tvTitleCount = findViewById(R.id.tvTitleCount);
        tvBodyCount = findViewById(R.id.tvBodyCount);
        tvImageName = findViewById(R.id.tvImageName);

        previewTile = findViewById(R.id.previewTile);
        previewCategory = findViewById(R.id.previewCategory);
        previewStatus = findViewById(R.id.previewStatus);
        previewTitle = findViewById(R.id.previewTitle);
        previewBody = findViewById(R.id.previewBody);
        imgPreview = findViewById(R.id.imgPreview);

        Button btnTipUploadImage = findViewById(R.id.btnTipUploadImage);
        Button btnTipSaveDraft = findViewById(R.id.btnTipSaveDraft);
        Button btnTipPublish = findViewById(R.id.btnTipPublish);

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        handleImage(uri);
                    }
                }
        );

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                CATEGORIES
        );

        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        updatePreview();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        spinnerTips.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (position == 0) {

                            if (currentId != -1) {
                                resetForm();
                            }

                            return;
                        }

                        try {

                            JSONObject object = tips.getJSONObject(position - 1);

                            if (object.optLong("id") == currentId) {
                                return;
                            }

                            fillForm(object);

                        } catch (Exception ignored) {
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        etTitle.addTextChangedListener(new SimpleWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                tvTitleCount.setText(s.length() + "/40");

                updatePreview();
            }
        });

        etBody.addTextChangedListener(new SimpleWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                tvBodyCount.setText(s.length() + "/300");

                updatePreview();
            }
        });

        btnTipUploadImage.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnTipSaveDraft.setOnClickListener(v -> saveTip("Draft"));

        btnTipPublish.setOnClickListener(v -> saveTip("Published"));

        loadTips();

        refreshTipSpinner(-1);

        resetForm();
    }


    private void loadTips() {

        try {

            tips = new JSONArray(
                    storage.getString("tips", "[]")
            );

        } catch (Exception e) {

            tips = new JSONArray();
        }
    }


    private void refreshTipSpinner(long selectId) {

        List<String> names = new ArrayList<>();

        names.add("+  New health tip");

        int selectedIndex = 0;

        for (int i = 0; i < tips.length(); i++) {

            JSONObject object = tips.optJSONObject(i);

            if (object == null) {
                continue;
            }

            names.add(
                    object.optString("title", "Untitled")
                            + "  ·  "
                            + object.optString("status", "Draft")
            );

            if (object.optLong("id") == selectId) {
                selectedIndex = names.size() - 1;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
        );

        spinnerTips.setAdapter(adapter);

        spinnerTips.setSelection(selectedIndex);
    }


    private void fillForm(JSONObject object) {

        currentId = object.optLong("id", -1);
        currentStatus = object.optString("status", "Draft");

        etTitle.setText(object.optString("title", ""));
        etBody.setText(object.optString("body", ""));

        spinnerCategory.setSelection(
                categoryIndex(object.optString("category", CATEGORIES[0]))
        );

        imagePath = object.optString("image", "");
        imageName = object.optString("imageName", "");

        showImage();

        updatePreview();
    }


    private void resetForm() {

        currentId = -1;
        currentStatus = "Draft";

        etTitle.setText("");
        etBody.setText("");

        spinnerCategory.setSelection(0);

        imagePath = "";
        imageName = "";

        showImage();

        updatePreview();
    }


    private int categoryIndex(String category) {

        for (int i = 0; i < CATEGORIES.length; i++) {

            if (CATEGORIES[i].equalsIgnoreCase(category)) {
                return i;
            }
        }

        return 0;
    }


    private void handleImage(Uri uri) {

        try {

            File dir = new File(getFilesDir(), "tips");

            dir.mkdirs();

            File out = new File(
                    dir,
                    "tip_" + System.currentTimeMillis() + ".jpg"
            );

            try (InputStream in = getContentResolver().openInputStream(uri);
                 OutputStream os = new FileOutputStream(out)) {

                byte[] buffer = new byte[8192];

                int read;

                while ((read = in.read(buffer)) > 0) {
                    os.write(buffer, 0, read);
                }
            }

            imagePath = out.getAbsolutePath();
            imageName = displayName(uri);

            showImage();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Could not load image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private String displayName(Uri uri) {

        String name = "image.jpg";

        try (Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        )) {

            if (cursor != null && cursor.moveToFirst()) {

                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (index >= 0) {
                    name = cursor.getString(index);
                }
            }

        } catch (Exception ignored) {
        }

        return name;
    }


    private void showImage() {

        if (!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {

            imgPreview.setImageURI(Uri.fromFile(new File(imagePath)));
            imgPreview.setVisibility(View.VISIBLE);

            tvImageName.setText(
                    TextUtils.isEmpty(imageName)
                            ? new File(imagePath).getName()
                            : imageName
            );

        } else {

            imgPreview.setImageDrawable(null);
            imgPreview.setVisibility(View.GONE);

            tvImageName.setText("No image selected");
        }
    }


    private void updatePreview() {

        if (previewTitle == null || spinnerCategory == null) {
            return;
        }

        String title = etTitle.getText().toString().trim();
        String body = etBody.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem() == null
                ? CATEGORIES[0]
                : spinnerCategory.getSelectedItem().toString();

        previewTitle.setText(
                title.isEmpty() ? "Your tip title" : title
        );

        previewBody.setText(
                body.isEmpty() ? "Your description will appear here." : body
        );

        previewCategory.setText(category);

        previewStatus.setText(currentStatus);

        previewTile.setText(
                category.substring(0, 1).toUpperCase()
        );
    }


    private void saveTip(String status) {

        String title = etTitle.getText().toString().trim();
        String body = etBody.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {

            etTitle.setError("Please enter a title");

            return;
        }

        if (status.equals("Published") && TextUtils.isEmpty(body)) {

            etBody.setError("Please enter a description before publishing");

            return;
        }

        try {

            long id = currentId == -1
                    ? System.currentTimeMillis()
                    : currentId;

            JSONObject object = new JSONObject();

            object.put("id", id);
            object.put("title", title);
            object.put("body", body);
            object.put("category", spinnerCategory.getSelectedItem().toString());
            object.put("image", imagePath);
            object.put("imageName", imageName);
            object.put("status", status);
            object.put("updatedAt", System.currentTimeMillis());

            int existingIndex = -1;

            for (int i = 0; i < tips.length(); i++) {

                JSONObject existing = tips.optJSONObject(i);

                if (existing != null && existing.optLong("id") == id) {
                    existingIndex = i;
                    break;
                }
            }

            if (existingIndex >= 0) {
                tips.put(existingIndex, object);
            } else {
                tips.put(object);
            }

            storage.edit()
                    .putString("tips", tips.toString())
                    .apply();

            currentId = id;
            currentStatus = status;

            refreshTipSpinner(id);

            updatePreview();

            Toast.makeText(
                    this,
                    status.equals("Published")
                            ? "Health tip published"
                            : "Draft saved",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Could not save health tip",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private abstract static class SimpleWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(
                CharSequence s,
                int start,
                int count,
                int after
        ) {
        }

        @Override
        public void onTextChanged(
                CharSequence s,
                int start,
                int before,
                int count
        ) {
        }
    }
}