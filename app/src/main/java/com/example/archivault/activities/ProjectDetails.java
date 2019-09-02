package com.example.archivault.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.archivault.R;
import com.example.archivault.adapters.ProjectsAdapter;
import com.example.archivault.adapters.ProjectsSummaryAdapter;
import com.example.archivault.model.ProjectModel;
import com.example.archivault.model.ProjectsSummaryModel;
import com.example.archivault.model.SummaryModel;
import com.example.archivault.model.Users;
import com.example.archivault.patterns.MySingleton;
import com.example.archivault.utils.Config;
import com.example.archivault.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDetails extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    FloatingActionButton fab_main;
    FloatingActionButton fab_add_income;
    FloatingActionButton fab_add_expense;
    Float translationY = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private static final String TAG = "MainActivity";

    Boolean isMenuOpen = false;

    TextView tv_total_balance;
    TextView tv_income;
    TextView tv_expense;
    TextView toggle_income;
    TextView toggle_expense;
    TextView tv_project_name;

    String total_balance;
    String income;
    String expense;


    List<ProjectsSummaryModel> projectsSummaryList;
    List<SummaryModel> summaryList;

    RecyclerView mRecyclerView;
    ProjectsSummaryAdapter mAdapter;
    Context mContext;

    Dialog mDialog;

    String project_id;
    String project_name;

    private ProgressDialog mProgressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        mContext = this;

        toolbar = findViewById(R.id.project_details_toolbar);
        setToolbar();

        getExtras();

        Log.i("ProjectIdFromIntent", project_id + " " + project_name);

        //FAB init.
        initFabMenu();

        mDialog = new Dialog(this);

        //initializing lists
        projectsSummaryList = new ArrayList<>();
        summaryList = new ArrayList<>();

        //initializing textviews and toggle buttons
        tv_total_balance = findViewById(R.id.tv_total_balance);
        tv_income = findViewById(R.id.tv_income);
        tv_expense = findViewById(R.id.tv_expense);
        tv_project_name = findViewById(R.id.tv_project_name);
        toggle_income = findViewById(R.id.toggle_income);
        toggle_expense = findViewById(R.id.toggle_expense);

        tv_project_name.setText(project_name);

        //implementing toggle onClicks
        toggle_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toggle_income.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                toggle_expense.setTextColor(getResources().getColor(R.color.disable_color));

                getIncomeSummary();
                initViews();
            }
        });
        toggle_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toggle_expense.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                toggle_income.setTextColor(getResources().getColor(R.color.disable_color));

                getExpenseSummary();
                initViews();
            }
        });

        getTotalSummary();
        getIncomeSummary();

        mRecyclerView = findViewById(R.id.project_details_recycleView);
        mRecyclerView.invalidate();
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setToolbar() {

        toolbar.setTitle("Project Summary");
        setSupportActionBar(toolbar);
    }

    private void getExtras() {

        project_id = getIntent().getExtras().getString("project_id");
        project_name = getIntent().getExtras().getString("project_name");
    }

    //FAB Buttons
    private void initFabMenu() {

        fab_main = findViewById(R.id.fab_main);
        fab_add_income = findViewById(R.id.fab_add_income);
        fab_add_expense = findViewById(R.id.fab_add_expense);

        fab_add_income.setAlpha(0f);
        fab_add_expense.setAlpha(0f);

        fab_add_income.setTranslationY(translationY);
        fab_add_expense.setTranslationY(translationY);

        fab_main.setOnClickListener(this);
        fab_add_income.setOnClickListener(this);
        fab_add_expense.setOnClickListener(this);
    }

    private void openMenu() {

        isMenuOpen = !isMenuOpen;

        fab_main.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fab_add_income.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fab_add_expense.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu() {

        isMenuOpen = !isMenuOpen;

        fab_main.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fab_add_income.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fab_add_expense.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

    }

    //implementing FAB click actions.
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_main:
                Log.i(TAG, "onClick: fab main");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;

            case R.id.fab_add_income:
                Log.i(TAG, "onClick: add income");
                showIncomePopup();
                break;

            case R.id.fab_add_expense:
                Log.i(TAG, "onClick: add expense");
                showExpensePopup();
                break;
        }
    }

    private void showIncomePopup() {

        TextView close_popup;
        final EditText et_reason;
        final EditText et_amount;
        Button btn_add_income;
        mDialog.setContentView(R.layout.add_income_popupwindow);

        close_popup = mDialog.findViewById(R.id.close_popup);
        et_reason = mDialog.findViewById(R.id.et_income_reason);
        et_amount = mDialog.findViewById(R.id.et_income_amount);
        btn_add_income = mDialog.findViewById(R.id.btn_add_income);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.dismiss();
            }
        });

        btn_add_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = et_reason.getText().toString();
                String amount = et_amount.getText().toString();

                Log.i("addingNewsIncome", project_id+ " " +title +" " +amount);

                if (TextUtils.isEmpty(title)){
                    et_reason.setError("Please enter reason");
                    et_reason.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(amount)){

                    et_amount.setError("Please enter amount");
                    et_amount.requestFocus();
                    return;
                }
                addNewIncome(title, amount, project_id);
                getIncomeSummary();
                getTotalSummary();

            }
        });

        mDialog.setCancelable(false);
        mDialog.show();

    }

    private void addNewIncome(final String title, final String amount, final String projectID){

        StringRequest addIncomeRequest = new StringRequest(Request.Method.POST, Config.ADD_NEW_INCOME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Check", response);

                try {

                    JSONObject obj = new JSONObject(response);

                    if (obj.optString("success").equals("1")){

                        String success_messge = obj.getString("message");
                        Toast.makeText(ProjectDetails.this, success_messge, Toast.LENGTH_SHORT).show();
                        Log.i("SSARegRes",success_messge);
                        mAdapter.notifyDataSetChanged();
                        mDialog.dismiss();

                    }else if (obj.optString("success").equals("0")){

                        String error_messge = obj.getString("message");
                        Toast.makeText(ProjectDetails.this, error_messge, Toast.LENGTH_SHORT).show();
                        Log.i("SSARegResErr",error_messge);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProjectDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SSA SignUp ERR", error.getMessage());
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Users users = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                String token_type = users.getToken_type();
                String access_token = users.getUser_token();

                Log.i("TokenFromModelSummary",token_type+" "+access_token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization",token_type+" "+access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                params.put("amount", amount);
                params.put("project_id", projectID);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(addIncomeRequest);
    }

    private void showExpensePopup() {

        TextView close_popup;
        final EditText et_reason;
        final EditText et_amount;
        Button btn_add_expense;
        mDialog.setContentView(R.layout.add_expense_popupwindow);

        close_popup = mDialog.findViewById(R.id.close_popup);
        et_reason = mDialog.findViewById(R.id.et_expense_reason);
        et_amount = mDialog.findViewById(R.id.et_expense_amount);
        btn_add_expense = mDialog.findViewById(R.id.btn_add_expense);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.dismiss();
            }
        });

        btn_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = et_reason.getText().toString();
                String amount = et_amount.getText().toString();

                Log.i("addingNewsIncome", project_id+ " " +title +" " +amount);

                if (TextUtils.isEmpty(title)){
                    et_reason.setError("Please enter reason");
                    et_reason.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(amount)){

                    et_amount.setError("Please enter amount");
                    et_amount.requestFocus();
                    return;
                }
                addNewExpense(title, amount, project_id);
                getExpenseSummary();
                getTotalSummary();
            }
        });

        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void addNewExpense(final String title, final String amount, final String projectID){

        StringRequest addIncomeRequest = new StringRequest(Request.Method.POST, Config.ADD_NEW_EXPENSE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Check", response);

                try {

                    JSONObject obj = new JSONObject(response);

                    if (obj.optString("success").equals("1")){

                        String success_messge = obj.getString("message");
                        Toast.makeText(ProjectDetails.this, success_messge, Toast.LENGTH_SHORT).show();
                        Log.i("SSARegRes",success_messge);
                        mAdapter.notifyDataSetChanged();
                        mDialog.dismiss();

                    }else if (obj.optString("success").equals("0")){

                        String error_messge = obj.getString("message");
                        Toast.makeText(ProjectDetails.this, error_messge, Toast.LENGTH_SHORT).show();
                        Log.i("SSARegResErr",error_messge);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProjectDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SSA SignUp ERR", error.getMessage());
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Users users = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                String token_type = users.getToken_type();
                String access_token = users.getUser_token();

                Log.i("TokenFromModelSummary",token_type+" "+access_token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization",token_type+" "+access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                params.put("amount", amount);
                params.put("project_id", projectID);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(addIncomeRequest);
    }

    //Getting data from Api's.
    private void initViews() {

        mRecyclerView = findViewById(R.id.project_details_recycleView);
        mRecyclerView.invalidate();
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void getTotalSummary() {

        // Display a progress dialog
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setMessage("Loading Projects");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.show();

        JsonObjectRequest projectSummary = new JsonObjectRequest(Request.Method.GET, Config.PROJECTS_SUMMARY_URL+project_id,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject obj = response.getJSONObject("data");
                            String incomes = obj.getString("incomes");
                            String expenses = obj.getString("expenses");
                            String balance = obj.getString("balance");


                            tv_total_balance.setText(balance);
                            tv_income.setText(incomes);
                            tv_expense.setText(expenses);

//                            mProgressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                       mProgressDialog.dismiss();
                       Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Users users = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                String token_type = users.getToken_type();
                String access_token = users.getUser_token();

                Log.i("TokenFromModelSummary",token_type+" "+access_token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization",token_type+" "+access_token);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(projectSummary);

    }

    private void getIncomeSummary() {

        summaryList.clear();
        toggle_income.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toggle_expense.setTextColor(getResources().getColor(R.color.disable_color));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading Incomes");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        StringRequest incomeRequest = new StringRequest(Request.Method.GET, Config.INCOMES_URL+project_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray projectsArray = obj.getJSONArray("data");

                    for (int i=0; i<projectsArray.length(); i++){

                        JSONObject projectsObj = projectsArray.getJSONObject(i);

                       String title = projectsObj.getString("comment");
                       String user = projectsObj.getString("added_by");
                       String date = projectsObj.getString("date");
                       String total_amount = projectsObj.getString("amount");

                       Log.i("IncomeSummary", user +" " + total_amount);

                        SummaryModel summaryModel = new SummaryModel(title,user,date,total_amount);
                        summaryList.add(summaryModel);
//                        mAdapter.notifyDataSetChanged();
                        mProgressDialog.dismiss();
                    }
                    mAdapter = new ProjectsSummaryAdapter(summaryList, mContext);
                    mRecyclerView.setAdapter(mAdapter);

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

                Log.i("TokenFromModelSummary",token_type+" "+access_token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization",token_type+" "+access_token);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(incomeRequest);

    }

    private void getExpenseSummary() {

        summaryList.clear();
        toggle_expense.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toggle_income.setTextColor(getResources().getColor(R.color.disable_color));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading Expenses");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        StringRequest expensesRequest = new StringRequest(Request.Method.GET, Config.EXPENSES_URL+project_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray projectsArray = obj.getJSONArray("data");

                    for (int i=0; i<projectsArray.length(); i++){

                        JSONObject projectsObj = projectsArray.getJSONObject(i);

                        String title = projectsObj.getString("comment");
                        String user = projectsObj.getString("added_by");
                        String date = projectsObj.getString("date");
                        String total_amount = projectsObj.getString("amount");

                        Log.i("IncomeSummary", user +" " + total_amount);

                        SummaryModel summaryModel = new SummaryModel(title,user,date,total_amount);
                        summaryList.add(summaryModel);

                        mProgressDialog.dismiss();

                    }
                    mAdapter = new ProjectsSummaryAdapter(summaryList, mContext);
                    mRecyclerView.setAdapter(mAdapter);


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

                Log.i("TokenFromModelSummary",token_type+" "+access_token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization",token_type+" "+access_token);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(expensesRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
