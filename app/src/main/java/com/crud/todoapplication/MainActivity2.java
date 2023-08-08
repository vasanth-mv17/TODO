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

/**
 * <p>
 * Representing the main activity2 of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class MainActivity2 extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private TableLayout tableLayout;
    private EditText editText;
    private String selectedList;

    /**
     * <p>
     * Creation of the main activity 2
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        selectedList = getIntent().getStringExtra("List Reference");

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

        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
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
        final String todoItem = editText.getText().toString();
        final TableRow tableRow = new TableRow(MainActivity2.this);
        final CheckBox checkBox = new CheckBox(MainActivity2.this);
        final TextView todoView = new TextView(MainActivity2.this);
        final ImageView closeIcon = new ImageView(MainActivity2.this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow.addView(checkBox);
        todoView.setText(todoItem);
        tableRow.addView(todoView);
        closeIcon.setImageResource(R.drawable.baseline_close_24);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                removeItem(tableRow);
            }
        });

        tableRow.addView(closeIcon);
        tableLayout.addView(tableRow);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    todoView.setTextColor(ContextCompat.getColor(MainActivity2.this, android.R.color.darker_gray));
                } else {
                    todoView.setTextColor(Color.BLACK);
                }
            }
        });
        editText.getText().clear();
        saveTodoList(selectedList);
    }

    /**
     * <p>
     * Load saved items associated with a specific list name from shared preferences
     * </p>
     *
     * @param listName Refers name of the todo list from which to load items
     */
    private void loadTodoList(String listName) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String savedTodoItems = sharedPreferences.getString(listName, "");
        final String[] todoItems = savedTodoItems.split(",");

        for (final String todoItem : todoItems) {
            if (!todoItem.isEmpty()) {
                editText.setText(todoItem);
                addNewTodoItem();
            }
        }
    }

    /**
     * <p>
     * Saved the list of Items on shared preferences
     * </p>
     *
     * @param listName Refers name of the todo list to associate with the saved items
     */
    private void saveTodoList(final String listName) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final StringBuilder todoItems = new StringBuilder();

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            final TableRow row = (TableRow) tableLayout.getChildAt(i);
            final TextView todoView = (TextView) row.getChildAt(1);
            final String Item = todoView.getText().toString();
            todoItems.append(Item).append(",");
        }

        editor.putString(listName, todoItems.toString());
        editor.apply();
    }

    private void deleteList(String listName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(listName);
        editor.apply();
    }

    /**
     * <p>
     * Remove the items on the table layout
     * </p>
     *
     * @param tableRow Refers the table row for the deletion
     */
    private void removeItem(final TableRow tableRow) {
        tableLayout.removeView(tableRow);
        deleteList(selectedList);
    }
}
