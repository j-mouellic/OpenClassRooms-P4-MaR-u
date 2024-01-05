package com.example.p4_mareunion.ui;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p4_mareunion.R;
import com.example.p4_mareunion.databinding.ItemListMeetingBinding;
import com.example.p4_mareunion.eventListener.ItemClickListener;
import com.example.p4_mareunion.model.Reunion;

import java.util.List;
import java.util.Random;

public class ReunionListAdapter extends RecyclerView.Adapter<ReunionListAdapter.ReunionViewHolder> {

    private List<Reunion> reunions;
    private ItemListMeetingBinding itemListMeetingBinding;
    private ItemClickListener deleteClickListener;

    public ReunionListAdapter(ItemClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void setReunions(List<Reunion> reunions) {
        this.reunions = reunions;
    }

    @NonNull
    @Override
    public ReunionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemListMeetingBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_list_meeting,
                parent,
                false
        );
        return new ReunionViewHolder(itemListMeetingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReunionViewHolder holder, int position) {
        Reunion reunion = reunions.get(position);
        holder.itemListMeetingBinding.setReunion(reunion);
    }

    @Override
    public int getItemCount() {
        return reunions.size();
    }

    // RecyclerView
    public class ReunionViewHolder extends RecyclerView.ViewHolder {
        public ItemListMeetingBinding itemListMeetingBinding;

        public ReunionViewHolder(@NonNull ItemListMeetingBinding itemListMeetingBinding) {
            super(itemListMeetingBinding.getRoot());
            this.itemListMeetingBinding = itemListMeetingBinding;

            int randomColor = getColorResourceId(getRandomIndex());
            int color = ContextCompat.getColor(itemListMeetingBinding.imageView.getContext(), randomColor);
            itemListMeetingBinding.imageView.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            itemListMeetingBinding.deleteButton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                deleteClickListener.onClickDeleteReunion(reunions.get(position));
            });
        }
    }

    private int getRandomIndex() {
        Random random = new Random();
        return random.nextInt(4);
    }

    private int getColorResourceId(int index) {
        switch (index) {
            case 0:
                return R.color.blue_palette_1;
            case 1:
                return R.color.blue_palette_2;
            case 2:
                return R.color.blue_palette_3;
            case 3:
                return R.color.blue_palette_4;
        }
        return R.color.blue_palette_1;
    }
}
