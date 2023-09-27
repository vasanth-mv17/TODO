package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crud.todoapplication.api.AuthenticationService;
import com.crud.todoapplication.api.ProjectService;
import com.crud.todoapplication.model.Credentials;
import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.ProjectList;
import com.crud.todoapplication.model.User;
import com.crud.todoapplication.service.MenuView;
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
 * Representing the menu list activity of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class MenuActivity2 extends AppCompatActivity implements MenuView {

    private static final int REQUEST_CODE = 1;
    private final int currentTheme = R.style.Green;
    private User user;
    private ProjectList projectList;
    private List<Project> projects;
    private ProjectAdapter adapter;
    private String token;
    private int fontFamilyFlag;
    private int fontSizeFlag;
    private int themeFlag;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private Spinner fontFamilySpinner;
    private Spinner themeSpinner;
    private Spinner fontSizeSpinner;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private ImageButton addList;


    /**
     * <p>
     * Creation of the menu list activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(currentTheme);
        setContentView(R.layout.activity_menu2);

        projectList = new ProjectList();
        user = new User();
        drawerLayout = findViewById(R.id.drawerLayout);
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.profileName);
        userTitle = findViewById(R.id.profileTitle);
        projects = projectList.getAllList();
        token = getIntent().getStringExtra(getString(R.string.token));

        setupMenuButton();
        setupEditButton();
        setupAddListButton();
        setupAdapter();
        setupSettingButton();
        setupFontFamilySpinner();
        setupFontSizeSpinner();
        setupThemeSpinner();
        setupLogoutButton();
        getUserDetail();
        loadProjectsFromDataBase();
        getSystemSettings();
    }

    private void setupMenuButton() {
        menuButton = findViewById(R.id.menuButton);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupEditButton() {
        final ImageButton editButton = findViewById(R.id.editIcon);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MenuActivity2.this, FormPageActivity.class);

                intent.putExtra(getString(R.string.token), token);
                startActivityIfNeeded(intent, REQUEST_CODE);
            }
        });
    }

    private void setupAddListButton() {
        final EditText addListItem = findViewById(R.id.addListItem);
        addList = findViewById(R.id.addList);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String projectLabel = addListItem.getText().toString();

                if (!projectLabel.isEmpty()) {
                    addProject(projectLabel);
                    addListItem.setText("");
                }
            }
        });
    }

    private void setupAdapter() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new ProjectAdapter(projects);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        androidx.recyclerview.widget.ItemTouchHelper.Callback callback = new ItemTouchHelper(adapter);
        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(callback);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final Project project = projectList.getAllList().get(position);
                goToListPage(project);
            }

            @Override
            public void onRemoveItem(int position) {
                removeProject(projects.get(position));
            }

            @Override
            public void onUpdateItem(final Project fromProject, final Project toProject) {
                updateProject(fromProject, toProject);
            }
        });

    }

    private void setupSettingButton() {
        final ImageButton setting = findViewById(R.id.setting_button);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupFontFamilySpinner() {
       fontFamilySpinner = findViewById(R.id.fontFamily1);
        final ArrayAdapter<CharSequence> fontFamilyAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.font_family,
                android.R.layout.simple_spinner_item
        );

        fontFamilyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontFamilySpinner.setAdapter(fontFamilyAdapter);
        fontFamilySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(fontFamilyFlag == 0)) {
                    final String selectedFontFamily = adapterView.getItemAtPosition(i).toString();
                    final Typeface typeface = selectTypeface(selectedFontFamily);

                    FontManager.setCurrentTypeface(typeface);
                    applyFontToAllLayout();
                    updateSystemSettings();
                } else {
                    fontFamilyFlag = 1;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupFontSizeSpinner() {
        fontSizeSpinner = findViewById(R.id.font_size);
        final ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.font_size,
                android.R.layout.simple_spinner_item
        );

        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(fontSizeAdapter);

        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(fontSizeFlag == 0)) {
                    String selectedFontSize = adapterView.getItemAtPosition(i).toString();
                    float textSize = selectTextSize(selectedFontSize);

                    FontManager.setCurrentFontSize(textSize);
                    applyTextSizeToTextViews();
                    updateSystemSettings();
                } else {
                    fontSizeFlag = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupThemeSpinner() {
        themeSpinner = findViewById(R.id.theme_color);
        final ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.theme,
                android.R.layout.simple_spinner_item
        );

        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(themeAdapter);

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(themeFlag == 0)) {
                    final int selectedTheme = getColorResourceId(adapterView.getItemAtPosition(i)
                            .toString());

                    FontManager.setCurrentColour(selectedTheme);
                    applyTheme(selectedTheme);
                    updateSystemSettings();
                } else {
                    themeFlag = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupLogoutButton() {
        final ImageView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MenuActivity2.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addProject(String projectLabel) {
        final Project project = new Project();
        final ProjectService projectService = new ProjectService(getString(R.string.http_192_168_1_109_8080), token);

        project.setName(projectLabel);
        project.setDescription(getString(R.string.desc));
        projectList.add(project);
        projectService.create(project, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                showSnackBar(getString(R.string.added_successful));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProjectsFromDataBase() {
        final ProjectService projectService = new ProjectService(getString(R.string.http_192_168_1_109_8080),token);

        projectService.getAll(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                projects = getProjectsFromResponse(response);

                adapter.clearProjects();
                adapter.addProjects(projects);
            }
            @Override
            public void onFailure(String response) {
                showSnackBar(response);
            }
        });
    }

    private List<Project> getProjectsFromResponse(String jsonResponse) {
        List<Project> parsedProjects = new ArrayList<>();

        try {
            final JSONObject responseJson = new JSONObject(jsonResponse);
            final JSONArray data = responseJson.getJSONArray(getString(R.string.data));


            for (int i = 0; i < data.length(); i++) {
                JSONObject projectObject = data.getJSONObject(i);
                final JSONObject additionalAttribute = projectObject.getJSONObject(getString(R.string.additional_attributes));

                if (user.getId().equals(additionalAttribute.getString(getString(R.string.created_by)))) {
                    final Project project = new Project();

                    project.setId(projectObject.getString(getString(R.string._id)));
                    project.setName(projectObject.getString(getString(R.string.name)));
                    project.setDescription(projectObject.getString(getString(R.string.description)));
                    project.setOrder((long) projectObject.getInt(getString(R.string.sort_order)));
                    parsedProjects.add(project);
                }
            }
            Collections.sort(parsedProjects, new Comparator<Project>() {
                @Override
                public int compare(final Project project1, final Project project2) {
                    return Long.compare(project1.getOrder(), project2.getOrder());
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return parsedProjects;
    }

    private void updateProject(final Project fromProject, final Project toProject) {
        final ProjectService projectService = new ProjectService(getString(R.string.http_192_168_1_109_8080),token);

        projectService.update(fromProject, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                showSnackBar(getString(R.string.project_updated));
            }

            @Override
            public void onFailure(String response) {
                showSnackBar(getString(R.string.project_updation_failed));
            }
        });

        projectService.update(toProject, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                showSnackBar(getString(R.string.project_updated));
            }

            @Override
            public void onFailure(String response) {
                showSnackBar(getString(R.string.project_updation_failed));
            }
        });
    }

    private void removeProject(Project project) {
        final ProjectService projectService = new ProjectService(getString(R.string.http_192_168_1_109_8080),token);

        projectService.delete(project.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                showSnackBar(getString(R.string.project_deleted));
            }

            @Override
            public void onFailure(String response) {
                showSnackBar(getString(R.string.project_deletion_failure));
            }
        });
    }

    private void getUserDetail() {
        final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.http_192_168_1_109_8080),token);

        authenticationService.getUserDetail(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String response) {
                setUserDetail(response);
            }

            @Override
            public void onFailure(String response) {
                showSnackBar(response);
            }
        });
    }

    private void setUserDetail(String response) {
        try {
            final JSONObject jsonObject = new JSONObject(response);
            final JSONObject data = jsonObject.getJSONObject(getString(R.string.data));

            user.setId(data.getString(getString(R.string._id)));
            user.setName(data.getString(getString(R.string.name)));
            user.setTitle(data.getString(getString(R.string.title)));
            user.setEmail(data.getString(getString(R.string._email)));
            userName.setText(user.getName());
            userTitle.setText(user.getTitle());
            profileIcon.setText(user.setProfileIcon());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getSystemSettings() {
        final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.http_192_168_1_109_8080), token);

        authenticationService.getSettingDetail(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                try {
                    final JSONObject responseJson = new JSONObject(responseBody);
                    final JSONObject data = responseJson.getJSONObject(getString(R.string.data));
                    final String fontName = data.getString(getString(R.string.font_family));
                    final int fontSize = data.getInt(getString(R.string.font_size));
                    final String color = data.getString(getString(R.string.color));
                    final String sizeName = fontSizeName(fontSize);

                    FontManager.setCurrentTypeface(selectTypeface(fontName));
                    FontManager.setCurrentFontSize(selectTextSize(sizeName));
                    FontManager.setCurrentColour(getColorResourceId(color));
                    applyFontToAllLayout();
                    applyTextSizeToTextViews();
                    applyTheme(FontManager.getCurrentColour());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void updateSystemSettings() {
        final String selectedFontFamily = fontFamilySpinner.getSelectedItem().toString();
        final String selectedColor = themeSpinner.getSelectedItem().toString();
        final int fontSize = (int) selectTextSize(fontSizeSpinner.getSelectedItem().toString());
        final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.http_192_168_1_109_8080), token);

        authenticationService.updateSystemSetting(selectedFontFamily, selectedColor, fontSize, new AuthenticationService.ApiResponseCallBack() {
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

    private String fontSizeName(int fontSize) {
        if (fontSize == (int) getResources().getDimension(R.dimen.small_text_size)) {
            return "Small";
        } else if (fontSize == (int) getResources().getDimension(R.dimen.medium_text_size)) {
            return "Medium";
        } else if (fontSize == (int) getResources().getDimension(R.dimen.large_text_size)) {
            return "Large";
        } else {
            return "Default";
        }
    }
    public void applyFontToAllLayout() {
        FontManager.applyFontToView(this, getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private float selectTextSize(final String fontSize) {
        switch (fontSize) {
            case "Small":
                return getResources().getDimension(R.dimen.small_text_size);
            case "Medium":
                return getResources().getDimension(R.dimen.medium_text_size);
            case "Large":
                return getResources().getDimension(R.dimen.large_text_size);
            default:
                return getResources().getDimension(R.dimen.default_text_size);
        }
    }

    private void applyTheme(int selectedTheme) {
        final int selectedColor = ContextCompat.getColor(this, selectedTheme);
        final RelativeLayout toolBar = findViewById(R.id.toolbar_view);
        final RelativeLayout sideNavBar = findViewById(R.id.sideNavMenu);

        addList.setBackgroundColor(selectedColor);
        toolBar.setBackgroundColor(selectedColor);
        sideNavBar.setBackgroundColor(selectedColor);
    }

    private int getColorResourceId(final String color) {
        switch (color) {
            case "Default(Green)":
                return R.color.Primary;
            case "Purple":
                return R.color.Secondary;
            default:
                return R.color.Primary;
        }
    }

    private void applyTextSizeToTextViews() {
        FontManager.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private Typeface selectTypeface(final String fontName) {
        Typeface typeface;
        switch (fontName) {
            case "Default":
                typeface = ResourcesCompat.getFont(this, R.font.karla);
                break;
            case "Alata":
                typeface = ResourcesCompat.getFont(this, R.font.alata);
                break;
            case "Amaranth":
                typeface = ResourcesCompat.getFont(this, R.font.amaranth_bold);
                break;
            case "Anton":
                typeface = ResourcesCompat.getFont(this, R.font.anton);
                break;
            case "Telex":
                typeface = ResourcesCompat.getFont(this, R.font.telex);
                break;
            default:
                typeface = ResourcesCompat.getFont(this, R.font.roboto);
        }
        return typeface;
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final User user = new User();

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
        final Intent intent = new Intent(MenuActivity2.this, ProjectTodoItemActivity2.class);

        intent.putExtra(getString(R.string.ProjectId), project.getId());
        intent.putExtra(getString(R.string.ProjectName), project.getName());
        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
    }


    /**
     * <p>
     * Displays a dialog box for adding a new list name
     * </p>
     */
    @Override
    public void showAddNameDialog() {

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


