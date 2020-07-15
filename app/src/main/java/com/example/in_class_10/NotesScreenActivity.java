package com.example.in_class_10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NotesScreenActivity extends AppCompatActivity {

    TextView name;
    Button add_note;
    ListView listView;
    ImageView logout;

    private final OkHttpClient client = new OkHttpClient();

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    ArrayList<User> result = new ArrayList<>();
    ArrayAdapter notes_Adapter;

    static String THREAD_KEY;

    String newname;



    String token1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_screen);
        setTitle("Notes");


        logout = findViewById(R.id.imageView);
        listView = findViewById(R.id.note_list_view);
        add_note = findViewById(R.id.add_note);
        name = findViewById(R.id.user_name);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        getnotes();
        getname();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    run();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NotesScreenActivity.this, AddNoteActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public void getname() {

        token1 = mPreferences.getString("token", "");
        final Request request = new Request.Builder()
                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/auth/me")
                .addHeader("x-access-token", token1)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("failed", "fail");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String body = responseBody.string();
                    JSONObject root = new JSONObject(body);
                    newname = root.getString("name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NotesScreenActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        name.setText(newname);


                        // Stuff that updates the UI

                    }
                });
            }

        });
    }

    public  void getnotes(){
        token1 = mPreferences.getString("token","");
        final Request request = new Request.Builder()
                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/note/getall")
                .addHeader("x-access-token",token1)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("failed","fail");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    Headers responseHeaders = response.headers();
                    String body = responseBody.string();
                    JSONObject root = new JSONObject(body);
                    JSONArray threads = root.getJSONArray("notes");
                    //Log.d("threads",threads.toString());
                    //for (int i = threads.length(); i > -1; i--)
                    for (int i = 0; i<threads.length(); i++) {

                        JSONObject threadJson = threads.getJSONObject(i);
                        User user = new User();
                        user.id = threadJson.getString("_id");
                        user.user_id = threadJson.getString("userId");
                        user.text = threadJson.getString("text");
                        result.add(user);

                        Log.d("info",result.toString());
                    }

                    Log.d("demo", result.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NotesScreenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            notes_Adapter = new notes_Adapter(NotesScreenActivity.this, R.layout.notes_list, result);
                            listView.setAdapter(notes_Adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    User thread = result.get(i);
                                    Intent intent = new Intent(NotesScreenActivity.this, DisplayNoteActivity.class);
                                    intent.putExtra(THREAD_KEY,thread);
                                    //intent.putExtra(USER_KEY, Cuser);
                                    startActivity(intent);
                                }
                            });
                        } catch (Exception e) {
                            Log.d("demo", e.toString());
                        }
                    }
                });

            }
        });
    }
    public void run() throws Exception {
        final Request request = new Request.Builder()
                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/auth/logout")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("failed","fail");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String prefTag = "token";
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
                mPreferences.edit().remove(prefTag).commit();

                Intent intent = new Intent(NotesScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
