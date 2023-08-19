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

import com.crud.todoapplication.controller.MenuController;
import com.crud.todoapplication.service.MenuView;

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
    private ImageButton backMenu;
    private ListView nameListView;
    private ArrayAdapter<String> arrayAdapter;
    private ImageButton menuButton;
    private MenuController menuController;

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

        menuController = new MenuController(this, this);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        nameListView = findViewById(R.id.nameListView);
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
                menuController.onAddNameClicked();
            }
        });
        final ListView listView = findViewById(R.id.nameListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int indexPosition, final long l) {
                menuController.onListItemClicked(indexPosition);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                menuController.onListItemLongClicked(i);
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

                        menuController.onNameAdded(name);
                        arrayAdapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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

                        menuController.onNameAdded(name);
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
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
        nameListView.setAdapter(arrayAdapter);
    }
}
