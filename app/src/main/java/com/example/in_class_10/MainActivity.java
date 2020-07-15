package com.example.in_class_10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity {

    Button LoginButton,SignUpButton;
    EditText email,password;

    String emailLogin, passwordLogin, token, status = "status";
    private final OkHttpClient client = new OkHttpClient();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    String token1,message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.login);


        email = findViewById(R.id.main_email);
        password = findViewById(R.id.main_password);

        LoginButton = findViewById(R.id.main_login);
        SignUpButton = findViewById(R.id.main_signUp);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        token1 = mPreferences.getString("token","");
        Log.d("token",token1);

        if (token1.isEmpty()) {
            Toast.makeText(MainActivity.this, "No info", Toast.LENGTH_SHORT).show();

        }
        else{
            try {
                run();
            } catch (Exception e) {
                Log.d("demo", e.toString());
            }
        }

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin = email.getText().toString();
                passwordLogin = password.getText().toString();
                if (TextUtils.isEmpty(emailLogin)) {
                    email.setError("Email cant be empty");
                } else if (TextUtils.isEmpty(passwordLogin)) {
                    password.setError("Password can't be Empty");
                } else {
                    try {
                        login(emailLogin, passwordLogin);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error logging in", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(String email, String password) throws Exception {
        try {

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/auth/login")
                    .post(formBody)
                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("demo", e.toString());
                    Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try(ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        String body = responseBody.string();
                        JSONObject root = new JSONObject(body);
                        status = root.getString("auth");
                        if(status.equals("error")) {
                            message = root.getString("message");
                        }
                        if(status.equals("true")){
                            token = root.getString("token");
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("demo",e.toString());
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mEditor.putString("token",token);
                            mEditor.commit();

                            Intent intent = new Intent(MainActivity.this, NotesScreenActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        final Request request = new Request.Builder()
                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/auth/me")
                .addHeader("x-access-token", token1)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("failed","fail");
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, NotesScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });
    }




}
