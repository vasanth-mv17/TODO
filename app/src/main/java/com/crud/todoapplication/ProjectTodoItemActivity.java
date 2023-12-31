package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.crud.todoapplication.controller.ProjectListController;
import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.model.TodoList;
import com.crud.todoapplication.service.ProjectView;

import java.util.ArrayList;
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
    private Spinner filterSpinner;
    private Spinner filterSpinnerPage;
    private TextView pageNumber;
    private TextView textView;
    private ImageButton previousPageButton;
    private ImageButton nextPageButton;
    private ImageButton filterButton;
    private Button addButton;

    private String selectedProjectName;
    private Long selectedProjectId;
    private TodoList todoList;
    private DatabaseConnection databaseConnection;
    private com.crud.todoapplication.model.Filter filter;
    private ProjectListController projectListController;
    private List<TodoItem> todoItems;

    private int currentPage = 1;
    private int pageCapacity = 10;
    private static Long id = 0L;

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
        searchButton = findViewById(R.id.searchView);
        filterButton = findViewById(R.id.filter_button);
        addButton = findViewById(R.id.button);
        filterSpinner = findViewById(R.id.filterSpinner);
        filterSpinnerPage = findViewById(R.id.filterSpinnerPage);
        previousPageButton = findViewById(R.id.prev_page);
        nextPageButton = findViewById(R.id.next_page);
        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);
        pageNumber = findViewById(R.id.pageCount);
        textView = findViewById(R.id.listName);

        projectListController = new ProjectListController(this);
        selectedProjectId = getIntent().getLongExtra("Project Id", 0L);
        selectedProjectName = getIntent().getStringExtra("Project name");
        todoList = new TodoList();
        databaseConnection = new DatabaseConnection(this);
        filter = new com.crud.todoapplication.model.Filter();

        if (selectedProjectName != null) {
            textView.setText(selectedProjectName);
        }

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });

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
                //projectListController.onSearchAndDisplayItems(newText);
                final Status selectedStatus = Status.values()[filterSpinner.getSelectedItemPosition()];

                switch (selectedStatus) {
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
                refreshUpdateTableLayout();
                updatePageNumber(pageNumber);
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
                final Status selectedStatus = Status.values()[position];
                filter.setSearchAttribute(searchButton.getQuery().toString());

                switch (selectedStatus) {
                    case ALL:
                        filter.setAttribute("label");
                        break;
                    case CHECKED:
                        filter.setAttribute("isChecked");
                        break;
                    case UNCHECKED:
                        filter.setAttribute("unChecked");
                        break;
                }

                //todoItems = todoList.getAllFilterItems(selectedStatus);
                currentPage = 1;
                refreshUpdateTableLayout();
                updatePageNumber(pageNumber);
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

                refreshUpdateTableLayout();
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
                    refreshUpdateTableLayout();
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
                    refreshUpdateTableLayout();
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
        final ImageButton addShowButton = findViewById(R.id.addButton);

        addShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getVisibility() == View.GONE) {
                    editText.setVisibility(View.VISIBLE);
                    addButton.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                    addButton.setVisibility(View.GONE);
                }
            }
        });

