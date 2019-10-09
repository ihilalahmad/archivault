package com.example.archivault.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
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

import java.util.ArrayList;
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

    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mDialog = new Dialog(this);

        //checking whether user is logedin or not
        isUserLoggedIn();

        toolbar = findViewById(R.id.main_toolbar);
        setToolbar();

        projectModelList = new ArrayList<>();
        getProjects();

        mRecyclerView = findViewById(R.id.main_recycleView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

    }

    private void isUserLoggedIn(){

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void getProjects(){

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


                Log.e("ProjectsFetchingErr", error.getMessage());

                if (error instanceof NetworkError) {

                    mProgressDialog.dismiss();

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("No Network Connection")
                            .setCancelable(true)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    getProjects();
                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setNeutralButton("Report a Problem", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", getString(R.string.email), null));
                                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                    intent.putExtra(Intent.EXTRA_TEXT, "");
                                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));

                                }
                            }).create().show();
                }
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

    private void openAddNewProjectPopup(){


    }


    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
            }, 100);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout){

            SharedPrefManager.getInstance(getApplicationContext()).logout();
        }

        if (id == R.id.action_add_project){

            TextView close_popup;
            final EditText et_project_name;
            final EditText et_project_description;
            Button btn_add_project;
            mDialog.setContentView(R.layout.add_new_project_popupwindow);

            close_popup = mDialog.findViewById(R.id.close_popup);
            et_project_name = mDialog.findViewById(R.id.et_new_project_name);
            et_project_description = mDialog.findViewById(R.id.et_new_project_desc);
            btn_add_project = mDialog.findViewById(R.id.btn_add_project);

            close_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mDialog.dismiss();
                }
            });

            btn_add_project.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String title = et_project_name.getText().toString();
                    String amount = et_project_description.getText().toString();

                    if (TextUtils.isEmpty(title)){
                        et_project_name.setError("Please enter project name");
                        et_project_name.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(amount)){

                        et_project_description.setError("Please enter project description");
                        et_project_description.requestFocus();
                        return;
                    }
                }
            });

            mDialog.setCancelable(false);
            mDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
