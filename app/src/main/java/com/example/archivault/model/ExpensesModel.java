package com.example.archivault.model;

public class ExpensesModel {

    private String expense_by_user;
    private String expense_reason;
    private String expense_date;
    private String total_expense;


    public ExpensesModel(String expense_by_user, String expense_reason, String expense_date, String total_expense) {
        this.expense_by_user = expense_by_user;
        this.expense_reason = expense_reason;
        this.expense_date = expense_date;
        this.total_expense = total_expense;
    }

    public String getExpense_by_user() {
        return expense_by_user;
    }

    public String getExpense_reason() {
        return expense_reason;
    }

    public String getExpense_date() {
        return expense_date;
    }

    public String getTotal_expense() {
        return total_expense;
    }
}
