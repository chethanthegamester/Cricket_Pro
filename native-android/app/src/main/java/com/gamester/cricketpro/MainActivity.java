package com.gamester.cricketpro;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private final int green = Color.rgb(18, 112, 62);
    private final int dark = Color.rgb(13, 35, 25);
    private final ArrayList<String> teams = new ArrayList<>();
    private final ArrayList<String> players = new ArrayList<>();
    private int runs = 0, wickets = 0, balls = 0;
    private TextView score, over;
    private LinearLayout root, content;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(dark);
        getWindow().setNavigationBarColor(dark);
        teams.add("Team A"); teams.add("Team B");
        buildHome();
    }
    private TextView label(String text, int size) {
        TextView t = new TextView(this); t.setText(text); t.setTextSize(size);
        t.setTextColor(Color.WHITE); t.setPadding(12, 12, 12, 12); return t;
    }
    private Button button(String text, Runnable action) {
        Button b = new Button(this); b.setText(text); b.setAllCaps(false);
        b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green));
        b.setOnClickListener(v -> action.run()); return b;
    }
    private void shell(String title) {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(dark);
        TextView heading = label("🏏  CRICKET PRO\n" + title, 23); heading.setTypeface(null, Typeface.BOLD);
        heading.setBackgroundColor(green); root.addView(heading);
        ScrollView scroll = new ScrollView(this); content = new LinearLayout(this); content.setPadding(14, 12, 14, 24); content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content); root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }
    private void buildHome() {
        shell("Cricket scoring");
        content.addView(label("Your cricket, your scorecard.", 18));
        content.addView(button("Start a match", this::buildScoring));
        content.addView(button("Teams", this::buildTeams));
        content.addView(button("Players", this::buildPlayers));
        content.addView(button("Match history / scorecards", () -> Toast.makeText(this, "Completed match history will appear here as scoring and storage are built.", Toast.LENGTH_LONG).show()));
        content.addView(label("Native Android app • Green theme", 13));
    }
    private void buildTeams() {
        shell("Teams");
        content.addView(label("Teams", 20));
        for (String team : teams) content.addView(label("🛡  " + team, 17));
        content.addView(button("＋ Add team", () -> askText("Team name", value -> { teams.add(value); buildTeams(); })));
        content.addView(button("Back", this::buildHome));
    }
    private void buildPlayers() {
        shell("Players");
        content.addView(label("Players", 20));
        for (String player : players) content.addView(label("👤  " + player, 17));
        content.addView(button("＋ Add player", () -> askText("Player name", value -> { players.add(value); buildPlayers(); })));
        content.addView(button("Back", this::buildHome));
    }
    private void buildScoring() {
        runs = 0; wickets = 0; balls = 0;
        shell("Live scoring");
        score = label("", 32); score.setGravity(Gravity.CENTER); score.setTypeface(null, Typeface.BOLD); score.setBackgroundColor(green);
        over = label("", 18); over.setGravity(Gravity.CENTER);
        content.addView(label("Team A  vs  Team B", 18)); content.addView(score); content.addView(over);
        LinearLayout grid = new LinearLayout(this); grid.setOrientation(LinearLayout.VERTICAL);
        for (int i=0;i<3;i++) {
            LinearLayout row = new LinearLayout(this);
            for (int j=0;j<3;j++) {
                final int n = i*3+j;
                row.addView(button(n==8 ? "WICKET" : String.valueOf(n), () -> addBall(n==8 ? -1 : n)), new LinearLayout.LayoutParams(0, -2, 1));
            }
            grid.addView(row);
        }
        content.addView(grid);
        content.addView(button("Wide (+1)", () -> { runs++; updateScore(); }));
        content.addView(button("No-ball (+1)", () -> { runs++; updateScore(); }));
        content.addView(button("Byes (+1)", () -> { runs++; legalBall(); }));
        content.addView(button("Leg-byes (+1)", () -> { runs++; legalBall(); }));
        content.addView(button("Back to home", this::buildHome)); updateScore();
    }
    private void addBall(int n) {
        if (n < 0) { wickets++; legalBall(); Toast.makeText(this, "Wicket recorded. Next-batter selection is part of the next scoring build.", Toast.LENGTH_SHORT).show(); return; }
        runs += n; legalBall();
    }
    private void legalBall() { balls++; if (balls % 6 == 0) Toast.makeText(this, "Over complete", Toast.LENGTH_SHORT).show(); updateScore(); }
    private void updateScore() {
        if (score != null) score.setText(runs + "/" + wickets);
        if (over != null) over.setText("Overs: " + (balls/6) + "." + (balls%6));
    }
    private interface TextAction { void run(String value); }
    private void askText(String title, TextAction action) {
        android.widget.EditText input = new android.widget.EditText(this); input.setSingleLine(true); input.setHint(title);
        new android.app.AlertDialog.Builder(this).setTitle(title).setView(input)
          .setNegativeButton("Cancel", null).setPositiveButton("Save", (d,w) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) action.run(value);
          }).show();
    }
}
