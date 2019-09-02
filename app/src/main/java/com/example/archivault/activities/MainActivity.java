package com.example.archivault.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.archivault.R;
import com.example.archivault.adapters.ProjectsAdapter;
import com.example.archivault.interfaces.ItemClickListener;
import com.example.archivault.model.ProjectModel;
import com.example.archivault.model.Users;
import com.example.archivault.patterns.MySingleton;
import com.example.archivault.utils.Config;
import com.example.archivault.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    Toolbar toolbar;
    RecyclerView mRecyclerView;
    List<ProjectModel> projectModelList;
    ProjectsAdapter mAdapter;
    Context mContext;
    private ProgressDialog mProgressDialog;

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

    }

    private void isUserLoggedIn(){

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void addingProjectsToList(){

        // Display a progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading Projects");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        StringRequest projectRequest = new StringRequest(Request.Method.GET, Config.PROJECTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray projectsArray = obj.getJSONArray("data");

                    for (int i=0; i<projectsArray.length(); i++){

                        JSONObject projectsObj = projectsArray.getJSONObject(i);

                        String project_id = projectsObj.getString("id");
                        String project_name = projectsObj.getString("project_title");

                        Log.i("ArchiProjects", project_id + " " + project_name);

                        ProjectModel projectModel = new ProjectModel(project_id,project_name);
                        projectModelList.add(projectModel);
                    }

                    mAdapter = new ProjectsAdapter(projectModelList, mContext);
                    mRecyclerView.setAdapter(mAdapter);

                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Users users = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                String token_type = users.getToken_type();
                String access_token = users.getUser_token();

                Log.i("TokenFromModel",token_type+" "+access_token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization",token_type+" "+access_token);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(projectRequest);

    }

    private void setToolbar(){

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

    }

    public void openProjectDetails(int index){

        Intent i = new Intent(MainActivity.this, ProjectDetails.class);
        i.putExtra("project_id", projectModelList.get(index).getProject_id());
        i.putExtra("project_name",projectModelList.get(index).getProject_name());
        startActivity(i);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
