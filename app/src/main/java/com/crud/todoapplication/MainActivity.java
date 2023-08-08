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

/**
 * <p>
 * Representing the main activity of the Todo application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton backMenu;
    private ListView nameListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> nameLists;
    private ImageButton menuButton;
    private String selectedList;

    /**
     * <p>
     * Creation of the main activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        nameListView = findViewById(R.id.nameListView);
        nameLists = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nameLists);

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
                final Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                selectedList = nameLists.get(indexPosition);
                intent.putExtra("List Reference", selectedList);
                startActivity(intent);

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
                            nameLists.add(name);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
