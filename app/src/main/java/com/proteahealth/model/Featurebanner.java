package com.proteahealth.model;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.R;

public class Featurebanner extends RecyclerView.Adapter<Featurebanner.BannerViewHolder> {

    private final String[] titles;
    private final String[] descriptions;

    public Featurebanner() {
        this.titles = new String[]{
                "Medication Reminders",
                "Book Your Appointments",
                "Easy Prescription Refills"
        };
        this.descriptions = new String[]{
                "Never forget your medication with personalised reminders.",
                "Keep track of your doctor appointments and healthcare schedule.",
                "Upload your prescription and manage your pharmacy refills with ease."
        };
    }

    public Featurebanner(String[] titles, String[] descriptions) {
        this.titles = titles != null ? titles : new String[0];
        this.descriptions = descriptions != null ? descriptions : new String[0];
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (position < titles.length) {
            holder.title.setText(titles[position]);
        }
        if (position < descriptions.length) {
            holder.description.setText(descriptions[position]);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(titles.length, descriptions.length);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bannerTitle);
            description = itemView.findViewById(R.id.bannerDescription);
        }
    }
}

