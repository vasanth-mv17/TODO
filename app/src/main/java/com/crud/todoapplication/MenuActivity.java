package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.crud.todoapplication.service.MenuView;

import de.hdodenhof.circleimageview.CircleImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

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
    private MenuController menuController;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private static Long id = 0L;

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

        menuController = new MenuController(this, this, projectList);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        nameListView = findViewById(R.id.nameListView);
        projects = projectList.getAllList();
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.profileName);
        userTitle = findViewById(R.id.profileTitle);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, projects);

        nameListView.setAdapter(arrayAdapter);
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

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final String updatedUserName = data.getStringExtra("User Name");
            final String updatedUserTitle = data.getStringExtra("User Title");
            assert updatedUserName != null;
            final String[] nameWords = updatedUserName.split(" ");
            final StringBuilder profileText = new StringBuilder();

            for (final String word : nameWords) {

                if (! word.isEmpty()) {
                    profileText.append(Character.toUpperCase(word.charAt(0)));
                }
            }

            userName.setText(updatedUserName);
            userTitle.setText(updatedUserTitle);
            profileIcon.setText(profileText);
        }
    }
    /**
     * <p>
     *
     * </p>
     *
     * @param project Refers the
     */
    public void goToListPage(final Project project) {
        final Intent intent = new Intent(MenuActivity.this,ProjectTodoItemActivity.class);

        intent.putExtra("Project Id", project.getId());
        intent.putExtra("Project name", project.getLabel());
        startActivity(intent);
    }

    /**
     * <p>
     *
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
                        final String name = editText.getText().toString();

                        menuController.onNameAdded(name, ++id);
                        arrayAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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


