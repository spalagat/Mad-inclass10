package com.example.in_class_10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    Button SignUp,Cancel;
    EditText email,password,firstName,lastName,confirmPassword;
    String token;
    String status="",message="failed to signup";
    private final okhttp3.OkHttpClient client = new OkHttpClient();

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle(R.string.signup);

        firstName = findViewById(R.id.sup_fname);
        lastName = findViewById(R.id.sup_lname);
        email = findViewById(R.id.sup_emailId);
        password = findViewById(R.id.sup_password);
        confirmPassword = findViewById(R.id.sup_confirmPass);

        SignUp=findViewById(R.id.sup_signUp);
        Cancel=findViewById(R.id.sup_cancel);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();


        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TextUtils.isEmpty(firstName.getText().toString())){
                    firstName.setError("First Name Can't be empty");
                }
                else if(TextUtils.isEmpty(lastName.getText().toString())){
                    lastName.setError("Last Name Can't be empty");
                }
                else if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Email Can't be empty");
                }
                else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Password can't be empty");
                }
                else if (TextUtils.isEmpty(confirmPassword.getText().toString())){
                    confirmPassword.setError("Repeat Password can't be empty");
                }
                else if(!(password.getText().toString()).equals(confirmPassword.getText().toString())){

                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else {

                    try {
                        Log.d("checkss","inside main code");
                        RequestBody formBody = new FormBody.Builder()
                                .add("name", firstName.getText().toString()+lastName.getText().toString())
                                .add("email", email.getText().toString())
                                .add("password", password.getText().toString())
                                .build();

                        okhttp3.Request request = new Request.Builder()
                                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/auth/register")
                                .post(formBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("Error in response",e.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String var = response.body().string();
                                Log.d("response","hi");
                                try {
                                    JSONObject root = new JSONObject(var);
                                    status = root.getString("auth");
                                    if(status.equals("error")) {
                                        message = root.getString("message");
                                    }
                                    if(status.equals("true")){
                                        token = root.getString("token");
                                        Log.d("tokennew",token);
                                        //finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("demo",e.toString());
                                }
                                SignUpActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(status.equals("error")){
                                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                        if(status.equals("true")){
                                            mEditor.putString("token",token);
                                            mEditor.commit();
                                            Log.d("token",token);

                                            Intent intent = new Intent(SignUpActivity.this, NotesScreenActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        });



                    } catch (Exception e) {
                        Log.d("demo", e.toString());
                    }
                }

            }
        });


        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
