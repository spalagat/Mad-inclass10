package com.example.in_class_10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNoteActivity extends AppCompatActivity {

    TextView add_notes;
    Button post;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private final OkHttpClient client = new OkHttpClient();
    String token_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setTitle("Add a note");

        add_notes = findViewById(R.id.note_edittext);
        post = findViewById(R.id.post_button);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        token_string = mPreferences.getString("token","");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("length of post", String.valueOf(add_notes.length()));
                if (add_notes.length() == 0) {
                  add_notes.setError("The Post should not be empty");
                  add_notes.requestFocus();
                } else {
                    if (add_notes.length() > 1000) {
                        Toast.makeText(AddNoteActivity.this, "Post should be less tham 1000 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        addNotes();
                    }
                }
            }
        });

    }
    public void addNotes(){
        RequestBody formBody = new FormBody.Builder()
                .add("text", add_notes.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/note/post")
                .post(formBody)
                .addHeader("x-access-token",token_string)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("demo", e.toString());
                Toast.makeText(AddNoteActivity.this, "failed to add_note", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Intent intent = new Intent(AddNoteActivity.this, NotesScreenActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

}
