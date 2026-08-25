package com.musicwave.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    TextView text(String value, float size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), 0);
        root.setBackgroundColor(Color.rgb(10, 12, 13));

        TextView title = text("Music Wave", 28);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(55)));

        TextView subtitle = text(
                "Любимые артисты   •   Альбомы   •   Вайб",
                14
        );
        subtitle.setTextColor(Color.rgb(170, 175, 175));
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(45)));

        TextView wave = text("     ▶     Моя волна", 19);
        wave.setBackground(bg(Color.rgb(119, 216, 192), 30));
        wave.setTextColor(Color.BLACK);

        wave.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Моя волна запущена",
                        Toast.LENGTH_SHORT
                ).show()
        );

        root.addView(
                wave,
                new LinearLayout.LayoutParams(-1, dp(60))
        );

        TextView liked = text("♥   Лайкнутые треки", 18);
        liked.setBackground(bg(Color.rgb(25, 29, 30), 20));

        LinearLayout.LayoutParams card =
                new LinearLayout.LayoutParams(-1, dp(65));
        card.topMargin = dp(18);

        root.addView(liked, card);

        TextView albums = text(
                "     Альбомы от любимых артистов",
                18
        );
        albums.setBackground(bg(Color.rgb(25, 29, 30), 20));

        LinearLayout.LayoutParams albumParams =
                new LinearLayout.LayoutParams(-1, dp(65));
        albumParams.topMargin = dp(10);

        root.addView(albums, albumParams);

        Space space = new Space(this);
        root.addView(
                space,
                new LinearLayout.LayoutParams(1, 0, 1)
        );

        TextView player = text(
                "  ▶    Ничего не играет",
                16
        );
        player.setBackground(bg(Color.rgb(32, 38, 38), 22));

        root.addView(
                player,
                new LinearLayout.LayoutParams(-1, dp(65))
        );

        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);

        String[] items = {
                "⌕\nПоиск",
                "⌂\nГлавная",
                "♡\nМоя музыка",
                "◯\nПрофиль"
        };

        for (String item : items) {
            TextView n = text(item, 12);
            n.setGravity(Gravity.CENTER);
            nav.addView(
                    n,
                    new LinearLayout.LayoutParams(0, dp(70), 1)
            );
        }

        root.addView(nav);

        setContentView(root);
    }
}
