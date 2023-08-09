package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.TodoList;

/**
 * <p>
 * Representing the main activity2 of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class SubListActivity extends AppCompatActivity {

    private static  final String NAME = "TodoListPREF";
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private TableLayout tableLayout;
    private EditText editText;
    private String selectedList;
    private TodoList todoList;

    /**
     * <p>
     * Creation of the sub list activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
        selectedList = getIntent().getStringExtra("List Reference");
        todoList = new TodoList(selectedList);

        if (null != selectedList) {
            TextView textView = findViewById(R.id.listName);
            textView.setText(selectedList);
        }
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });

        final Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addNewTodoItem();
            }
        });

        loadTodoList(selectedList);
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    public void addNewTodoItem() {
        String todoItem = editText.getText().toString();
        if (!todoItem.isEmpty()) {
            Project project = new Project(todoItem);
            todoList.add(project);
            updateTableLayout();
            saveTodoList();
            editText.getText().clear();
        }
    }

    /**
     * <p>
     * Update the tableLayout with todo items, checkboxes and close icons
     * </p>
     */
    private void updateTableLayout() {
        tableLayout.removeAllViews();

        for (final Project project : todoList.getAll()) {
            final TableRow tableRow = new TableRow(SubListActivity.this);
            final CheckBox checkBox = new CheckBox(SubListActivity.this);
            final TextView todoView = new TextView(SubListActivity.this);
            final ImageView closeIcon = new ImageView(SubListActivity.this);

            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            tableRow.addView(checkBox);
            todoView.setText(project.getLabel());
            tableRow.addView(todoView);
            closeIcon.setImageResource(R.drawable.baseline_close_24);
            closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(project);
                }
            });

            tableRow.addView(closeIcon);
            tableLayout.addView(tableRow);
            checkBox.setChecked(getCheckedBoxBehaviour(project.getLabel()));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {

                    if(isChecked) {
                    todoView.setTextColor(ContextCompat.getColor(SubListActivity.this, android.R.color.darker_gray));
                    } else {
                    todoView.setTextColor(Color.BLACK);
                    }
                    saveCheckBoxState(project.getLabel(), isChecked);
                }
            });
        }
    }

    private boolean getCheckedBoxBehaviour(String label) {
        final SharedPreferences sharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(label, false);
    }

    private void saveCheckBoxState(String label, boolean isChecked) {
        final SharedPreferences sharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(label, isChecked);
        editor.apply();
    }


    /**
     * <p>
     * Load saved items with a specific list name from shared preferences
     * </p>
     *
     * @param listName Refers name of the todo list from which to load items
     */
    private void loadTodoList(final String listName) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String savedTodoItems = sharedPreferences.getString(listName, "");
        final String[] todoItems = savedTodoItems.split(",");

        for (final String todoItem : todoItems) {
            if (!todoItem.isEmpty()) {
                final Project project = new Project(todoItem);
                todoList.add(project);
            }
        }
        updateTableLayout();
    }

    /**
     * <p>
     * Saved the list of items on shared preferences
     * </p>
     */
    private void saveTodoList() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final StringBuilder todoItems = new StringBuilder();

        for (final Project project : todoList.getAll()) {
            todoItems.append(project.getLabel()).append(",");
        }

        editor.putString(selectedList, todoItems.toString());
        editor.apply();
    }

    /**
     * <p>
     * Remove a Todo item from the list, update the TableLayout, and save the updated list
     * </p>
     *
     * @param project The Todo item to be removed
     */
    private void removeItem(final Project project) {
        todoList.remove(project.getId());
        updateTableLayout();
        saveTodoList();
    }
}
