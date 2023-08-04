package com.crud.todoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.todoEditText);

        final Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTodoItem();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void addNewTodoItem() {
        final String todoItem = editText.getText().toString();

        if (! todoItem.isEmpty()) {
            final TableRow tableRow = new TableRow(MainActivity.this);

            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            final CheckBox checkBox = new CheckBox(MainActivity.this);
            tableRow.addView(checkBox);

            final TextView todoView = new TextView(MainActivity.this);
            todoView.setText(todoItem);
            tableRow.addView(todoView);

            final ImageView closeIcon = new ImageView(MainActivity.this);
            closeIcon.setImageResource(R.drawable.baseline_close_24);
            closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(tableRow);
                }
            });

            tableRow.addView(closeIcon);

            tableLayout.addView(tableRow);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        todoView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                    } else {
                        todoView.setTextColor(Color.BLACK);
                    }
                }
            });
            editText.getText().clear();
        }
    }

    private void removeItem(final TableRow tableRow) {
        tableLayout.removeView(tableRow);
    }
}