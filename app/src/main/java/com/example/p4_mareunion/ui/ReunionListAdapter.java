package com.example.p4_mareunion.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p4_mareunion.R;
import com.example.p4_mareunion.eventListener.ItemClickListener;
import com.example.p4_mareunion.model.Reunion;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ReunionListAdapter extends RecyclerView.Adapter<ReunionListAdapter.ReunionViewHolder> {

    private List<Reunion> reunions;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reunion, parent, false);
        return new ReunionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReunionViewHolder holder, int position) {
        Reunion reunion = reunions.get(position);

        ImageView avatar = holder.itemView.findViewById(R.id.avatarReunion);
        TextView room = holder.itemView.findViewById(R.id.room);
        TextView subject= holder.itemView.findViewById(R.id.subject) ;
        TextView participants = holder.itemView.findViewById(R.id.participants);
        TextView time = holder.itemView.findViewById(R.id.time);

        String timeString = getTimeString(reunion.getTime());
        String participantsString = getParticipantsAsString(reunion.getParticipants());

        avatar.setBackgroundResource(getRandomOvalShape());
        room.setText(reunion.getRoom());
        subject.setText(reunion.getSubject());
        time.setText(timeString);
        participants.setText(participantsString);
    }


    @Override
    public int getItemCount() {
        return reunions.size();
    }

    // RecyclerView
    public class ReunionViewHolder extends RecyclerView.ViewHolder {

        public ReunionViewHolder(View view) {
            super(view);
            ImageView deleteButton = view.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                deleteClickListener.onClickDeleteReunion(reunions.get(position));
            });
        }
    }

    private int getRandomOvalShape() {
        Random random = new Random();
        int i = random.nextInt(4);
        switch (i) {
            case 0:
                return R.drawable.oval_shape_1;
            case 1:
                return R.drawable.oval_shape_2;
            case 2:
                return R.drawable.oval_shape_3;
            case 3:
                return R.drawable.oval_shape_4;
        }
        return R.drawable.oval_shape_1;
    }

    private String getTimeString(Time time){
        String timeString = new SimpleDateFormat("HH'H'mm", Locale.getDefault()).format(time);
        return timeString;
    }

    private String getParticipantsAsString(List<String> participants){
        if (participants != null){
            StringBuilder builder = new StringBuilder();
            for (String participant : participants){
                builder.append(participant).append(", ");
            }
            String participantsString = builder.toString();
            return participantsString;
        }else{
            return "";
        }
    }
}
