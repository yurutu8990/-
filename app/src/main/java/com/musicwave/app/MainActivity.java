package com.musicwave.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.media.MediaPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private final String CLIENT_ID = "709fa152";
    private final ArrayList<Track> tracks = new ArrayList<>();

    private LinearLayout list;
    private EditText search;
    private TextView nowPlaying;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildInterface();
        loadTracks("");
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 20);
        root.setBackgroundColor(Color.rgb(10, 11, 13));

        TextView title = new TextView(this);
        title.setText("Music Wave");
        title.setTextColor(Color.WHITE);
        title.setTextSize(32);
        title.setTypeface(null, 1);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Музыка из интернета");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(16);

        root.addView(subtitle);

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setPadding(0, 25, 0, 15);

        search = new EditText(this);
        search.setHint("Найти музыку...");
        search.setHintTextColor(Color.GRAY);
        search.setTextColor(Color.WHITE);
        search.setSingleLine(true);

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(Color.rgb(30, 33, 36));
        searchBg.setCornerRadius(40);
        search.setBackground(searchBg);
        search.setPadding(30, 0, 20, 0);

        searchRow.addView(
                search,
                new LinearLayout.LayoutParams(0, 60, 1)
        );

        Button searchButton = new Button(this);
        searchButton.setText("Найти");

        searchButton.setOnClickListener(v -> {
            String query = search.getText().toString().trim();
            loadTracks(query);
        });

        searchRow.addView(
                searchButton,
                new LinearLayout.LayoutParams(
                        120,
                        60
                )
        );

        root.addView(searchRow);

        nowPlaying = new TextView(this);
        nowPlaying.setText("Ничего не играет");
        nowPlaying.setTextColor(Color.WHITE);
        nowPlaying.setTextSize(17);
        nowPlaying.setGravity(Gravity.CENTER_VERTICAL);
        nowPlaying.setPadding(20, 15, 20, 15);

        GradientDrawable playerBg = new GradientDrawable();
        playerBg.setColor(Color.rgb(28, 31, 33));
        playerBg.setCornerRadius(30);
        nowPlaying.setBackground(playerBg);

        root.addView(
                nowPlaying,
                new LinearLayout.LayoutParams(
                        -1,
                        70
                )
        );

        ScrollView scroll = new ScrollView(this);

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, 15, 0, 100);

        scroll.addView(list);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(root);
    }

    private void loadTracks(String query) {

        list.removeAllViews();

        TextView loading = new TextView(this);
        loading.setText("Загрузка...");
        loading.setTextColor(Color.WHITE);
        loading.setTextSize(18);
        loading.setPadding(20, 30, 20, 30);

        list.addView(loading);

        new Thread(() -> {

            try {

                String encoded = URLEncoder.encode(query, "UTF-8");

                String api =
                        "https://api.jamendo.com/v3.0/tracks/" +
                        "?client_id=" + CLIENT_ID +
                        "&format=json" +
                        "&limit=20" +
                        "&imagesize=200" +
                        "&audioformat=mp32";

                if (!query.isEmpty()) {
                    api += "&search=" + encoded;
                }

                URL url = new URL(api);

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder result = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();

                JSONObject json =
                        new JSONObject(result.toString());

                JSONArray array =
                        json.getJSONArray("results");

                tracks.clear();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject item =
                            array.getJSONObject(i);

                    Track track = new Track();

                    track.name =
                            item.optString(
                                    "name",
                                    "Без названия"
                            );

                    track.artist =
                            item.optString(
                                    "artist_name",
                                    "Неизвестный исполнитель"
                            );

                    track.audio =
                            item.optString(
                                    "audio",
                                    ""
                            );

                    track.image =
                            item.optString(
                                    "image",
                                    ""
                            );

                    tracks.add(track);
                }

                runOnUiThread(() -> showTracks());

            } catch (Exception e) {

                runOnUiThread(() -> {

                    list.removeAllViews();

                    TextView error = new TextView(this);

                    error.setText(
                            "Не удалось загрузить музыку.\n\n" +
                            e.getMessage()
                    );

                    error.setTextColor(Color.WHITE);
                    error.setTextSize(16);
                    error.setPadding(20, 30, 20, 30);

                    list.addView(error);
                });
            }

        }).start();
    }

    private void showTracks() {

        list.removeAllViews();

        if (tracks.isEmpty()) {

            TextView empty = new TextView(this);

            empty.setText("Ничего не найдено");
            empty.setTextColor(Color.WHITE);
            empty.setTextSize(18);
            empty.setPadding(20, 30, 20, 30);

            list.addView(empty);

            return;
        }

        for (int i = 0; i < tracks.size(); i++) {

            Track track = tracks.get(i);

            LinearLayout item =
                    new LinearLayout(this);

            item.setOrientation(
                    LinearLayout.VERTICAL
            );

            item.setPadding(
                    25,
                    20,
                    25,
                    20
            );

            GradientDrawable bg =
                    new GradientDrawable();

            bg.setColor(
                    Color.rgb(28, 31, 33)
            );

            bg.setCornerRadius(30);

            item.setBackground(bg);

            TextView name =
                    new TextView(this);

            name.setText(track.name);
            name.setTextColor(Color.WHITE);
            name.setTextSize(19);
            name.setTypeface(null, 1);

            item.addView(name);

            TextView artist =
                    new TextView(this);

            artist.setText(track.artist);
            artist.setTextColor(Color.LTGRAY);
            artist.setTextSize(15);

            item.addView(artist);

            Button play =
                    new Button(this);

            play.setText("▶ Слушать");

            final int index = i;

            play.setOnClickListener(
                    v -> playTrack(index)
            );

            item.addView(play);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            -1,
                            -2
                    );

            params.setMargins(
                    0,
                    0,
                    0,
                    15
            );

            list.addView(item, params);
        }
    }

    private void playTrack(int index) {

        if (index < 0 || index >= tracks.size()) {
            return;
        }

        Track track = tracks.get(index);

        if (track.audio.isEmpty()) {
            Toast.makeText(
                    this,
                    "У этого трека нет потока",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (player != null) {

            try {
                player.stop();
                player.release();
            } catch (Exception ignored) {
            }

            player = null;
        }

        nowPlaying.setText(
                "Загрузка: " + track.name
        );

        player = new MediaPlayer();

        try {

            player.setDataSource(track.audio);

            player.setOnPreparedListener(mp -> {

                mp.start();

                nowPlaying.setText(
                        "▶ " +
                        track.name +
                        "\n" +
                        track.artist
                );
            });

            player.setOnCompletionListener(mp ->
                    nowPlaying.setText(
                            "Ничего не играет"
                    )
            );

            player.setOnErrorListener(
                    (mp, what, extra) -> {

                        nowPlaying.setText(
                                "Ошибка воспроизведения"
                        );

                        return true;
                    }
            );

            player.prepareAsync();

        } catch (Exception e) {

            nowPlaying.setText(
                    "Ошибка: " + e.getMessage()
            );
        }
    }

    @Override
    protected void onDestroy() {

        if (player != null) {

            try {
                player.stop();
                player.release();
            } catch (Exception ignored) {
            }
        }

        super.onDestroy();
    }

    static class Track {

        String name;
        String artist;
        String audio;
        String image;
    }
}
