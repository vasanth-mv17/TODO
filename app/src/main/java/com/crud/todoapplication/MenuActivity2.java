package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.controller.MenuController;
import com.crud.todoapplication.model.Credentials;
import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.ProjectList;
import com.crud.todoapplication.model.User;
import com.crud.todoapplication.service.MenuView;

import java.util.ArrayList;
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
    private Credentials userCredentials;
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
    private int currentTheme = R.style.Green;

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
        setTheme(currentTheme);
        setContentView(R.layout.activity_menu2);

//        projectList = new ProjectList();
//        databaseConnection = new DatabaseConnection(this);
//        drawerLayout = findViewById(R.id.drawerLayout);
//        menuButton = findViewById(R.id.menuButton);
//        recyclerView = findViewById(R.id.recyclerView);
//        profileIcon = findViewById(R.id.profileIcon);
//        userName = findViewById(R.id.profileName);
//        userTitle = findViewById(R.id.profileTitle);
////        user = databaseConnection.getUserProfile();
//        user = new User();
//        projects = projectList.getAllList();
//        userCredentials  = new Credentials();
//        Intent intent = getIntent();
//        String Email = intent.getStringExtra("email");
//        //final User user = databaseConnection.getUserById(Email);
//        userId = user.getId();
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new ProjectAdapter(projects, MenuActivity2.this, databaseConnection);
//        androidx.recyclerview.widget.ItemTouchHelper.Callback callback = new ItemTouchHelper(adapter);
//        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(callback);
//        loadProjectsFromDataBase(userId);
//        recyclerView.setAdapter(adapter);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//
//
//        if (userName != null && userTitle != null) {
//            user.setId(userId);
//            user.setName(user.getName());
//            user.setTitle(user.getTitle());
//            user.setEmail(Email);
//            //databaseConnection.updateUser(user);
//
//        }
//
//        if (null != user) {
////            userName.setText(getIntent().getStringExtra(userCredentials.getName()));
//            userName.setText(user.getName());
//            userTitle.setText(user.getTitle());
//            profileIcon.setText(user.setProfileIcon());
//            userId = user.getId();
//
//        }
//
//        menuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//
//        final ImageButton editButton = findViewById(R.id.editIcon);
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Intent intent = new Intent(MenuActivity2.this, FormPageActivity.class);
//
//                intent.putExtra(String.valueOf(R.string.Name), userName.getText().toString());
//                intent.putExtra(String.valueOf(R.string.Title), userTitle.getText().toString());
//                startActivityIfNeeded(intent, REQUEST_CODE);
//            }
//        });
//
//        final EditText addListItem = findViewById(R.id.addListItem);
//        final ImageButton addList = findViewById(R.id.addList);
//        addList.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onClick(View view) {
//                final String projectLabel = addListItem.getText().toString();
//
//                if (!projectLabel.isEmpty()) {
//                    final Project project = new Project();
//
//                    project.setUserId(userId);
//                    project.setId(++id);
//                    project.setLabel(projectLabel);
//                    final long projectOrder = adapter.getItemCount()+1;
//
//                    project.setOrder(projectOrder);
//                    projectList.add(project);
//                    databaseConnection.insertProject(project);
//                    adapter.notifyDataSetChanged();
//
//                    addListItem.getText().clear();
//                }
//            }
//        });
//
//        final ImageButton setting = findViewById(R.id.setting_button);
//
//        setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.END);
//            }
//        });
//        final Spinner fontFamilySpinner = findViewById(R.id.fontFamily1);
//
//        ArrayAdapter<CharSequence> fontFamilyAdapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.font_family,
//                android.R.layout.simple_spinner_item
//        );
//
//        fontFamilyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        fontFamilySpinner.setAdapter(fontFamilyAdapter);
//        fontFamilySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                final String selectedFontFamily = adapterView.getItemAtPosition(i).toString();
//                final Typeface typeface = selectTypeface(selectedFontFamily);
//
//                FontManager.setCurrentTypeface(typeface);
//                applyFontToAllLayout();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//        final Spinner fontSizeSpinner = findViewById(R.id.font_size);
//
//        ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.font_size,
//                android.R.layout.simple_spinner_item
//        );
//
//        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        fontSizeSpinner.setAdapter(fontSizeAdapter);
//
//        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selectedFontSize = adapterView.getItemAtPosition(i).toString();
//                float textSize = selectTextSize(selectedFontSize);
//
//                FontManager.setCurrentFontSize(textSize);
//                applyTextSizeToTextViews();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
//
//        final Spinner themeSpinner = findViewById(R.id.theme_color);
//        final RelativeLayout toolBar = findViewById(R.id.toolbar_view);
//        final RelativeLayout sideNavBar = findViewById(R.id.sideNavMenu);
//
//        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.theme,
//                android.R.layout.simple_spinner_item
//        );
//
//        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        themeSpinner.setAdapter(themeAdapter);
//
//        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selectedTheme = adapterView.getItemAtPosition(i).toString();
//
//                switch (selectedTheme) {
//                    case "Default(Green)":
//                        addList.setBackgroundColor(getResources().getColor(R.color.Primary));
//                        toolBar.setBackgroundColor(getResources().getColor(R.color.Primary));
//                        sideNavBar.setBackgroundColor(getResources().getColor(R.color.Primary));
//                        FontManager.setCurrentColour(R.color.Primary);
//                        break;
//                    case "Purple":
//                        addList.setBackgroundColor(getResources().getColor(R.color.Secondary));
//                        toolBar.setBackgroundColor(getResources().getColor(R.color.Secondary));
//                        sideNavBar.setBackgroundColor(getResources().getColor(R.color.Secondary));
//                        FontManager.setCurrentColour(R.color.Secondary);
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        final ImageView logout = findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Intent intent = new Intent(MenuActivity2.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//
//    public void applyFontToAllLayout() {
//        FontManager.applyFontToView(this, getWindow().getDecorView().findViewById(android.R.id.content));
//    }
//    private float selectTextSize(final String fontSize) {
//        switch (fontSize) {
//            case "Small":
//                return getResources().getDimension(R.dimen.small_text_size);
//            case "Medium":
//                return getResources().getDimension(R.dimen.medium_text_size);
//            case "Large":
//                return getResources().getDimension(R.dimen.large_text_size);
//            default:
//                return getResources().getDimension(R.dimen.small_text_size);
//        }
//    }
//
//    private void applyTextSizeToTextViews() {
//        FontManager.applyTextSizeToView(findViewById(android.R.id.content).getRootView());
//    }
//
//    private Typeface selectTypeface(final String fontName) {
//        Typeface typeface;
//        switch (fontName) {
//            case "Default":
//                typeface = ResourcesCompat.getFont(this, R.font.karla);
//                break;
//            case "Alata":
//                typeface = ResourcesCompat.getFont(this, R.font.alata);
//                break;
//            case "Amaranth":
//                typeface = ResourcesCompat.getFont(this, R.font.amaranth_bold);
//                break;
//            case "Anton":
//                typeface = ResourcesCompat.getFont(this, R.font.anton);
//                break;
//            case "Telex":
//                typeface = ResourcesCompat.getFont(this, R.font.telex);
//                break;
//            default:
//                typeface = ResourcesCompat.getFont(this, R.font.karla);
//        }
//        return typeface;
//    }
//
//    private void loadProjectsFromDataBase(final Long userId) {
//        projects = databaseConnection.getAllProjectList();
//        List<Project> filterProject = new ArrayList<>();
//
//        for (Project project : projects) {
//            if (project.getUserId().equals(userId)) {
//                filterProject.add(project);
//            }
//        }
//        adapter.clearProjects();
//        adapter.addProjects(filterProject);
//    }
//
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            final User user = new User();
//
//            userId = data.getLongExtra(String.valueOf(R.string.Id),0);
//            user.setName(data.getStringExtra(String.valueOf(R.string.UserName)));
//            user.setTitle(data.getStringExtra(String.valueOf(R.string.UserTitle)));
//            userName.setText(user.getName());
////            userName.setText(userCredentials.getName());
//            userTitle.setText(user.getTitle());
//            profileIcon.setText(user.setProfileIcon());
//        }
//    }
//    /**
//     * <p>
//     * Navigates to another activity
//     * </p>
//     *
//     * @param project Refers the projects for intent to activity
//     */
//    public void goToListPage(final Project project) {
//        final Intent intent = new Intent(MenuActivity2.this,ProjectTodoItemActivity.class);
//
//        intent.putExtra(String.valueOf(R.string.ProjectId), project.getId());
//        intent.putExtra(String.valueOf(R.string.ProjectName), project.getLabel());
//        startActivity(intent);
//    }
//
//
//    /**
//     * <p>
//     * Displays a dialog box for adding a new list name
//     * </p>
//     */
//    @Override
//    public void showAddNameDialog() {
//        final EditText editText = new EditText(this);
//
//        editText.setInputType(InputType.TYPE_CLASS_TEXT);
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.addList)
//                .setView(editText)
//                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, final int which) {
//                        final String projectName = editText.getText().toString();
//
//                        if (! projectName.isEmpty()) {
//                            final Project project = new Project();
//                            project.setUserId(userId);
//                            project.setId(++id);
//                            project.setLabel(projectName);
//                            final long projectOrder = adapter.getItemCount()+1;
//
//                            project.setOrder(projectOrder);
//                            projectList.add(project);
//                            databaseConnection.insertProject(project);
//                            adapter.notifyDataSetChanged();
//
//                        }
//                    }
//                })
//                .setNegativeButton(R.string.cancel, null)
//                .create()
//                .show();
//    }
//
//
//    /**
//     * <p>
//     * Updates the todo list displayed in the listView
//     * </p>
//     *
//     * @param todoList Refer the list of the todo list to be displayed
//     */
//    @Override
//    public void updateTodoList(final List<String> todoList) {
////        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
////        nameListView.setAdapter(arrayAdapter);
//    }
    }

    @Override
    public void showAddNameDialog() {

    }

    @Override
    public void updateTodoList(List<String> projectList) {

    }
}


