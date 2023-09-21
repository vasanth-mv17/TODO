package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private OnItemClickListener listener;

    public interface OnItemClickListener {

        void onCheckBoxClick(TodoItem todoItem);
        void onCloseIconClick(TodoItem todoItem);
        void onItemUpdate(TodoItem fromItem, TodoItem toItem);
    }

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

    public void setOnClickListener(final OnItemClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);
        Typeface typeface = FontManager.getCurrentTypeface();
        //holder.bind(todoItem,listener);
        holder.itemText.setTypeface(typeface);
        holder.bind(todoItem, listener);

//        holder.removeButton.setOnClickListener(v -> {
//            todoItems.remove(position);
//            notifyDataSetChanged();
//        });
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
        listener.onItemUpdate(fromList, toList);
        notifyItemMoved(fromPosition, toPosition);
//        databaseConnection.updateTodoOrder(fromList);
//        databaseConnection.updateTodoOrder(toList);
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

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    final TodoItem todoItem = todoItems.get(position);

                    todoItem.setChecked();
                    todoItem.setStatus(isChecked ? TodoItem.Status.UNCHECKED
                            : TodoItem.Status.CHECKED);
                    itemText.setTextColor(todoItem.getStatus() == TodoItem.Status.CHECKED
                            ? Color.GRAY : Color.BLACK);
                    listener.onCheckBoxClick(todoItem);
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        final TodoItem todoItem = todoItems.get(position);

                        todoItems.remove(todoItem);
                        notifyItemRemoved(position);
                        listener.onCloseIconClick(todoItem);
                    }
                }
            });
        }

        public void bind(final TodoItem todoItem, final OnItemClickListener listener) {
            itemText.setText(todoItem.getName());
            checkBox.setChecked(todoItem.getStatus() == TodoItem.Status.CHECKED);
            itemText.setTextColor(todoItem.getStatus() == TodoItem.Status.CHECKED ? Color.GRAY : Color.BLACK);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCheckBoxClick(todoItem);
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCloseIconClick(todoItem);
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

