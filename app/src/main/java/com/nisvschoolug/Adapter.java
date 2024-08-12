package com.nisvschoolug;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.VH> {
    private List<Model> stories;

    private LayoutInflater inflater;
    Context context;

    public Adapter(Context context, List<Model> stories){
        this.context=context;
        this.stories=stories;
        inflater = LayoutInflater.from(context);
    }
    public void filterList(List<Model> filterList) {
        stories.clear();
        stories.addAll(filterList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout,parent,false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        Model story = stories.get(position);
        holder.titleTextView.setText(story.getTitle());
        String img = story.getImage();
        Glide.with(context).load(img).into(holder.iv_layout);

        holder.cv_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreen.courseID = stories.get(position).getId();
                Intent intent = new Intent(context, HomeScreen.class);
                intent.putExtra("story_id", story.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        TextView titleTextView;
        ImageView iv_layout;
        CardView cv_course;
        public VH(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_title);
            cv_course = itemView.findViewById(R.id.cv_course);
            iv_layout = itemView.findViewById(R.id.iv_layout);
        }
    }
}
