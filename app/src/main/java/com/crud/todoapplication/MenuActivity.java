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
public class MenuActivity extends AppCompatActivity implements MenuView{

    private DrawerLayout drawerLayout;
    private ListView nameListView;
    private ArrayAdapter<Project> arrayAdapter;
    private List<Project> projects;
    private ProjectList projectList;
    private ImageButton menuButton;
    private User user;
    private MenuController menuController;
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
        setContentView(R.layout.activity_menu_list);

        projectList = new ProjectList();
        databaseConnection = new DatabaseConnection(this);
        menuController = new MenuController(this, this, projectList,this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        nameListView = findViewById(R.id.nameListView);
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.profileName);
        userTitle = findViewById(R.id.profileTitle);
        user = databaseConnection.getUserProfile();
        projects = projectList.getAllList();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, projects);
        loadProjectsFromDataBase();
        nameListView.setAdapter(arrayAdapter);

        if (null != user) {
            userName.setText(user.getName());
            userTitle.setText(user.getTitle());
            profileIcon.setText(user.setProfileIcon());
//            userId = user.getId();
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
                final Intent intent = new Intent(MenuActivity.this, FormPageActivity.class);

                intent.putExtra("Name", userName.getText().toString());
                intent.putExtra("Title", userTitle.getText().toString());
                startActivityIfNeeded(intent, REQUEST_CODE);
            }
        });

        final LinearLayout addList = findViewById(R.id.add);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                menuController.onAddNameClicked();
            }
        });
        final ListView listView = findViewById(R.id.nameListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int indexPosition, final long l) {
                final Project selectedProject = projectList.getAllList().get(indexPosition);
                menuController.onListItemClicked(selectedProject);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final Project selectedProject = projectList.getAllList().get(i);
                menuController.onListItemLongClicked(selectedProject);
                return true;
            }
        });

    }

    private void loadProjectsFromDataBase() {
        projects = databaseConnection.getAllProjectList();

        arrayAdapter.clear();
        arrayAdapter.addAll(projects);
        arrayAdapter.notifyDataSetChanged();
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final User user = new User();

            userId = data.getLongExtra("Id",0);
            user.setName(data.getStringExtra("User Name"));
            user.setTitle(data.getStringExtra("User Title"));
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
        final Intent intent = new Intent(MenuActivity.this,ProjectTodoItemActivity.class);

        intent.putExtra("Project Id", project.getId());
        intent.putExtra("Project name", project.getName());
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
                .setTitle("Add List Name")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final String projectName = editText.getText().toString();

                        if (! projectName.isEmpty()) {
                            onNameAdded(projectName);
                            editText.setText("");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
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
            //project.setId(++id);
            project.setName(projectName);
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


