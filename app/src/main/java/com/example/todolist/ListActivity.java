package com.example.todolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.adapter.TodoAdapter;
import com.example.todolist.data.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity_tag";

    private String listTitle;
    private String listId;
    private int listSize;

    private ImageView btnBack;
    private TextView tvListTitle;
    private TextView tvDeleteList;
    private Button btnCreateNewTask;
    private RecyclerView rvTodos;

    private FirebaseDatabase database;

    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rvTodos = findViewById(R.id.rvTodos);
        btnBack = findViewById(R.id.btnBack);
        tvListTitle = findViewById(R.id.tvListTitle);
        tvDeleteList = findViewById(R.id.tvDeleteList);
        btnCreateNewTask = findViewById(R.id.btnCreateNewTask);

        setupRecycler();

        listTitle = getIntent().getStringExtra("list_title");
        listId = getIntent().getStringExtra("list_id");
        tvListTitle.setText(listTitle);

        database = FirebaseDatabase.getInstance();

        loadList();

        tvDeleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteList(listId);
            }
        });

        btnCreateNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, NewTaskActivity.class);
                intent.putExtra("title", listTitle);
                intent.putExtra("list_id", listId);
                intent.putExtra("list_size", listSize);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void deleteList(String listId) {

        database.getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("lists")
                .child(listId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: delete list " + listTitle);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: delete failed " + e.getMessage());

                    }
                });
    }

    private void setupRecycler() {

        adapter = new TodoAdapter();
        rvTodos.setAdapter(adapter);

        adapter.setOnCheckedListener(new TodoAdapter.OnChecked() {

            @Override
            public void onChecked(final ToDo todo) {
                // update the status
                database.getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("lists")
                        .child(listId)
                        .child("tasks")
                        .child(todo.getId())
                        .child("checked")
                        .setValue(!todo.isChecked())
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


//                database.collection("task")
//                        .document(todo.getId())
//                        .update("checked", !todo.isChecked())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "onSuccess: check the todo " + todo.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "onFailure: " + e.getMessage());
//                            }
//                        });
            }

            @Override
            public void onItemClicked(String title, String description, String date) {
                Intent intent = new Intent(ListActivity.this, TaskActivity.class);

                intent.putExtra("task_title", title);
                intent.putExtra("task_description", description);
                intent.putExtra("task_date", date);
                intent.putExtra("list_id", listId);
                intent.putExtra("list_size", listSize);

                startActivity(intent);
            }
        });

    }

    private void loadList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database.getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("lists")
                .child(listId)
                .child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onSuccess: " + snapshot.toString());
                        progressDialog.dismiss();

                        List<ToDo> list = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            ToDo task = data.getValue(ToDo.class);
                            list.add(task);
                            if (list.size() != 0) {
                                listSize = list.size();
                                adapter.setList(list);
                            } else {
                                adapter.setList(list);
                                Toast.makeText(ListActivity.this, "No List", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: ");
                    }
                });
    }
}