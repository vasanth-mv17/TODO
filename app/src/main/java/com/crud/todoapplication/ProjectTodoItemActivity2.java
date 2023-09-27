package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.api.AuthenticationService;
import com.crud.todoapplication.api.TodoItemService;
import com.crud.todoapplication.controller.ProjectListController;
import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.model.TodoList;
import com.crud.todoapplication.model.User;
import com.crud.todoapplication.service.ProjectView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String selectedProjectId;
    private TodoList todoList;
    private DatabaseConnection databaseConnection;
    private com.crud.todoapplication.model.Filter filter;
    private ProjectListController projectListController;
    private List<TodoItem> todoItems;
    private RelativeLayout toolbar;
    private RelativeLayout toolBar;
    private int currentPage = 1;
    private int pageCapacity;
    private String token;

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

        projectListController = new ProjectListController(this);
        selectedProjectId = getIntent().getStringExtra(getString(R.string.ProjectId));
        selectedProjectName = getIntent().getStringExtra(getString(R.string.ProjectName));
        token = getIntent().getStringExtra(getString(R.string.token));
        todoList = new TodoList();
        databaseConnection = new DatabaseConnection(this);
        filter = new com.crud.todoapplication.model.Filter();
        todoItems = todoList.getAllItems();
        pageCapacity = Integer.parseInt(filterSpinnerPage.getSelectedItem().toString());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoItemAdapter(todoItems, ProjectTodoItemActivity2.this, databaseConnection, todoList);
        androidx.recyclerview.widget.ItemTouchHelper.Callback callback = new ItemMoveHelper(adapter);
        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(callback);
        loadTodoItemsFromDatabase();
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if (selectedProjectName != null) {
            textView.setText(selectedProjectName);
        }

        setupMenuButton();
        setupAddList();
        setupAdapter();
        setupSearchButton();
        setupStatusSpinner();
        setupPageFilterSpinner();
        setupPreviousPageButton();
        setupNextPageButton();
        setupFilterButton();
        setupAddShowButton();
        applyFontToAllLayout();
        applyFontSize();
        applyTheme();
    }

    private void setupMenuButton() {
        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
    }

    private void setupAddList() {
        addButton = findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                projectListController.onAddButtonClick();
            }
        });
    }

    private void setupAdapter() {
        adapter.setOnClickListener(new TodoItemAdapter.OnItemClickListener() {
            @Override
            public void onCheckBoxClick(TodoItem todoItem) {
                updateItemState(todoItem);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCloseIconClick(TodoItem todoItem) {
                removeTodoItem(todoItem);
                todoList.remove(todoItem.getId());
                todoItems.remove(todoItem);
                adapter.notifyDataSetChanged();

                if (currentPage > totalPages()) {
                    currentPage = totalPages();
                }

                updateRecyclerView();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onItemUpdate(TodoItem fromItem, TodoItem toItem) {
                updateItemOrder(fromItem, toItem);
            }
        });
    }

    private void setupSearchButton() {
        searchButton = findViewById(R.id.searchView);
        searchButton.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                projectListController.onSearchAndDisplayItems(newText);

                return true;
            }
        });
    }

    private void setupStatusSpinner() {
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
                        todoItems = todoList.getAllItems(selectedProjectId);
                        List<TodoItem> allItem = new ArrayList<>();

                        for (final TodoItem todoItem : todoItems) {
                            allItem.add(todoItem);
                        }
                        todoItems = allItem;
                        currentPage = 1;

                        if (null != todoItems) {
                            adapter.updateTodoItems(todoItems);
                        }
                        break;
                    case CHECKED:
                        displayCheckedItems();
                        break;
                    case UNCHECKED:
                        displayUncheckedItems();
                        break;
                }
                updateRecyclerView();
                updatePageNumber(pageNumber);
            }
            @Override
            public void onNothingSelected(final AdapterView<?> parentView) {
            }
        });
    }

    private void setupPageFilterSpinner() {
        final ArrayAdapter<CharSequence> pageSpinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.page_options, android.R.layout.simple_spinner_item);

        pageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinnerPage.setAdapter(pageSpinnerAdapter);
        filterSpinnerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                pageCapacity = Integer.parseInt(adapterView.getItemAtPosition(i).toString());

                if (currentPage > totalPages()) {
                    currentPage = totalPages();
                }
                updateRecyclerView();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });

    }

    private void setupPreviousPageButton() {
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 1) {
                    currentPage--;
                    loadTodoItemsFromDatabase();
                    updatePageNumber(pageNumber);
                }
            }
        });
    }

    private void setupNextPageButton() {
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((currentPage * pageCapacity) < todoItems.size()) {
                    currentPage++;
                    loadTodoItemsFromDatabase();
                    updatePageNumber(pageNumber);
                }
            }
        });
    }

    private void setupFilterButton() {
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
    }

    private void setupAddShowButton() {
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
    }

    private void updateItemState(TodoItem todoItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.http_192_168_1_109_8080), token);

        itemService.updateState(todoItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                showSnackBar(response);
            }

            @Override
            public void onFailure(String response) {
                showSnackBar(response);
            }
        });
    }

    private void updateItemOrder(TodoItem fromItem, TodoItem toItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.http_192_168_1_109_8080), token);

        itemService.update(fromItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onFailure(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
        itemService.update(toItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                showSnackBar(responseBody);
            }

            @Override
            public void onFailure(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void removeTodoItem(final TodoItem todoItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.http_192_168_1_109_8080), token);

        itemService.delete(todoItem.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                showSnackBar(getString(R.string.removed));
            }

            @Override
            public void onFailure(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    public void applyTheme() {
        int currentTheme = FontManager.getCurrentColour();
        if (currentTheme == R.color.Primary) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Primary));
            addButton.setBackgroundColor(getResources().getColor(R.color.Primary));
            toolBar.setBackgroundColor(getResources().getColor(R.color.Primary));
        } else if (currentTheme == R.color.Secondary) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.Secondary));
            addButton.setBackgroundColor(getResources().getColor(R.color.Secondary));
            toolBar.setBackgroundColor(getResources().getColor(R.color.Secondary));
        }
    }

    public void applyFontSize() {
        FontManager.applyFontToView(this, getWindow().getDecorView().findViewById(android.R.id.content));
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

    private void loadTodoItemsFromDatabase() {
        final TodoItemService todoItemService = new TodoItemService(getString(R.string.http_192_168_1_109_8080), token);

        todoItemService.getAllItem(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                todoItems = parseItemFromResponse(response);

                if (!todoItems.isEmpty()) {
                    todoList.setAllItems(todoItems);
                    updateRecyclerView();
                    updatePageNumber(pageNumber);
                } else {
                    pageNumber.setVisibility(View.GONE);
                    nextPageButton.setVisibility(View.GONE);
                    previousPageButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String response) {
                showSnackBar(response);
            }
        });
    }

    private List<TodoItem> parseItemFromResponse(String response) {
        List<TodoItem> parsedProjects = new ArrayList<>();

        try {
            final JSONObject responseJson = new JSONObject(response);
            final JSONArray data = responseJson.getJSONArray(getString(R.string.data));


            for (int i = 0; i < data.length(); i++) {
                JSONObject projectObject = data.getJSONObject(i);


                if (null != selectedProjectId && selectedProjectId.equals(projectObject.getString(getString(R.string.project_id)))) {
                    final TodoItem todoItem = new TodoItem();

                    todoItem.setId(projectObject.getString(getString(R.string._id)));
                    todoItem.setParentId(selectedProjectId);
                    todoItem.setStatus(projectObject.getBoolean(getString(R.string.is_completed)) ? TodoItem.Status.CHECKED : TodoItem.Status.UNCHECKED);
                    todoItem.setName(projectObject.getString(getString(R.string.name)));
                    todoItem.setOrder((long) projectObject.getInt(getString(R.string.sort_order)));
                    parsedProjects.add(todoItem);
                }
            }
            Collections.sort(parsedProjects, new Comparator<TodoItem>() {
                @Override
                public int compare(final TodoItem item1, final TodoItem item2) {
                    return Long.compare(item1.getOrder(), item2.getOrder());
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return parsedProjects;
    }

    /**
     * <p>
     * Displays the checked items of the list
     * </p>
     *
     */
    private void displayCheckedItems() {
        todoItems = todoList.getAllItems(selectedProjectId);
        List<TodoItem> checkedItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getStatus() == TodoItem.Status.CHECKED) {
                checkedItems.add(todoItem);
            }
        }
        todoItems = checkedItems;
        currentPage = 1;
        if (null != todoItems) {
            adapter.updateTodoItems(todoItems);
        }
    }

    /**
     * <p>
     * Displays the Unchecked items of the list
     * </p>
     *
     */
    private void displayUncheckedItems() {
        todoItems = todoList.getAllItems(selectedProjectId);
        List<TodoItem> unCheckedItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getStatus() == TodoItem.Status.UNCHECKED) {
                unCheckedItems.add(todoItem);
            }
        }
        todoItems = unCheckedItems;
        currentPage = 1;
        if (null != todoItems) {
            adapter.updateTodoItems(todoItems);
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
        todoItems = todoList.getAllItems(selectedProjectId);
        List<TodoItem> searchAllItems = new ArrayList<>();

        for (final TodoItem todoItem : todoItems) {
            if (todoItem.getName().toLowerCase().contains(filter.getSearchAttribute().toLowerCase())) {
                searchAllItems.add(todoItem);
            }
        }
        todoItems = searchAllItems;
        currentPage = 1;
        if (null != todoItems) {
            adapter.updateTodoItems(todoItems);
        }
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
            final TodoItem todoItem = new TodoItem();
            final long itemOrder = adapter.getItemCount() + 1;
            final TodoItemService todoItemService = new TodoItemService(getString(R.string.http_192_168_1_109_8080), token);

            todoItem.setName(todoLabel);
            todoItem.setParentId(selectedProjectId);
            todoItem.setStatus(TodoItem.Status.UNCHECKED);
            todoItem.setOrder(itemOrder);
            todoList.add(todoItem);

            todoItemService.create(todoItem.getName(), selectedProjectId, new AuthenticationService.ApiResponseCallBack() {
                @Override
                public void onSuccess(String response) {
                    showSnackBar(getString(R.string.added_successful));

                    todoItems = todoList.getAllItems(selectedProjectId);

                    pageNumber.setVisibility(View.VISIBLE);
                    previousPageButton.setVisibility(View.VISIBLE);
                    nextPageButton.setVisibility(View.VISIBLE);
//                    adapter.addProjects(todoItems);
//                    loadTodoItemsFromDatabase();
                    adapter.updateTodoItems(todoItems);
                    loadTodoItemsFromDatabase();
                }

                @Override
                public void onFailure(String response) {
                    showSnackBar(response);
                }
            });

            editText.getText().clear();
            updateRecyclerView();
            updatePageNumber(pageNumber);
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

        if (0 > startIndex) {
            startIndex = 0;
        }

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
        pageNumber.setText(String.format("%d / %d", currentPage, totalPages()));
    }

    private int totalPages() {
        return (int) Math.ceil((double) todoItems.size() / pageCapacity);
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}