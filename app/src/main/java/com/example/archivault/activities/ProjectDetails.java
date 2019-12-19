package com.example.archivault.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.archivault.R;
import com.example.archivault.adapters.IncomesAdapter;
import com.example.archivault.adapters.ExpensesAdapter;
import com.example.archivault.model.ExpensesModel;
import com.example.archivault.model.IncomesModel;
import com.example.archivault.model.ProjectsSummaryModel;
import com.example.archivault.model.Users;
import com.example.archivault.patterns.MySingleton;
import com.example.archivault.utils.Config;
import com.example.archivault.utils.SharedPrefManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDetails extends AppCompatActivity {

    Toolbar toolbar;

    FloatingActionMenu floatingActionMenu;
    FloatingActionButton fab_add_income;
    FloatingActionButton fab_add_expense;
    FloatingActionButton fab_add_item_expense;


    TextView tv_total_balance;
    TextView tv_income;
    TextView tv_expense;
    TextView toggle_income;
    TextView toggle_expense;
    TextView tv_project_name;


    List<ProjectsSummaryModel> projectsSummaryList;
    List<ExpensesModel> expensesList;
    List<IncomesModel> incomesList;
    List<String> itemList;
    private JSONArray itemsJsonArry;

    RecyclerView mRecyclerView;
    ExpensesAdapter expensesAdapter;
    IncomesAdapter incomesAdapter;
    Context mContext;

    Dialog mDialog;

    String project_id;
    String project_name;

    private ProgressDialog mProgressDialog;

    private ExpensesAdapter.OnItemClickListener expenseListener;
    private IncomesAdapter.OnItemClickListener incomesListener;
    boolean isIncome = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);


        mContext = this;

        toolbar = findViewById(R.id.project_details_toolbar);
        setToolbar();

        getExtras();

        mRecyclerView = findViewById(R.id.project_details_recycleView);
        mRecyclerView.invalidate();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mDialog = new Dialog(this);

        //initializing lists
        projectsSummaryList = new ArrayList<>();
        itemList = new ArrayList<>();

        //initializing textviews and toggle buttons
        tv_total_balance = findViewById(R.id.tv_total_balance);
        tv_income = findViewById(R.id.tv_income);
        tv_expense = findViewById(R.id.tv_expense);
        tv_project_name = findViewById(R.id.tv_project_name);
        toggle_income = findViewById(R.id.toggle_income);
        toggle_expense = findViewById(R.id.toggle_expense);

        tv_project_name.setText(project_name);


        //implementing floating buttons.
        floatingActionMenu = findViewById(R.id.floating_action_menu);
        fab_add_income = findViewById(R.id.fab_add_income);
        fab_add_expense = findViewById(R.id.fab_add_expense);
        fab_add_item_expense = findViewById(R.id.fab_add_item_expense);

        fab_add_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                floatingActionMenu.close(true);
                showIncomePopup();
            }
        });

        fab_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                showExpensePopup();
            }
        });

        fab_add_item_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                showItemExpensePopup();
            }
        });


        //implementing toggle onClicks
        toggle_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIncome = true;
                getIncomeSummary();
//                initViews();
            }
        });
        toggle_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIncome = false;
                getExpenseSummary();
