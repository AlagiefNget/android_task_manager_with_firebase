package com.example.taskmanagementapp_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagementapp_v2.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fabBtn;
    private Toolbar toolBar;

    // Set up firebase to store data
    private FirebaseAuth myAuth;
    private DatabaseReference myDb;

    // Recyclerview
    private RecyclerView recyclerView;

    // Variables to get post details when clicked, for updates, delete etc
    private String title;
    private String note;
    private String post_key;

    // Variables for Delete/Update data
    private EditText titleUpdate;
    private EditText noteUpdate;
    private Button updateBtn;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fabBtn = findViewById(R.id.fab_btn);
        toolBar = findViewById(R.id.toolbar_home);

        toolBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("ToDo List");

        //Firebase details
        myAuth = FirebaseAuth.getInstance();
        FirebaseUser user = myAuth.getCurrentUser();
        String user_id = user.getUid();

        myDb = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id);
        // Added after creating the onStart method to load data from the database
        myDb.keepSynced(true);

        //Recyclervieew to show data
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager); // From here create the item_data (of each row) to get data from the adapter, viewholder and onstart method


        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to show the add form in a dialog when fab button is clicked
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                View myView = inflater.inflate(R.layout.add_task, null);
                myAlertDialog.setView(myView);
                AlertDialog dialog = myAlertDialog.create();
                dialog.show();

                //Form properties
                EditText addTaskTitle = myView.findViewById(R.id.addTaskTitle);
                EditText addTaskNote = myView.findViewById(R.id.addTaskNote);
                Button addTaskSaveBtn = myView.findViewById(R.id.addTaskSaveBtn);

                addTaskSaveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = addTaskTitle.getText().toString().trim();
                        String note = addTaskNote.getText().toString().trim();

                        if(TextUtils.isEmpty(title)){
                            addTaskTitle.setError("Field is require");
                            return;
                        }
                        if(TextUtils.isEmpty(note)){
                            addTaskTitle.setError("Field is require");
                            return;
                        }

                        String id = myDb.push().getKey(); // This code makes a post to create a random id of a task then updates that task with the task's data
                        String date = DateFormat.getDateInstance().format(new Date());
                        Data data = new Data(title, note, date, id);
                        myDb.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(), "Task successfully added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTitle(String title){
            TextView myTitle = myView.findViewById(R.id.titleField);
            myTitle.setText(title);
        }

        public void setNote(String note){
            TextView myNote = myView.findViewById(R.id.noteField);
            myNote.setText(note);
        }

        public void setDate(String date){
            TextView myDate = myView.findViewById(R.id.dateField);
            myDate.setText(date);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get data from database
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                myDb
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int i) {
                myViewHolder.setTitle(data.getTitle());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setDate(data.getDate());

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(i).getKey();
                        note = data.getNote();
                        title = data.getTitle();

                        populatedEditFields();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter); // add this line set adapter, to populate data
    }

    // Method to populate update form fields when an item is clicked
    public void populatedEditFields(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myView = inflater.inflate(R.layout.updateinputfields, null);
        alertDialog.setView(myView);
        AlertDialog dialog = alertDialog.create();
        // code above sets up and adds the form tho the dialog which will be shown as well

        // Get data from the update data form
        titleUpdate = myView.findViewById(R.id.updateTitle);
        noteUpdate = myView.findViewById(R.id.updateNote);

        deleteBtn = myView.findViewById(R.id.deleteUpdate);
        updateBtn = myView.findViewById(R.id.saveUpdate);

        titleUpdate.setText(title);
        titleUpdate.setSelection(title.length()); //  To set the cursor to the end of the text

        noteUpdate.setText(note);
        noteUpdate.setSelection(note.length());

        // Onclick listener to update task in the database
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleUpdate.getText().toString().trim();
                String note = noteUpdate.getText().toString().trim();
                String date = DateFormat.getTimeInstance().format(new Date());

                Data data = new Data(title, note, date, post_key);
                myDb.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        // Onclick listener to delete task from database
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show(); // to show the dialog
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutBtn) {
            myAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}