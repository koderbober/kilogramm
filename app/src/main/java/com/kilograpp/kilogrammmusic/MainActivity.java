package com.kilograpp.kilogrammmusic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends Activity {

    List<Song> songsList;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager layoutManager;
    DBOpenHelper dbOpenHelper;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsList = getServerSongsList();
        saveSongsListToDB(songsList);

        recyclerView = (RecyclerView) findViewById(R.id.listView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(songsList);
        recyclerView.setAdapter(recyclerViewAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(itemAnimator);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<Song> serverSongsList = getServerSongsList();
                songsList = getDBSongsList();

                for (int i = 0; i < songsList.size(); i++) {
                    if (!serverSongsList.contains(songsList.get(i))) {
                        songsList.remove(i);
                        recyclerViewAdapter.removeItem(i);
                        i = i - 1;
                    }
                }

                for (Song each : serverSongsList) {
                    if (!songsList.contains(each)) {
                        songsList.add(each);
                        recyclerViewAdapter.addItem(each);
                    };
                }
                saveSongsListToDB(songsList);
                Log.d("SongsListSize", songsList.size() + "");

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void onClick(View view) {

        List<Song> serverSongsList = getServerSongsList();
        songsList = getDBSongsList();

        for (int i = 0; i < songsList.size(); i++) {
            if (!serverSongsList.contains(songsList.get(i))) {
                songsList.remove(i);
                recyclerViewAdapter.removeItem(i);
                i = i - 1;
            }
        }

        for (Song each : serverSongsList) {
            if (!songsList.contains(each)) {
                songsList.add(each);
                recyclerViewAdapter.addItem(each);
            };
        }
        saveSongsListToDB(songsList);
        Log.d("SongsListSize", songsList.size() + "");
    }

    public List<Song> getDBSongsList() {
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("music", null, null, null, null, null, null, null);
        List<Song> songsList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Song song = new Song();

                    song.setId(cursor.getInt(cursor.getColumnIndex("id_song")));
                    song.setName(cursor.getString(cursor.getColumnIndex("name_song")));
                    song.setAuthor(cursor.getString(cursor.getColumnIndex("author_song")));

                    songsList.add(song);
                } while (cursor.moveToNext());
            }
        }
        Log.d("DBListCount", songsList.size() + "");
        cursor.close();
        db.close();
        return songsList;
    }

    public void saveSongsListToDB(List<Song> songsList) {
        ContentValues contentValues = new ContentValues();
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("music", null, null);

        for (Song each : songsList) {
            contentValues.put("name_song", each.getName());
            contentValues.put("id_song", each.getId());
            contentValues.put("author_song", each.getAuthor());
            db.insert("music", null, contentValues);
        }
        Log.d("addToDBList", songsList.size() + "");
        db.close();
    }


    public List<Song> getServerSongsList() {
        String json = getSongsJSON();
        List<Song> songsList = new ArrayList<>();
        try {
            JSONArray songsJSON = new JSONArray(json);
            for (int i = 0; i < songsJSON.length(); i++) {
                JSONObject songJSONObject = (JSONObject) songsJSON.get(i);
                Song song = new Song();

                song.setId(songJSONObject.getInt("id"));
                song.setName(songJSONObject.getString("label"));
                song.setAuthor(songJSONObject.getString("author"));

                songsList.add(song);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ServerListCount", songsList.size() + "");
        return songsList;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getSongsJSON() {
        String json = "";

        try {
            URL siteUrl = new URL("http://kilograpp.com:8080/songs/api/songs");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            HttpURLConnection httpURLConnection = (HttpURLConnection) siteUrl.openConnection();
            httpURLConnection.connect();

            try (InputStream inputStream = httpURLConnection.getInputStream();
                 Scanner scanner = new Scanner(inputStream, "UTF-8");) {
                json = scanner.useDelimiter("\\A").next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}