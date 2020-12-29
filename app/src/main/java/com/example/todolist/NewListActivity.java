package com.example.todolist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.data.TaskList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewListActivity extends AppCompatActivity {

    private static final String TAG = "NewListActivity_tag";
    private EditText etEmailListTitle;
    private Button btnNewList;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        etEmailListTitle = findViewById(R.id.etListTitle);
        btnNewList = findViewById(R.id.btnNewList);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etEmailListTitle.getText().toString().trim();

                if (title.equals("")) {
                    Toast.makeText(NewListActivity.this, "Add list Title", Toast.LENGTH_SHORT).show();
                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(NewListActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Loading ...");
                    progressDialog.show();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    DatabaseReference ref = database.getReference()
                            .child("users")
                            .child(auth.getUid())
                            .child("lists");

                    String listId = ref.push().getKey();

                    TaskList newList = new TaskList(listId, title, 0, "" + System.currentTimeMillis());

                    ref.child(listId)
                            .setValue(newList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: ");
                                    progressDialog.dismiss();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: ");
                                    Toast.makeText(NewListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });

    }
}