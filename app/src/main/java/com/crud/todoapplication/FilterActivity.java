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
import com.crud.todoapplication.model.ProjectTodoList;
import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.service.ProjectView;

import java.util.List;

public class FilterActivity extends AppCompatActivity implements ProjectView {

    private static final String NAME = "CheckBoxState";
    private DrawerLayout drawerLayout;
    private TableLayout tableLayout;
    private EditText editText;
    private ImageButton menuButton;
    private SearchView searchButton;
    private String selectedList;
    private ProjectTodoList projectTodoList;
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
        selectedList = getIntent().getStringExtra("List Reference");
        projectTodoList = new ProjectTodoList(selectedList);

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
                final ProjectListActivity.Filter selectedFilter = ProjectListActivity.Filter.values()[filterSpinner.getSelectedItemPosition()];

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
                        for (final TodoItem todoItem : projectTodoList.getAll()) {
                            if (todoItem.isChecked()) {
                                addTodoItem(todoItem);
                            }
                        }
                        break;
                    case UNCHECKED:
                        tableLayout.removeAllViews();
                        for (final TodoItem todoItem : projectTodoList.getAll()) {
                            if (!todoItem.isChecked()) {
                                addTodoItem(todoItem);
                            }
                        }
                        break;
                    default:
                        tableLayout.removeAllViews();
                        for (final TodoItem todoItem : projectTodoList.getAll()) {
                            addTodoItem(todoItem);
                        }
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parentView) {
            }
        });
        ArrayAdapter<CharSequence> pageSpinnerAdapter = ArrayAdapter.createFromResource(
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
                navigateToPage(currentPage - 1);
                updateTableLayout();
                updatePageNumber(pageNumber);
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPage(currentPage + 1);
                updateTableLayout();
                updatePageNumber(pageNumber);
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
        loadTodoList(selectedList);
        updatePageNumber(pageNumber);
        pageButtonState();
    }

    /**
     * <p>
     * Navigates to the target page
     * </p>
     *
     * @param targetPage The target page number to navigate to.
     */
    private void navigateToPage(final int targetPage) {
        if (targetPage >= 1 && targetPage <= getTotalPages()) {
            currentPage = targetPage;
            updateTableLayout();
        }
    }

    /**
     * <p>
     * Calculates the total pages required for the todo items
     * </p>
     *
     * @return Returns the total pages for the project
     */
    private int getTotalPages() {
        return (int) Math.ceil((double) todoItems.size() / pageCapacity);
    }

    /**
     * <p>
     * Handles the page button state
     * </p>
     */
    public void pageButtonState() {
        final int totalPages = getTotalPages();

        if (currentPage == 1) {
            previousPageButton.setEnabled(false);
            previousPageButton.setColorFilter(Color.GRAY);
        } else {
            previousPageButton.setEnabled(true);
            previousPageButton.setColorFilter(null);
        }

        if (currentPage == totalPages) {
            nextPageButton.setEnabled(false);
            nextPageButton.setColorFilter(Color.GRAY);
        } else {
            nextPageButton.setEnabled(true);
            nextPageButton.setColorFilter(null);
        }

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

        for (final TodoItem todoItem : projectTodoList.getAll()) {
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
        todoItems = projectTodoList.getAll();

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
        todoItems = projectTodoList.getAll();

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
        final TableRow tableRow = new TableRow(FilterActivity.this);
        final CheckBox checkBox = new CheckBox(FilterActivity.this);
        final TextView label = new TextView(FilterActivity.this);
        final ImageView closeIcon = new ImageView(FilterActivity.this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow.addView(checkBox);
        label.setText(todoItem.getLabel());
        tableRow.addView(label);
        closeIcon.setImageResource(R.drawable.baseline_close_24);

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                removeItem(todoItem);
            }
        });

        tableRow.addView(closeIcon);
        tableLayout.addView(tableRow);
        checkBox.setChecked(getCheckedBox(todoItem.getLabel()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                label.setTextColor(isChecked ? ContextCompat.getColor(FilterActivity.this, android.R.color.darker_gray) : Color.BLACK);
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
            projectTodoList.add(todoItem);
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
        todoItems = projectTodoList.getAll();
        final int startIndex = (currentPage - 1) * pageCapacity;
        final int endIndex = Math.min(startIndex + pageCapacity, todoItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final TodoItem todoItem = todoItems.get(i);
            addTodoItem(todoItem);
        }
        pageButtonState();
    }

    /**
     * <p>
     * Updates the page number in the layout
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
     * @param label     Refers the  label with the checkbox
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
                projectTodoList.add(project);
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

        for (final TodoItem todoItem : projectTodoList.getAll()) {
            todoItems.append(todoItem.getLabel()).append(",");
        }
        editor.putString(selectedList, todoItems.toString());
        editor.apply();
    }

    /**
     * <p>
     * Remove a Todo item from the list, update the tableLayout, and save the updated list
     * </p>
     *
     * @param todoItem Refer the Todo item to be removed
     */
    private void removeItem(final TodoItem todoItem) {
        projectTodoList.remove(todoItem.getId());
        updateTableLayout();
        saveTodoList();
    }

}
