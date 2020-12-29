package com.example.todolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.adapter.TaskListAdapter;
import com.example.todolist.data.TaskList;
import com.example.todolist.register.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_tag";

    private TextView tvLogout;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    CheckBox a;

    private Button btnCreateNewList;

    private EditText edSearch;

    private TaskListAdapter adapter;
    private RecyclerView rvTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateNewList = findViewById(R.id.btnCreateNewList);
        edSearch = findViewById(R.id.edSearch);
        tvLogout = findViewById(R.id.tvLogout);
        rvTasks = findViewById(R.id.rvTasks);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        setupRecycler();

        // load list
        loadList(auth.getCurrentUser().getUid());


        edSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        btnCreateNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewListActivity.class));
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupRecycler() {
        adapter = new TaskListAdapter();
        rvTasks.setAdapter(adapter);

        adapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id,String title, int size) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("list_title", title);
                intent.putExtra("list_id", id);
                intent.putExtra("list_size", size);
                startActivity(intent);
            }
        });
    }

    private void loadList(String uid) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database.getReference()
                .child("users")
                .child(uid)
                .child("lists")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onSuccess: " + dataSnapshot.toString());
                        progressDialog.dismiss();

                        List<TaskList> list = new ArrayList<>();

                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            TaskList task = data.getValue(TaskList.class);
                            list.add(task);
                            if (list.size() != 0) {
                                adapter.setList(list);
                            } else {
                                adapter.setList(list);
                                Toast.makeText(MainActivity.this, "No List", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: cancel "+error.getMessage());
                    }
                });
    }
}