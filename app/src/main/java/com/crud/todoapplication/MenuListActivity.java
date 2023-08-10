package com.crud.todoapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;

import com.crud.todoapplication.model.Project;

/**
 * <p>
 * Representing the menu list activity of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class MenuListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton backMenu;
    private ListView nameListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> todoList;
    private ImageButton menuButton;
    private String selectedList;

    /**
     * <p>
     * Creation of the menu list activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        nameListView = findViewById(R.id.nameListView);
        todoList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);

        nameListView.setAdapter(arrayAdapter);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        backMenu = findViewById(R.id.backMenuButton);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        final LinearLayout addList = findViewById(R.id.add);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AddNameDialog();
            }
        });

        final ListView listView = findViewById(R.id.nameListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int indexPosition, final long l) {
                final Intent intent = new Intent(MenuListActivity.this, SearchActivity.class);
                selectedList = todoList.get(indexPosition);
                intent.putExtra("List Reference", selectedList);
                startActivity(intent);


            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuListActivity.this);
                builder.setTitle("Delete List Item");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todoList.remove(i);
                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    /**
     * <p>
     * Displays a dialog box for adding a new list name
     * </p>
     */
    private void AddNameDialog() {
        final EditText editText = new EditText(this);

        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle("Add List Name")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final String name = editText.getText().toString();

                        if (!name.isEmpty()) {
                            final Project project = new Project();
                            project.setLabel(name);
                            todoList.add(project.toString());
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
