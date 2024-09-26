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

import com.crud.todoapplication.controller.ProjectListController;
import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.model.TodoList;
import com.crud.todoapplication.service.ProjectView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * Representing the sub list activity of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class ProjectTodoItemActivity extends AppCompatActivity implements ProjectView {

    private static final String NAME = "CheckBoxState";
    private DrawerLayout drawerLayout;
    private TableLayout tableLayout;
    private EditText editText;
    private ImageButton menuButton;
    private SearchView searchButton;
    private String selectedProjectName;
    private Long selectedProjectId;
    private TodoList todoList;
    private ProjectListController projectListController;
    private List<TodoItem> todoItems;
    private Spinner filterSpinner;
    private Spinner filterSpinnerPage;
    private TextView pageNumber;
    private ImageButton previousPageButton;
    private ImageButton nextPageButton;
    private ImageButton filterButton;
    private int currentPage = 1;
    private int pageCapacity = 10;
    private static Long id = 0L;
    private boolean isAscending = true;

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

        projectListController = new ProjectListController(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        searchButton = findViewById(R.id.searchView);
        filterButton = findViewById(R.id.filter_button);
        filterSpinner = findViewById(R.id.filterSpinner);
        filterSpinnerPage = findViewById(R.id.filterSpinnerPage);
        previousPageButton = findViewById(R.id.prev_page);
        nextPageButton = findViewById(R.id.next_page);
        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
        pageNumber = findViewById(R.id.pageCount);
        selectedProjectId = getIntent().getLongExtra("Project Id", 0L);
        selectedProjectName = getIntent().getStringExtra("Project name");
        todoList = new TodoList();
        todoItems = todoList.getAllItems(selectedProjectId);

        if (null != selectedProjectName) {
            TextView textView = findViewById(R.id.listName);
            textView.setText(selectedProjectName);
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
                projectListController.onAddButtonClick();
            }
        });

        searchButton.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                projectListController.onSearchAndDisplayItems(newText);
                final Filter selectedFilter = Filter.values()[filterSpinner.getSelectedItemPosition()];

                switch (selectedFilter) {
                    case ALL:
                        filterAndDisplayItems(newText);
                        break;
                    case CHECKED:
                        displayCheckedItems(newText);
                        break;
                    case UNCHECKED:
                        displayUncheckedItems(newText);
                        break;
                }
                return true;
            }
        });
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.filter_options, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id) {
                final Filter selectedFilter = Filter.values()[position];

                switch (selectedFilter) {
                    case CHECKED:
                        tableLayout.removeAllViews();
                        for (final TodoItem todoItem : todoList.getAllItems()) {
                            if (todoItem.isChecked()) {
                                addTodoItem(todoItem);
                            }
                        }
                        break;
                    case UNCHECKED:
                        tableLayout.removeAllViews();
                        for (final TodoItem todoItem : todoList.getAllItems()) {
                            if (!todoItem.isChecked()) {
                                addTodoItem(todoItem);
                            }
                        }
                        break;
                    default:
                        tableLayout.removeAllViews();
                        for (final TodoItem todoItem : todoList.getAllItems()) {
                            addTodoItem(todoItem);
                        }
                }
            }
            @Override
            public void onNothingSelected(final AdapterView<?> parentView) {
            }
        });
        final ArrayAdapter<CharSequence> pageSpinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.page_options, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinnerPage.setAdapter(pageSpinnerAdapter);
        filterSpinnerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                pageCapacity = Integer.parseInt(adapterView.getItemAtPosition(i).toString());

                updateTableLayout();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });

        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 1) {
                    currentPage--;
                    updateTableLayout();
                    updatePageNumber(pageNumber);
                }

                if (currentPage == 1) {
                    previousPageButton.setEnabled(false);
                    previousPageButton.setColorFilter(Color.GRAY);
                } else {
                    previousPageButton.setEnabled(true);
                    previousPageButton.setColorFilter(null);
                }
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((currentPage * pageCapacity) < todoItems.size()) {
                    currentPage++;
                    updateTableLayout();
                    updatePageNumber(pageNumber);
                }

                if (currentPage == 1) {
                    previousPageButton.setEnabled(false);
                    previousPageButton.setColorFilter(Color.GRAY);
                } else {
                    previousPageButton.setEnabled(true);
                    previousPageButton.setColorFilter(null);
                }
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (searchButton.getVisibility() == View.GONE) {
                    searchButton.setVisibility(View.VISIBLE);
                    filterSpinner.setVisibility(View.VISIBLE);
                    filterSpinnerPage.setVisibility(View.VISIBLE);
                } else {
                    searchButton.setVisibility(View.GONE);
                    filterSpinner.setVisibility(View.GONE);
                    filterSpinnerPage.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton sortAscendingButton = findViewById(R.id.sortAscendingButton);
        ImageButton sortDescendingButton = findViewById(R.id.sortDescendingButton);

        sortAscendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAscending = true;
                updateTableLayout();
            }
        });

        sortDescendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAscending = false;
                updateTableLayout();
            }
        });

        loadTodoList(selectedProjectName);
        updatePageNumber(pageNumber);
    }

    /**
     * <p>
     * Enum for filter content
     * </p>
     */
    public enum Filter {
        ALL(),
        CHECKED(),
        UNCHECKED()

    }

    /**
     * <p>
     * Displays the checked items of the list
     * </p>
     *
     * @param query Refer the query to filter items by search
     */
    private void displayCheckedItems(final String query) {
        tableLayout.removeAllViews();

        for (final TodoItem todoItem : todoList.getAllItems()) {
            if (todoItem.isChecked() && todoItem.getLabel().toLowerCase().contains(query.toLowerCase())) {
                addTodoItem(todoItem);
            }
        }
    }

    /**
     * <p>
     * Displays the Unchecked items of the list
     * </p>
     *
     * @param query Refer the query to filter items by search
     */
    private void displayUncheckedItems(final String query) {
        tableLayout.removeAllViews();
        todoItems = todoList.getAllItems();

        for (final TodoItem todoItem : todoItems) {
            if (!todoItem.isChecked() && todoItem.getLabel().toLowerCase().contains(query.toLowerCase())) {
                addTodoItem(todoItem);
            }
        }
    }

    /**
     * <p>
     * Filter and display items in the tableLayout based on a given query
     * </p>
     *
     * @param query Refer the query to filter items by search
     */
    public void filterAndDisplayItems(final String query) {
        tableLayout.removeAllViews();
        todoItems = todoList.getAllItems();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getLabel().toLowerCase().contains(query.toLowerCase())) {
                addTodoItem(todoItem);
            }
        }
    }

    /**
     * <p>
     * Add a new todo item to the TableLayout based on the provided project
     * </p>
     *
     * @param todoItem Refer the Project containing the todo item information
     */
    public void addTodoItem(final TodoItem todoItem) {
        final TableRow tableRow = new TableRow(ProjectTodoItemActivity.this);
        final CheckBox checkBox = new CheckBox(ProjectTodoItemActivity.this);
        final TextView label = new TextView(ProjectTodoItemActivity.this);
        final ImageView closeIcon = new ImageView(ProjectTodoItemActivity.this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow.addView(checkBox);
        label.setText(todoItem.getLabel());
        tableRow.addView(label);
        closeIcon.setImageResource(R.drawable.baseline_close_24);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                removeItem(tableRow, todoItem);
            }
        });

        tableRow.addView(closeIcon);
        tableLayout.addView(tableRow);
        checkBox.setChecked(getCheckedBox(todoItem.getLabel()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                label.setTextColor(isChecked ? ContextCompat.getColor(ProjectTodoItemActivity.this, android.R.color.darker_gray) : Color.BLACK);
                todoItem.setChecked();
                saveCheckBox(todoItem.getLabel(), isChecked);
            }
        });
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    public void addNewTodoItem() {
        final String todoLabel = editText.getText().toString();
        if (!todoLabel.isEmpty()) {
            TodoItem todoItem = new TodoItem(todoLabel);
            todoItem.setParentId(selectedProjectId);
            todoItem.setId(++id);
            todoList.add(todoItem);
            todoItems = todoList.getAllItems(selectedProjectId);

            updateTableLayout();
            updatePageNumber(pageNumber);
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
        todoItems = todoList.getAllItems();

        Collections.sort(todoItems, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem item1, TodoItem item2) {
                if (isAscending) {
                    return item1.getLabel().compareToIgnoreCase(item2.getLabel());
                } else {
                    return item2.getLabel().compareToIgnoreCase(item1.getLabel());
                }
            }
        });

        final int startIndex = (currentPage - 1) * pageCapacity;
        final int endIndex = Math.min(startIndex + pageCapacity, todoItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final TodoItem todoItem = todoItems.get(i);
            addTodoItem(todoItem);
        }
    }

    /**
     * <p>
     *  Updates the page number in the layout
     * </p>
     *
     * @param pageNumber Represents the layout view of the page number
     */
    @SuppressLint("DefaultLocale")
    private void updatePageNumber(final TextView pageNumber) {
        final int totalPageCount = (int) Math.ceil((double) todoItems.size() / pageCapacity);

        pageNumber.setText(String.format("%d / %d", currentPage, totalPageCount));
    }

    /**
     * <p>
     * Retrieve the checked state of a checkbox with a given label from shared preferences
     * </p>
     *
     * @param label Refer the label with the checkbox
     * @return Returns checked state of the checkbox
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
     * @param label Refers the  label with the checkbox
     * @param isChecked Refer the checked state to be saved
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
                final TodoItem project = new TodoItem(todoItem);
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

        for (final TodoItem todoItem : todoList.getAllItems()) {
            todoItems.append(todoItem.getLabel()).append(",");
        }
        editor.putString(selectedProjectName, todoItems.toString());
        editor.apply();
    }

    /**
     * <p>
     * Remove a Todo item from the list, update the tableLayout, and save the updated list
     * </p>
     *
     * @param todoItem Refer the Todo item to be removed
     */
    private void removeItem(final TableRow tableRow, final TodoItem todoItem) {
        tableLayout.removeView(tableRow);
        todoList.remove(todoItem.getId());

        int totalPageCount = (int) Math.ceil((double) todoItems.size()/ pageCapacity);
        if (currentPage > totalPageCount) {
            currentPage = totalPageCount;
        }
        updateTableLayout();
        updatePageNumber(pageNumber);
        saveTodoList();
    }
}