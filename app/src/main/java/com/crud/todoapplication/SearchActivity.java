package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.TodoList;

public class SearchActivity extends AppCompatActivity {

    private static  final String NAME = "TodoListPREF";
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private SearchView searchButton;
    private TableLayout tableLayout;
    private EditText editText;
    private String selectedList;
    private TodoList todoList;
    private Spinner filterSpinner;

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
        setContentView(R.layout.activity_search);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        searchButton = findViewById(R.id.searchView);
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
        searchButton.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterAndDisplayItems(newText);
                return true;
            }
        });
        final Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addNewTodoItem();
            }
        });

        filterSpinner = findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.filter_options, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0 :
                        tableLayout.removeAllViews();
                        for(final Project project : todoList.getAll()) {
                            addTodoItem(project);
                        }
                        break;
                    case 1 :
                        tableLayout.removeAllViews();
                        for(final Project project : todoList.getAll()) {
                           if (project.isChecked()) {
                               addTodoItem(project);
                           }
                        }
                        break;
                    case 2 :
                        tableLayout.removeAllViews();
                        for(final Project project : todoList.getAll()) {
                            if (!project.isChecked()) {
                                addTodoItem(project);
                            }
                        }
                        break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        ImageButton filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (searchButton.getVisibility() == View.GONE) {
                    searchButton.setVisibility(View.VISIBLE);
                    filterSpinner.setVisibility(View.VISIBLE);
                } else {
                    searchButton.setVisibility(View.GONE);
                    filterSpinner.setVisibility(View.GONE);
                }
            }
        });
        loadTodoList(selectedList);
    }


    private void filterAndDisplayItems(final String query) {
        tableLayout.removeAllViews();

        for (final Project project : todoList.getAll()) {
            if (project.getLabel().toLowerCase().contains(query.toLowerCase())) {
                addTodoItem(project);
            }
        }
    }

    public void addTodoItem(final Project project) {
        final TableRow tableRow = new TableRow(SearchActivity.this);
        final CheckBox checkBox = new CheckBox(SearchActivity.this);
        final TextView todoView = new TextView(SearchActivity.this);
        final ImageView closeIcon = new ImageView(SearchActivity.this);

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
        checkBox.setChecked(getCheckedBox(project.getLabel()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {

                if(isChecked) {
                    todoView.setTextColor(ContextCompat.getColor(SearchActivity.this, android.R.color.darker_gray));
                    project.setChecked();
                } else {
                    todoView.setTextColor(Color.BLACK);
                    project.setChecked();
                }
                saveCheckBox(project.getLabel(), isChecked);
            }
        });
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    public void addNewTodoItem() {
        final String todoItem = editText.getText().toString();
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
            addTodoItem(project);
        }
    }

    /**
     * <p>
     * Retrieve the checked state of a checkbox with a given label from shared preferences
     * </p>
     *
     * @param label The label with the checkbox
     * @return The checked state of the checkbox
     */
    private boolean getCheckedBox(final String label) {
        final SharedPreferences sharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(label, false);
    }

    /**
     * <p>
     * Save the checked state of a checkbox with a given label into shared preferences
     * </p>
     *
     * @param label The label with the checkbox.
     * @param isChecked The checked state to be saved.
     */
    private void saveCheckBox(final String label, final boolean isChecked) {
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

