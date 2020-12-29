package com.example.todolist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.data.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewTaskActivity extends AppCompatActivity {

    private static final String TAG = "NewTaskActivity_tag";

    private FirebaseDatabase database;

    private ImageView btnBack;
    private TextView btnAddTask;

    private String listTitle;
    private String listId;
    private int listSize;

    private EditText etTaskTitle, etTaskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        btnBack = findViewById(R.id.btnBack);
        btnAddTask = findViewById(R.id.btnAddTask);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskTitle = findViewById(R.id.etTaskTitle);

        database = FirebaseDatabase.getInstance();

        listTitle = getIntent().getStringExtra("title");
        listId = getIntent().getStringExtra("list_id");
        listSize = getIntent().getIntExtra("list_size", 0);


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTaskTitle.getText().toString().trim();
                String description = etTaskDescription.getText().toString().trim();
                if (title.equals("")) {
                    Toast.makeText(NewTaskActivity.this, "Add Title !", Toast.LENGTH_SHORT).show();
                } else if (description.equals("")) {
                    Toast.makeText(NewTaskActivity.this, "Add description !", Toast.LENGTH_SHORT).show();
                } else {
                    addNewTask(title, description);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addNewTask(String title, String description) {

        final ProgressDialog progressDialog = new ProgressDialog(NewTaskActivity.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        Log.d(TAG, "addNewTask: list id " + listId);

        DatabaseReference ref = database.getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("lists")
                .child(listId)
                .child("tasks");

        String todoId = ref.push().getKey();

        ToDo toDo = new ToDo(todoId, title, description, false, "" + System.currentTimeMillis());

        ref.child(todoId)
                .setValue(toDo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: add new Todo ");
                        updateListSize(listSize);
                        progressDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(NewTaskActivity.this, "Failed to add new ToDo ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateListSize(int size) {

        final int finalSize = size;
        database.getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("lists")
                .child(listId)
                .child("size")
                .setValue(++size)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: update " + finalSize);
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