//                initViews();
            }
        });

        getTotalSummary();
        getIncomeSummary();
        getItems();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setToolbar() {

        toolbar.setTitle("Project Summary");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void getExtras() {

        project_id = getIntent().getExtras().getString("project_id");
        project_name = getIntent().getExtras().getString("project_name");
    }


    //Dialog popups
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
            }
        });

        mDialog.setCancelable(false);
        mDialog.show();

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

                Log.i("addingNewsExpense", project_id+ " " +title +" " +amount);

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
            }
        });

        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void showItemExpensePopup(){

        TextView close_popup;
        Spinner item_expense_spinner;
        final EditText et_amount;
        final EditText et_item_quantity;
        Button btn_add_item_expense;
        mDialog.setContentView(R.layout.add_item_expense_popupwindow);

        close_popup = mDialog.findViewById(R.id.close_popup);
        item_expense_spinner = mDialog.findViewById(R.id.item_expense_spinner);
        et_amount = mDialog.findViewById(R.id.et_item_expense_amount);
        et_item_quantity = mDialog.findViewById(R.id.et_item_expense_quantity);
        btn_add_item_expense = mDialog.findViewById(R.id.btn_add_item_expense);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.dismiss();
            }
        });

       final List<String> item_id = new ArrayList<>();

        item_expense_spinner.setAdapter(new ArrayAdapter<>(ProjectDetails.this, android.R.layout.simple_spinner_dropdown_item, itemList));

        item_expense_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                item_id.clear();
                item_id.add(getItemId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_add_item_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = item_id.get(0);
                String amount = et_amount.getText().toString();
                String item_quantity = et_item_quantity.getText().toString();
                Log.i("ArchiItemClickon", id);

                if (TextUtils.isEmpty(item_quantity)){

                    et_item_quantity.setError("Please enter quantity");
                    et_item_quantity.requestFocus();

                    return;
                }

                if (TextUtils.isEmpty(amount)){
                    et_amount.setError("Please enter reason");
                    et_amount.requestFocus();

                    return;
                }

                addNewItemExpense(amount,project_id,id,item_quantity);
            }
        });

        mDialog.setCancelable(false);
        mDialog.show();
    }


    //sending data to Api.
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
                        getTotalSummary();
                        getIncomeSummary();
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
                        getTotalSummary();
                        getExpenseSummary();
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

    private void addNewItemExpense(final String amount, final String projectId, final String itemId, final String item_quantity){

        StringRequest addNewItemExpense = new StringRequest(Request.Method.POST, Config.ADD_NEW_ITEM_EXPENSE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Check", response);

                try {

                    JSONObject obj = new JSONObject(response);

                    if (obj.optString("success").equals("1")){

                        String success_messge = obj.getString("message");
                        Toast.makeText(ProjectDetails.this, success_messge, Toast.LENGTH_SHORT).show();
                        Log.i("SSARegRes",success_messge);
                        getTotalSummary();
                        getExpenseSummary();
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
                params.put("amount", amount);
                params.put("project_id", projectId);
                params.put("item_id", itemId);
                params.put("quantity", item_quantity);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(addNewItemExpense);

    }

    //getting data from api.
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
        MySingleton.getInstance(this).addToRequestQueue(projectSummary);

    }

    private void getIncomeSummary() {

        incomesList = new ArrayList<>();

//        isIncome = true;
        toggle_income.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toggle_expense.setTextColor(getResources().getColor(R.color.disable_color));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading Incomes");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        final StringRequest incomeRequest = new StringRequest(Request.Method.GET, Config.INCOMES_URL+project_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray projectsArray = obj.getJSONArray("data");

                    if (projectsArray.length()==0){

                        Toast.makeText(getApplicationContext(), "No Incomes to Show", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }

                    for (int i=0; i<projectsArray.length(); i++){

                        JSONObject projectsObj = projectsArray.getJSONObject(i);

                        String id = projectsObj.getString("id");
                       String title = projectsObj.getString("comment");
                       String user = projectsObj.getString("added_by");
                       String date = projectsObj.getString("date");
                       String total_amount = projectsObj.getString("amount");

                        //format the date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date convertedDate = new Date();

                        convertedDate = dateFormat.parse(date);
                        SimpleDateFormat sdfnewformat = new SimpleDateFormat("dd-MMM-yyyy");

                        String mDateFormated = sdfnewformat.format(convertedDate);


                        Log.i("IncomeSummary", user +" " + total_amount);

                       IncomesModel incomes = new IncomesModel(id,user,title,mDateFormated,total_amount);
                       incomesList.add(incomes);
                        mProgressDialog.dismiss();
                    }
                    incomesAdapter = new IncomesAdapter(incomesList,mContext,incomesListener);
                    mRecyclerView.setAdapter(incomesAdapter);

                } catch (JSONException e) {
                    Log.i("IncomeSummaryErr", e.getMessage());
                } catch (ParseException e) {
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

        expensesList = new ArrayList<>();

//        isIncome = false;
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

                    if (projectsArray.length()==0){
                        Toast.makeText(getApplicationContext(), "No Expenses to Show", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }

                    for (int i=0; i<projectsArray.length(); i++){

                        JSONObject projectsObj = projectsArray.getJSONObject(i);

                        String title;
                        //check if its item from dropdown or comment in response.
                        if (projectsObj.optString("item_name").equals("0")){
                            title = projectsObj.getString("comment");
                        } else {
                            title = projectsObj.getString("item_name");
                        }

                        String id = projectsObj.getString("id");
                        String user = projectsObj.getString("added_by");
                        String date = projectsObj.getString("date");
                        String total_amount = projectsObj.getString("amount");
                        String quantity = projectsObj.getString("quantity");

                        //format the date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date convertedDate = new Date();

                        convertedDate = dateFormat.parse(date);
                        SimpleDateFormat sdfnewformat = new SimpleDateFormat("dd-MMM-yyyy");

                        String mDateFormated = sdfnewformat.format(convertedDate);

                        Log.i("ExpenseSummary", user +" " + title + " "+ total_amount);

                        ExpensesModel expensesModel = new ExpensesModel(id,title,user,mDateFormated,total_amount, quantity);
                        expensesList.add(expensesModel);

                        mProgressDialog.dismiss();

                    }
                    expensesAdapter = new ExpensesAdapter(expensesList, mContext, expenseListener);
                    mRecyclerView.setAdapter(expensesAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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

        expenseListener = new ExpensesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ExpensesModel expensesModel) {

                new AlertDialog.Builder(ProjectDetails.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are You Sure You Want To Delete This?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (isIncome){
                                    String income_id = expensesModel.getId();
                                    deleteIncome(income_id);
                                } else {
                                    String expense_id = expensesModel.getId();
                                    deleteExpense(expense_id);
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).create().show();
            }
        };
    }

    private void getItems(){

        StringRequest itemRequest = new StringRequest(Request.Method.GET, Config.GET_ITEMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject itemObj = null;

                try{
                    //getting items object from json
                    itemObj = new JSONObject(response);
                    //getting items array from json
                    itemsJsonArry = itemObj.getJSONArray("data");

                    for (int i=0; i<itemsJsonArry.length(); i++){

                        JSONObject items = itemsJsonArry.getJSONObject(i);

                        String item_name = items.getString("item");

                        Log.i("ArchiItemsList", item_name);

                        itemList.add(item_name);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        MySingleton.getInstance(this).addToRequestQueue(itemRequest);

    }

    private String getItemId(int position){

        String item_id = "";

        try {

            JSONObject item_idobj = itemsJsonArry.getJSONObject(position);
            item_id = item_idobj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item_id;
    }


    //deleting incomes and expense methods.
    private void deleteIncome(String income_id){

        StringRequest deleteIncomeReq = new StringRequest(Request.Method.GET, Config.DELETE_INCOME+income_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject deleteObj = new JSONObject(response);

                    if (deleteObj.optString("success").equals("1")){

                        String success_messge = deleteObj.getString("message");
                        Toast.makeText(ProjectDetails.this, success_messge, Toast.LENGTH_SHORT).show();
                        Log.i("DelIncomeErr",success_messge);

                        getTotalSummary();
                        getIncomeSummary();

                    }else if (deleteObj.optString("success").equals("0")){

                        String error_messge = deleteObj.getString("message");
                        Toast.makeText(ProjectDetails.this, error_messge, Toast.LENGTH_SHORT).show();
                        Log.i("DelIncomeErr",error_messge);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ProjectDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DelIncomeErr", error.getMessage());
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
        };
        MySingleton.getInstance(this).addToRequestQueue(deleteIncomeReq);

    }

    private void deleteExpense(String expense_id){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Deleting Expense");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        StringRequest deleteExpenseReq = new StringRequest(Request.Method.GET, Config.DELETE_EXPENSE+expense_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject deleteObj = new JSONObject(response);

                    if (deleteObj.optString("success").equals("1")){

                        String success_messge = deleteObj.getString("message");
                        Toast.makeText(ProjectDetails.this, success_messge, Toast.LENGTH_SHORT).show();
                        Log.i("DelIncomeErr",success_messge);
                        mProgressDialog.dismiss();
                        getTotalSummary();
                        getExpenseSummary();

                    }else if (deleteObj.optString("success").equals("0")){

                        String error_messge = deleteObj.getString("message");
                        Toast.makeText(ProjectDetails.this, error_messge, Toast.LENGTH_SHORT).show();
                        Log.i("DelExpenseErr",error_messge);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ProjectDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DelExpenseErr", error.getMessage());
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
        };
        MySingleton.getInstance(this).addToRequestQueue(deleteExpenseReq);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.projects_details_menu, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!isIncome){

                    expensesAdapter.getFilter().filter(newText);
                }else {

                    incomesAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){

            finish();
            super.onBackPressed();
        }

        if (id == R.id.action_refresh){

            recreate();
        }

        return super.onOptionsItemSelected(item);
    }


}
