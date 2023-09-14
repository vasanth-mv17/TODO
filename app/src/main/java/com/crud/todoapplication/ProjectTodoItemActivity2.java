package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
public class ProjectTodoItemActivity2 extends AppCompatActivity implements ProjectView {

    private DrawerLayout drawerLayout;
    private TableLayout tableLayout;
    private RecyclerView recyclerView;
    private TodoItemAdapter adapter;
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
    private ImageButton addButton;

    private String selectedProjectName;
    private Long selectedProjectId;
    private TodoList todoList;
    private DatabaseConnection databaseConnection;
    private com.crud.todoapplication.model.Filter filter;
    private ProjectListController projectListController;
    private List<TodoItem> todoItems;
    private List<TodoItem> checkedItems;
    private List<TodoItem> unCheckedItems;
    private List<TodoItem> allItems;
    private RelativeLayout toolbar;
    private RelativeLayout toolBar;
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
    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentTheme = FontManager.getCurrentColour();
        setContentView(R.layout.activity_sub2);

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
        toolbar = findViewById(R.id.toolbar);
        toolBar = findViewById(R.id.bottom_toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectListController = new ProjectListController(this);
        selectedProjectId = getIntent().getLongExtra(String.valueOf(R.string.ProjectId), 0L);
        selectedProjectName = getIntent().getStringExtra(String.valueOf(R.string.ProjectName));
        todoList = new TodoList();
        databaseConnection = new DatabaseConnection(this);
        filter = new com.crud.todoapplication.model.Filter();

        //todoItems = databaseConnection.getTodoItemsForProject(selectedProjectId);
        todoItems = todoList.getAllItems();
        adapter = new TodoItemAdapter(todoItems, ProjectTodoItemActivity2.this, databaseConnection, todoList);
        androidx.recyclerview.widget.ItemTouchHelper.Callback callback = new ItemMoveHelper(adapter);
        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(callback);
        loadTodoItemsFromDatabase(selectedProjectId);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        checkedItems = new ArrayList<>();
        unCheckedItems = new ArrayList<>();
        allItems = new ArrayList<>();

        if (currentTheme == R.color.Primary) {
           toolbar.setBackgroundColor(getResources().getColor(R.color.Primary));
           addButton.setBackgroundColor(getResources().getColor(R.color.Primary));
           toolBar.setBackgroundColor(getResources().getColor(R.color.Primary));
        } else if (currentTheme == R.color.Secondary) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Secondary));
            addButton.setBackgroundColor(getResources().getColor(R.color.Secondary));
            toolBar.setBackgroundColor(getResources().getColor(R.color.Secondary));
        }

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
                projectListController.onSearchAndDisplayItems(newText);

                if (null != todoItems) {
                    adapter.clearProjects();
                    adapter.addProjects(todoItems);
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
                final Status selectedStatus = Status.values()[position];
                filter.setSearchAttribute(searchButton.getQuery().toString());

                switch (selectedStatus) {
                    case ALL:
                        todoItems = databaseConnection.getTodoItemsForProject(selectedProjectId);
                        List<TodoItem> allItem = new ArrayList<>();

                        for (final TodoItem todoItem : todoItems) {
                            allItem.add(todoItem);
                        }
                        todoItems = allItem;
                        currentPage = 1;

                        if (null != todoItems) {
                            adapter.clearProjects();
                            adapter.addProjects(todoItems);
                        }
                        break;
                    case CHECKED:
                        displayCheckedItems();
                        break;
                    case UNCHECKED:
                        displayUncheckedItems();
                        break;
                }

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
                    updateRecyclerView();
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
                    updateRecyclerView();
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
        final TextView addTextView = findViewById(R.id.addTodoItem);

        addShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getVisibility() == View.GONE) {
                    editText.setVisibility(View.VISIBLE);
                    addButton.setVisibility(View.VISIBLE);
                    addTextView.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.GONE);
                    addButton.setVisibility(View.GONE);
                    addTextView.setVisibility(View.GONE);
                }
            }
        });
        applyFontToAllLayout();
    }

    public void applyFontToAllLayout() {
        FontManager.applyFontToView(this, getWindow().getDecorView().findViewById(android.R.id.content));
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
            adapter.clearProjects();
            adapter.addProjects(todoItems);
            updatePageNumber(pageNumber);
        }

        adapter.updateTodoItems(todoItems);
    }

    /**
     * <p>
     * Displays the checked items of the list
     * </p>
     *
     */
    private void displayCheckedItems() {
        todoItems = databaseConnection.getTodoItemsForProject(selectedProjectId);
        List<TodoItem> checkedItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getStatus() == TodoItem.Status.CHECKED) {
                checkedItems.add(todoItem);
            }
        }
        todoItems = checkedItems;
        currentPage = 1;
        if (null != todoItems) {

            adapter.clearProjects();
            adapter.addProjects(todoItems);
        }
    }

    /**
     * <p>
     * Displays the Unchecked items of the list
     * </p>
     *
     */
    private void displayUncheckedItems() {
        todoItems = databaseConnection.getTodoItemsForProject(selectedProjectId);
        List<TodoItem> unCheckedItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getStatus() == TodoItem.Status.UNCHECKED) {
                unCheckedItems.add(todoItem);
            }
        }
        todoItems = unCheckedItems;
        currentPage = 1;
        if (null != todoItems) {
            adapter.clearProjects();
            adapter.addProjects(todoItems);
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
        filter.setSearchAttribute(query);
        todoItems = databaseConnection.getTodoItemsForProject(selectedProjectId);
        List<TodoItem> searchAllItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getLabel().toLowerCase().contains(filter.getSearchAttribute().toLowerCase())) {
                searchAllItems.add(todoItem);
            }
        }
        todoItems = searchAllItems;
        currentPage = 1;
    }

    /**
     * <p>
     * Stores the items into table layout
     * </p>
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addNewTodoItem() {
        final String todoLabel = editText.getText().toString();
        if (!todoLabel.isEmpty()) {
            final TodoItem todoItem = new TodoItem(todoLabel);
            final long itemOrder = adapter.getItemCount() + 1;

            todoItem.setParentId(selectedProjectId);
            todoItem.setId(++id);
            todoItem.setOrder(itemOrder);
            todoItem.setStatus(TodoItem.Status.UNCHECKED);
            todoList.add(todoItem);

            databaseConnection.insertTodo(todoItem);
            adapter.notifyDataSetChanged();
            todoItems = todoList.getAllItems(selectedProjectId);
//            int totalPageCount = (int) Math.ceil((double) todoItems.size()/ pageCapacity);

//            if (1 == todoItems.size() % pageCapacity && currentPage == totalPageCount - 1) {
//                currentPage = totalPageCount;
//            }

//            pageCapacity = 10;
            pageNumber.setVisibility(View.VISIBLE);
            previousPageButton.setEnabled(true);
            previousPageButton.setColorFilter(null);
            nextPageButton.setEnabled(true);
            nextPageButton.setColorFilter(null);

            updateRecyclerView();
            updatePageNumber(pageNumber);
            editText.getText().clear();

        }
    }

    /**
     * <p>
     * Update the tableLayout with todo items, checkboxes and close icons
     * </p>
     */

    private void updateRecyclerView() {
        int startIndex = (currentPage - 1) * pageCapacity;
        int endIndex = Math.min(startIndex + pageCapacity, todoItems.size());

        List<TodoItem> pageItems = todoItems.subList(startIndex, endIndex);
        adapter.updateTodoItems(pageItems);
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
        todoList.remove(todoItem.getId());

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

            }
        }
    }
}