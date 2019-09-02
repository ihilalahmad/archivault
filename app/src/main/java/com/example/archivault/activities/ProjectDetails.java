package com.example.archivault.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.archivault.R;
import com.example.archivault.adapters.ProjectsSummaryAdapter;
import com.example.archivault.model.ProjectsSummaryModel;
import com.example.archivault.model.SummaryModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetails extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton fab_main;
    FloatingActionButton fab_add_income;
    FloatingActionButton fab_add_expense;
    Animation fab_open;
    Animation fab_close;
    Animation rotate_clockwise;
    Animation rotate_anticlockwise;
    boolean isOpen = false;
    Float translationY = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private static final String TAG = "MainActivity";

    Boolean isMenuOpen = false;

    TextView tv_total_balance;
    TextView tv_income;
    TextView tv_expense;
    TextView toggle_income;
    TextView toggle_expense;

    String total_balance;
    String income;
    String expense;


    List<ProjectsSummaryModel> projectsSummaryList;
    List<SummaryModel> summaryList;

    RecyclerView mRecyclerView;
    ProjectsSummaryAdapter mAdapter;
    Context mContext;

    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        mContext = this;

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
        toggle_income = findViewById(R.id.toggle_income);
        toggle_expense = findViewById(R.id.toggle_expense);

        //implementing toggle onClicks
        toggle_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle_income.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                toggle_expense.setTextColor(getResources().getColor(R.color.disable_color));

                getIncomeSummary();
                initViews();
            }
        });
        toggle_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle_expense.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                toggle_income.setTextColor(getResources().getColor(R.color.disable_color));

                getExpenseSummary();
                initViews();
            }
        });

        getTotalSummary();
        getIncomeSummary();
        initViews();

    }

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

    private void initViews(){

        mRecyclerView = findViewById(R.id.project_details_recycleView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ProjectsSummaryAdapter(summaryList, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getTotalSummary(){

        ProjectsSummaryModel projectsSummaryModel = new ProjectsSummaryModel("150000","25000","1200");
        projectsSummaryList.add(projectsSummaryModel);


        total_balance = projectsSummaryList.get(0).getTotal_balance();
        income = projectsSummaryList.get(0).getIncomes();
        expense = projectsSummaryList.get(0).getExpenses();

        tv_total_balance.setText(total_balance);
        tv_income.setText(income);
        tv_expense.setText(expense);
    }

    private void getIncomeSummary(){
        summaryList.clear();

        for (int i=0; i<10; i++){

            SummaryModel model = new SummaryModel("Freelance " + i, "Income Users " + i, "Date " + i, "150000");
            summaryList.add(model);
        }
    }

    private void getExpenseSummary(){

        summaryList.clear();

        for (int i=0; i<10; i++){

            SummaryModel model = new SummaryModel("Fuel " + i, "Expense Users " + i, "Date " + i, "15000");
            summaryList.add(model);
        }
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

    private void showIncomePopup(){

        TextView close_popup;
        EditText et_reason;
        EditText et_amount;
        mDialog.setContentView(R.layout.add_income_popupwindow);

        close_popup = mDialog.findViewById(R.id.close_popup);
        et_reason = mDialog.findViewById(R.id.et_income_reason);
        et_amount = mDialog.findViewById(R.id.et_income_amount);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.dismiss();
            }
        });

        mDialog.show();

    }

    private void showExpensePopup(){

        TextView close_popup;
        EditText et_reason;
        EditText et_amount;
        mDialog.setContentView(R.layout.add_expense_popupwindow);

        close_popup = mDialog.findViewById(R.id.close_popup);
        et_reason = mDialog.findViewById(R.id.et_expense_reason);
        et_amount = mDialog.findViewById(R.id.et_expense_amount);

        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }
}
