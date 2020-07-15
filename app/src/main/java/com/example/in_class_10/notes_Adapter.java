package com.example.in_class_10;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class notes_Adapter extends ArrayAdapter<User> {
    List<User> objects;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    String token1;


    private final OkHttpClient client = new OkHttpClient();




    /*public notes_Adapter(@NonNull Context context, int resource, @NonNull List<userinfo> objects) {
        super(context, resource, objects);
    }*/

    public notes_Adapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User musicitems = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notes_list, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text);

        textView.setText(musicitems.text);

        ImageView delete = (ImageView) convertView.findViewById(R.id.imageView2);

        delete.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                try {

                    mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    mEditor = mPreferences.edit();

                    token1 = mPreferences.getString("token","");
                    final User msg = getItem(position);
                    String thread_id = msg.id;
                    Log.d("demo",msg.toString());
                    delete(thread_id);
                    objects.remove(position);

                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        return convertView;
    }

    public void delete(String message_id) throws Exception {
        Request request = new Request.Builder()
                .url("http://ec2-3-91-77-16.compute-1.amazonaws.com:3000/api/note/delete?msgId=" + message_id)
                .addHeader("x-access-token", token1)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("call fail message", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String var = response.body().string();
                Log.d("demo", var);
            }
        });
    }
}
