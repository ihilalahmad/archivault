package com.example.archivault.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.archivault.R;
import com.example.archivault.model.Users;
import com.example.archivault.patterns.MySingleton;
import com.example.archivault.utils.Config;
import com.example.archivault.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText et_user_email;
    EditText et_user_password;

    Toolbar toolbar;
    Button btn_login;
    ProgressBar login_progressBar;

    String st_login_email;
    String st_login_password;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.login_toolbar);
        setToolbar();

        et_user_email = findViewById(R.id.et_user_email);
        et_user_password = findViewById(R.id.et_user_password);
        btn_login = findViewById(R.id.btn_login);
        login_progressBar = findViewById(R.id.login_progress);
        login_progressBar.setVisibility(View.GONE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_login.setVisibility(View.GONE);
                login_progressBar.setVisibility(View.VISIBLE);
                loginStudent();

            }
        });
    }

    private void setToolbar(){

        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
    }


    private void loginStudent(){

        st_login_email = et_user_email.getText().toString();
        st_login_password = et_user_password.getText().toString();

        if (TextUtils.isEmpty(st_login_email)) {
            et_user_email.setError("Please enter your email");
            et_user_email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(st_login_email).matches()) {
            et_user_email.setError("Enter a valid email");
            et_user_email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(st_login_password)) {
            et_user_password.setError("Enter a password");
            et_user_password.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("SAALoginResponse", response);

                        try{

                            JSONObject jsonObject = new JSONObject(response);

                            int success_message = jsonObject.getInt("success");

                            if (success_message == 1) {

                                String access_token = jsonObject.getString("access_token");
                                String token_type = jsonObject.getString("token_type");
                                Users users = new Users(st_login_email, st_login_password,access_token,token_type);
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(users);

                                Log.i("SSAToken",   access_token);

                                    login_progressBar.setVisibility(View.GONE);
                                    finish();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));



                            }else if (success_message == 0){

                                login_progressBar.setVisibility(View.GONE);
                                String loginErrMsg = jsonObject.getString("message");
                                Toast.makeText(LoginActivity.this,loginErrMsg,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SSALoginErr", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",st_login_email);
                params.put("password",st_login_password);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
