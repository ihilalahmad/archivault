package com.example.archivault.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.example.archivault.R;
import com.example.archivault.adapters.ProjectsAdapter;
import com.example.archivault.interfaces.ItemClickListener;
import com.example.archivault.model.ProjectModel;
import com.example.archivault.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    Toolbar toolbar;
    RecyclerView mRecyclerView;
    List<ProjectModel> projectModelList;
    ProjectsAdapter mAdapter;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        //checking whether user is logedin or not
        isUserLoggedIn();

        toolbar = findViewById(R.id.main_toolbar);
        setToolbar();

        projectModelList = new ArrayList<>();
        addingProjectsToList();

        mRecyclerView = findViewById(R.id.main_recycleView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ProjectsAdapter(projectModelList, mContext);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void isUserLoggedIn(){

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void addingProjectsToList(){

        for (int i=0; i<10; i++){

            ProjectModel model = new ProjectModel("Project " + i);
            projectModelList.add(model);
        }
    }

    private void setToolbar(){

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

    }

    public void openProjectDetails(int index){

        Intent i = new Intent(MainActivity.this, ProjectDetails.class);
        startActivity(i);
    }


}
