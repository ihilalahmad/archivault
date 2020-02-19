package com.example.archivault.utils;

public class Config {

    // live database
    private static final String BASE_URL = "https://dev.mraheelkhan.com/archivault/api/";

    // local database
    //private static final String BASE_URL = "http://192.168.8.101/archivault/api/";

    public static final String LOGIN_URL = BASE_URL +"login";
    public static final String PROJECTS_URL = BASE_URL +"projects";
    public static final String PROJECTS_SUMMARY_URL = BASE_URL +"summary?project_id=";
    public static final String INCOMES_URL = BASE_URL +"incomes?project_id=";
    public static final String EXPENSES_URL = BASE_URL +"expenses?project_id=";
    public static final String ADD_NEW_INCOME = BASE_URL +"income/store";
    public static final String ADD_NEW_EXPENSE = BASE_URL +"expense/store";
    public static final String GET_ITEMS = BASE_URL +"items";
    public static final String ADD_NEW_ITEM_EXPENSE = BASE_URL +"expensesWithItem";
    public static final String ADD_NEW_ITEM_COMMENT_EXPENSE = BASE_URL +"expensesWithItemComment";
    public static final String DELETE_INCOME = BASE_URL +"income/delete?income_id=";
    public static final String DELETE_EXPENSE = BASE_URL +"expense/delete?expense_id=";
}
