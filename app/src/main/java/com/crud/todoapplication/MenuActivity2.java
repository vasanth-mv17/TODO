package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.controller.MenuController;
import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.ProjectList;
import com.crud.todoapplication.model.User;
import com.crud.todoapplication.service.MenuView;

import java.util.List;

/**
 * <p>
 * Representing the menu list activity of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class MenuActivity2 extends AppCompatActivity implements MenuView{

    private DrawerLayout drawerLayout;
    private ListView nameListView;
    private ArrayAdapter<Project> arrayAdapter;
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<Project> projects;
    private ProjectList projectList;
    private ImageButton menuButton;
    private User user;
    private DatabaseConnection databaseConnection;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private static Long id = 0L;
    private Long userId;

    /**
     * <p>
     * Creation of the menu list activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        projectList = new ProjectList();
        databaseConnection = new DatabaseConnection(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        recyclerView = findViewById(R.id.recyclerView);
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.profileName);
        userTitle = findViewById(R.id.profileTitle);
        user = databaseConnection.getUserProfile();
        projects = projectList.getAllList();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectAdapter(projects, MenuActivity2.this, databaseConnection);
        androidx.recyclerview.widget.ItemTouchHelper.Callback callback = new ItemTouchHelper(adapter);
        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(callback);
        loadProjectsFromDataBase();
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if (null != user) {
            userName.setText(user.getName());
            userTitle.setText(user.getTitle());
            profileIcon.setText(user.setProfileIcon());
            userId = user.getId();
        }

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        final ImageButton editButton = findViewById(R.id.editIcon);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MenuActivity2.this, FormPageActivity.class);

                intent.putExtra(String.valueOf(R.string.Name), userName.getText().toString());
                intent.putExtra(String.valueOf(R.string.Title), userTitle.getText().toString());
                startActivityIfNeeded(intent, REQUEST_CODE);
            }
        });

        final LinearLayout addList = findViewById(R.id.add);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showAddNameDialog();
            }
        });
    }

    private void loadProjectsFromDataBase() {
        projects = databaseConnection.getAllProjectList();

        adapter.clearProjects();
        adapter.addProjects(projects);
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final User user = new User();

            userId = data.getLongExtra(String.valueOf(R.string.Id),0);
            user.setName(data.getStringExtra(String.valueOf(R.string.UserName)));
            user.setTitle(data.getStringExtra(String.valueOf(R.string.UserTitle)));
            userName.setText(user.getName());
            userTitle.setText(user.getTitle());
            profileIcon.setText(user.setProfileIcon());
        }
    }
    /**
     * <p>
     * Navigates to another activity
     * </p>
     *
     * @param project Refers the projects for intent to activity
     */
    public void goToListPage(final Project project) {
        final Intent intent = new Intent(MenuActivity2.this,ProjectTodoItemActivity.class);

        intent.putExtra(String.valueOf(R.string.ProjectId), project.getId());
        intent.putExtra(String.valueOf(R.string.ProjectName), project.getLabel());
        startActivity(intent);
    }

    /**
     * <p>
     * Removes the project form list
     * </p>
     *
     * @param project Refers the project for remove the list
     */
    public void removeProjectFromList(final Project project) {
        arrayAdapter.remove(project);
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * <p>
     * Displays a dialog box for adding a new list name
     * </p>
     */
    @Override
    public void showAddNameDialog() {
        final EditText editText = new EditText(this);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle(R.string.addList)
                .setView(editText)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final String projectName = editText.getText().toString();

                        if (! projectName.isEmpty()) {
                            final Project project = new Project();
                            project.setUserId(userId);
                            project.setId(++id);
                            project.setLabel(projectName);
                            final long projectOrder = adapter.getItemCount()+1;

                            project.setOrder(projectOrder);
                            projectList.add(project);
                            databaseConnection.insertProject(project);
                            adapter.notifyDataSetChanged();

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    /**
     * <p>
     * Called when a new name is added to the project
     * </p>
     *
     * @param projectName Refer the name to be added to the project
     */
    public void onNameAdded(final String projectName) {
        if (!projectName.isEmpty()) {
            final Project project = new Project();
            project.setUserId(userId);
            project.setId(++id);
            project.setLabel(projectName);
            projectList.add(project);
            databaseConnection.insertProject(project);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * <p>
     * Updates the todo list displayed in the listView
     * </p>
     *
     * @param todoList Refer the list of the todo list to be displayed
     */
    @Override
    public void updateTodoList(final List<String> todoList) {
//        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
//        nameListView.setAdapter(arrayAdapter);
    }

}


