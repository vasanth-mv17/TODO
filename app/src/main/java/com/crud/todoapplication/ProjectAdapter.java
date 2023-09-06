package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.model.Project;

import java.util.Collections;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private List<Project> projects;
    private Context context;
    private DatabaseConnection databaseConnection;

    public ProjectAdapter(final List<Project> projects, final Context context, final DatabaseConnection databaseConnection) {
        this.projects = projects;
        this.context = context;
        this.databaseConnection = databaseConnection;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Project project = projects.get(position);
        Typeface typeface = FontManager.getCurrentTypeface();
        float textSize = FontManager.getCurrentFontSize();

        holder.projectNameTextView.setTextSize(textSize);
        holder.projectNameTextView.setTypeface(typeface);
        holder.projectNameTextView.setText(project.getLabel());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(context, ProjectTodoItemActivity2.class);
                intent.putExtra(String.valueOf(R.string.ProjectId), project.getId());
                intent.putExtra(String.valueOf(R.string.ProjectName), project.getLabel());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectNameTextView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.projectNameTextView);
        }
    }

    public void onItemMove(final int fromPosition, final int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(projects, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(projects, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
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
}
