package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "TaskActivity_tag";
    String task_title;
    String task_description;
    String task_date;
    String task_id;
    String list_id;
    int list_size;

    private ImageView btnBack;
    private EditText etTaskTitle, etTaskDescription;
    private TextView btnEdit;
    private TextView tvDeleteTask;
    private TextView tvDate;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        btnBack = findViewById(R.id.btnBack);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnEdit = findViewById(R.id.btnEdit);
        tvDeleteTask = findViewById(R.id.tvDeleteTask);
        tvDate = findViewById(R.id.tvDate);

        database = FirebaseDatabase.getInstance();


        Intent i = getIntent();

        task_title = i.getStringExtra("task_title");
        task_description = i.getStringExtra("task_description");
        task_date = i.getStringExtra("task_date");
        task_id = i.getStringExtra("task_id");
        list_id = i.getStringExtra("list_id");
        list_size = i.getIntExtra("list_size", 0);

        etTaskTitle.setText(task_title);
        etTaskDescription.setText(task_description);

        // date
        SimpleDateFormat DateFor = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
        String stringDate = DateFor.format(Long.parseLong(task_date));
        tvDate.setText(stringDate);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: task id  : " + task_id);
                Log.d(TAG, "onClick: task_date  : " + task_date);

                String title = etTaskTitle.getText().toString().trim();
                String description = etTaskDescription.getText().toString().trim();

                if (!title.equals(task_title)) {
                    database.getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child("lists")
                            .child(list_id)
                            .child("tasks")
                            .child(task_id)
                            .child("title")
                            .setValue(title)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: success to create task");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: error " + e.getMessage());

                                }
                            });
                }

                if (!description.equals(task_description)) {
                    database.getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child("lists")
                            .child(list_id)
                            .child("tasks")
                            .child(task_id)
                            .child("description")
                            .setValue(description)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: success to create task");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: error " + e.getMessage());
                                }
                            });

                }
                finish();
            }
        });


        tvDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTask(task_id);
            }
        });
    }

    private void deleteTask(final String task_id) {
        database.getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("lists")
                .child(list_id)
                .child("tasks")
                .child(task_id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: success to create task");
                        updateListSize();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error " + e.getMessage());
                    }
                });
    }

    private void updateListSize() {
        database.getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("lists")
                .child(list_id)
                .child("size")
                .setValue(--list_size)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: update");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
}