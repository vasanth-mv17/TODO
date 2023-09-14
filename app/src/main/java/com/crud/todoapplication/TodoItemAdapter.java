package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.model.TodoList;

import java.util.Collections;
import java.util.List;

public class TodoItemAdapter extends RecyclerView.Adapter<TodoItemAdapter.ViewHolder> {

    private List<TodoItem> todoItems;
    private Context context;
    private DatabaseConnection databaseConnection;
    private TodoList todoList;
   // private OnItemClickListener listener;

    public TodoItemAdapter(List<TodoItem> todoItems, Context context, DatabaseConnection databaseConnection, TodoList todoList) {
        this.todoItems = todoItems;
        this.context = context;
        this.databaseConnection = databaseConnection;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

//    public void setOnClickListener(final OnItemClickListener listener) {
//        this.listener = listener;
//    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);
        Typeface typeface = FontManager.getCurrentTypeface();
        //holder.bind(todoItem,listener);
        holder.itemText.setTypeface(typeface);
        holder.bind(todoItem, databaseConnection);

        holder.removeButton.setOnClickListener(v -> {
            todoItems.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public void onItemMove(final int fromPosition, final int toPosition) {
//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(todoItems, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(todoItems, i, i - 1);
//            }
//        }
//        notifyItemMoved(fromPosition, toPosition);

        final TodoItem fromList = todoItems.get(fromPosition);
        final TodoItem toList = todoItems.get(toPosition);

        Collections.swap(todoItems, fromPosition, toPosition);
        fromList.setOrder((long) (toPosition + 1));
        toList.setOrder((long) (fromPosition + 1));
        notifyItemMoved(fromPosition, toPosition);
        databaseConnection.updateTodoOrder(fromList);
        databaseConnection.updateTodoOrder(toList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView itemText;
        public ImageView removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemText = itemView.findViewById(R.id.itemText);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        public void bind(final TodoItem todoItem, final DatabaseConnection databaseConnection) {
            itemText.setText(todoItem.getLabel());
            checkBox.setChecked(todoItem.getStatus() == TodoItem.Status.CHECKED);
            itemText.setTextColor(todoItem.getStatus() == TodoItem.Status.CHECKED ? Color.GRAY : Color.BLACK);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int position = todoItems.indexOf(todoItem);

                    if (-1 != position) {
                        final TodoItem updatedItem = todoItems.get(position);

                        updatedItem.setStatus(updatedItem.getStatus() == TodoItem.Status.CHECKED ? TodoItem.Status.UNCHECKED : TodoItem.Status.CHECKED);
                        databaseConnection.update(todoItem);
                        notifyItemChanged(position);
                    }
                }
            });
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = todoItems.indexOf(todoItem);

                    if (-1 != position) {
                        final TodoItem removeItem = todoItems.get(position);

                        todoList.remove(todoItem.getId());
                        databaseConnection.delete(removeItem.getId());
                        notifyItemChanged(position);
                    }
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTodoItems(List<TodoItem> updatedItems) {
        this.todoItems = updatedItems;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProjects() {
        todoItems.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addProjects(final List<TodoItem> newProjects) {
        todoItems.addAll(newProjects);
        notifyDataSetChanged();
    }
}

