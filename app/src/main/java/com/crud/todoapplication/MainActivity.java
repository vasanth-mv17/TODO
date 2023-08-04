package com.crud.todoapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton backMenu;
    //    private TableLayout tableLayout;
//    private TextView editText;
    private ListView nameListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> nameLists;
    private ImageButton menuButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
//        tableLayout = findViewById(R.id.tableLayout);
//        editText = findViewById(R.id.addList);
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
//                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//                startActivity(intent);
                AddNameDialog();
            }
        });

//        ListView listView = findViewById(R.id.nameListView);
//        listView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//                startActivity(intent);
//            }
//        });
    }

    private void AddNameDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Add List Name")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String name = editText.getText().toString();

                        if (!name.isEmpty()) {
                            nameLists.add(name);
                            arrayAdapter.notifyDataSetChanged();
                        }
//                        addNewTodoItem();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

//    public void addNewTodoItem() {
//        final String todoItem = editText.getText().toString();
//
//        if (!todoItem.isEmpty()) {
//            final TableRow tableRow = new TableRow(MainActivity.this);
//
//            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT));
//
//            final CheckBox checkBox = new CheckBox(MainActivity.this);
//            tableRow.addView(checkBox);
//
//            final TextView todoView = new TextView(MainActivity.this);
//            todoView.setText(todoItem);
//            tableRow.addView(todoView);
//
//            final ImageView closeIcon = new ImageView(MainActivity.this);
//            closeIcon.setImageResource(R.drawable.close);
//            closeIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    removeItem(tableRow);
//                }
//            });
//
//            tableRow.addView(closeIcon);
//
//            tableLayout.addView(tableRow);
//            checkBox.setOnCheckedChangeListener((v, isClick) -> {
//
//            });
//        }
//    }
//    private void removeItem ( final TableRow tableRow){
//        tableLayout.removeView(tableRow);
//    }
}