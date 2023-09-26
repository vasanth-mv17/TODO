package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.model.Project;

import java.util.Collections;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private final List<Project> projects;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(int position);
        void onRemoveItem(int position);
        void onUpdateItem(final Project fromProject, final Project toProject);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ProjectAdapter(final List<Project> projects) {
        this.projects = projects;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Project project = projects.get(position);
        final Typeface typeface = FontManager.getCurrentTypeface();
        float textSize = FontManager.getCurrentFontSize();

        if (null != typeface) {
            holder.projectNameTextView.setTypeface(typeface);
        } else if (0 != textSize) {
            holder.projectNameTextView.setTextSize(textSize);
        }
        holder.projectNameTextView.setText(project.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int position = holder.getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProjects() {
        projects.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addProjects(final List<Project> newProjects) {
        projects.addAll(newProjects);
        notifyDataSetChanged();
    }

    public void onItemMove(final int fromPosition, final int toPosition) {
        final Project fromProject = projects.get(fromPosition);
        final Project toProject = projects.get(toPosition);

        Collections.swap(projects, fromPosition, toPosition);
        fromProject.setOrder((long) (toPosition + 1));
        toProject.setOrder((long) fromPosition + 1);
        notifyItemMoved(fromPosition, toPosition);
        onItemClickListener.onUpdateItem(fromProject, toProject);

    }
    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectNameTextView;
        ImageButton removeButton;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.projectNameTextView);
            removeButton = itemView.findViewById(R.id.remove_project);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && null != onItemClickListener) {
                        onItemClickListener.onRemoveItem(position);
                        final Project project = projects.get(position);

                        if (projects.contains(project)) {
                            projects.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                }
            });
        }
    }
}