//        if (null == todoItems ) {
//            currentPage = 0;
//            updatePageNumber(pageNumber);
//        }
        loadTodoItemsFromDatabase(selectedProjectId);

    }

    /**
     * <p>
     * Enum for filter content
     * </p>
     */
    public enum Status {
        ALL(),
        CHECKED(),
        UNCHECKED()

    }


    private void loadTodoItemsFromDatabase(final Long selectedProjectId) {
        todoItems = databaseConnection.getTodoItemsForProject(selectedProjectId);

        if (null != todoItems) {
//            for (TodoItem todoItem : todoItems) {
//                if (todoItem.isChecked()) {
//                    todoItem.setStatus("Completed");
//                } else {
//                    todoItem.setStatus("Not Completed");
//                }
//            }
            todoList.setAllItems(todoItems);
            refreshUpdateTableLayout();
            updatePageNumber(pageNumber);
        }
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
        filter.setSearchAttribute(query);
        todoItems = todoList.getAllItems();
        List<TodoItem> searchCheckedItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.isChecked() && todoItem.getName().toLowerCase().contains(filter.getSearchAttribute().toLowerCase())) {
                searchCheckedItems.add(todoItem);
            }
        }
        todoItems = searchCheckedItems;
        currentPage = 1;
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
        filter.setSearchAttribute(query);
        todoItems = todoList.getAllItems();
        List<TodoItem> searchUnCheckedItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (!todoItem.isChecked() && todoItem.getName().toLowerCase().contains(filter.getSearchAttribute().toLowerCase())) {
                searchUnCheckedItems.add(todoItem);
            }
        }
        todoItems = searchUnCheckedItems;
        currentPage = 1;
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
        filter.setSearchAttribute(query);
        todoItems = todoList.getAllItems();
        List<TodoItem> searchAllItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getName().toLowerCase().contains(filter.getSearchAttribute().toLowerCase())) {
                searchAllItems.add(todoItem);
            }
        }
        todoItems = searchAllItems;
        currentPage = 1;
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

        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        final CheckBox checkBox = new CheckBox(ProjectTodoItemActivity.this);

        checkBox.setChecked(todoItem.getStatus() == TodoItem.Status.CHECKED);
        final TextView label = new TextView(ProjectTodoItemActivity.this);
        label.setTextColor(todoItem.getStatus() == TodoItem.Status.CHECKED ? Color.GRAY : Color.BLACK);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
               // todoItem.setChecked();
                todoItem.setStatus(isChecked ? TodoItem.Status.CHECKED : TodoItem.Status.UNCHECKED);
                label.setTextColor(todoItem.getStatus() == TodoItem.Status.CHECKED ? Color.GRAY : Color.BLACK);
                databaseConnection.update(todoItem);
            }
        });
        tableRow.addView(checkBox);
        label.setText(todoItem.getName());
        tableRow.addView(label);
        final ImageView closeIcon = new ImageView(ProjectTodoItemActivity.this);

        closeIcon.setImageResource(R.drawable.baseline_close_24);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                removeItem(tableRow, todoItem);
            }
        });

        tableRow.addView(closeIcon);
        tableLayout.addView(tableRow);
        editText.getText().clear();
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    public void addNewTodoItem() {
        final String todoLabel = editText.getText().toString();
        if (!todoLabel.isEmpty()) {
            final TodoItem todoItem = new TodoItem();
            //todoItem.setParentId(selectedProjectId);
            //todoItem.setId(++id);
            todoItem.setStatus(TodoItem.Status.UNCHECKED);
            todoList.add(todoItem);
            todoItems = todoList.getAllItems();

            final Long itemId = databaseConnection.insertTodo(todoItem);
            int totalPageCount = (int) Math.ceil((double) todoItems.size()/ pageCapacity);

            if (1 == todoItems.size() % pageCapacity && currentPage == totalPageCount - 1) {
                currentPage = totalPageCount;
            }

            if (-1 == itemId) {
                Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Added SuccessFully", Toast.LENGTH_SHORT).show();
            }

            pageCapacity = 10;
            pageNumber.setVisibility(View.VISIBLE);
            previousPageButton.setEnabled(true);
            previousPageButton.setColorFilter(null);
            nextPageButton.setEnabled(true);
            nextPageButton.setColorFilter(null);

            addTodoItem(todoItem);
            editText.getText().clear();
            refreshUpdateTableLayout();
            updatePageNumber(pageNumber);

        }
    }

    /**
     * <p>
     * Update the tableLayout with todo items, checkboxes and close icons
     * </p>
     */
    private void updateTableLayout() {
        tableLayout.removeAllViews();
//        todoItems = todoList.getAllItems();

        Collections.sort(todoItems, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem item1, TodoItem item2) {
                return item1.getSortingValue(filter.getAttribute())
                        .compareToIgnoreCase(item2.getSortingValue(filter.getAttribute()));
            }
        });
        final int startIndex = (currentPage - 1) * pageCapacity;
        final int endIndex = Math.min(startIndex + pageCapacity, todoItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final TodoItem todoItem = todoItems.get(i);
            addTodoItem(todoItem);
        }
    }

    private void refreshUpdateTableLayout() {
        tableLayout.removeAllViews();
        Collections.sort(todoItems, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem item1, TodoItem item2) {
                return item1.getSortingValue(filter.getAttribute())
                        .compareToIgnoreCase(item2.getSortingValue(filter.getAttribute()));
            }
        });
        final int startItem = (currentPage - 1) * pageCapacity;
        final int endItem = Math.min(startItem + pageCapacity, todoItems.size());

        for (int i = startItem; i < endItem; i++) {
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

    private void removeItem(final TableRow tableRow, final TodoItem todoItem) {
        int previousTotalPageCount = (int) Math.ceil((double) todoItems.size() / pageCapacity);
        tableLayout.removeView(tableRow);
        //todoList.remove(todoItem.getId());

        todoItems = todoList.getAllItems();
        int totalPageCount = (int) Math.ceil((double) todoItems.size() / pageCapacity);

        if (currentPage > totalPageCount) {
            currentPage = totalPageCount;
        }

        if (todoItems.isEmpty()) {
            currentPage = 0;
            pageCapacity = 0;
            pageNumber.setVisibility(View.GONE);
            previousPageButton.setEnabled(false);
            previousPageButton.setColorFilter(Color.GRAY);
            nextPageButton.setEnabled(false);
            nextPageButton.setColorFilter(Color.GRAY);
        } else {
            pageCapacity = 10;
            updatePageNumber(pageNumber);
            if (previousTotalPageCount > totalPageCount) {
                refreshUpdateTableLayout();
            }
        }

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (null != todoItems) {
//            for ( final TodoItem todoItem : todoItems) {
//                databaseConnection.update(todoItem);
//            }
//        }
//    }
}