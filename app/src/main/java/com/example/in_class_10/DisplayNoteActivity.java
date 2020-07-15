package com.example.in_class_10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayNoteActivity extends AppCompatActivity {


    TextView display;
    Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);
        setTitle("Displaying Notes");


        display = findViewById(R.id.notes_text);
        close = findViewById(R.id.close_button);

        User thread = (User) getIntent().getExtras().getSerializable(NotesScreenActivity.THREAD_KEY);

        display.setText(thread.getText());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayNoteActivity.this, NotesScreenActivity.class);
                startActivity(i);
                finish();
            }
        });


    }
}
