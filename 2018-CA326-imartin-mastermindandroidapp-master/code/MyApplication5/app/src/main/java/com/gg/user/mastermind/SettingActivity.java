package com.gg.user.mastermind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button button_single_player_start = findViewById(R.id.btn_single_start);
        Button button_local_start = findViewById(R.id.btn_local_start);
        Button button_online_start = findViewById(R.id.btn_online_start);
        Button num_of_pegs_btn = findViewById(R.id.num_of_pegs_toggle_btn);
        Button num_of_tries_btn = findViewById(R.id.num_of_tries_toggle_btn);
        Button timeLimit_btn = findViewById(R.id.time_setting_toggle_btn);
        Button duplicates_btn = findViewById(R.id.duplicate_toggle_btn);
        button_local_start.setOnClickListener(this);
        button_online_start.setOnClickListener(this);
        button_single_player_start.setOnClickListener(this);
        num_of_pegs_btn.setOnClickListener(this);
        num_of_tries_btn.setOnClickListener(this);
        timeLimit_btn.setOnClickListener(this);
        duplicates_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_single_start:
                Intent singleIntent = new Intent(SettingActivity.this,
                        SinglePlayerGame.class);
                startActivity(singleIntent);
                break;

            case R.id.btn_local_start:
                Intent localIndent = new Intent(SettingActivity.this,
                        LocalCodeSelect.class);
                startActivity(localIndent);
                break;

            case R.id.btn_online_start:
                Intent onlineIntent = new Intent(SettingActivity.this,
                        OnlineMultiPlayerGame.class);
                startActivity(onlineIntent);
                break;

            case R.id.num_of_pegs_toggle_btn:
                if (DataHolder.getInstance().getWidth() == 4) {
                    DataHolder.getInstance().setWidth(6);
                } else {
                    DataHolder.getInstance().setWidth(4);
                }
                break;

            case R.id.num_of_tries_toggle_btn:
                if (DataHolder.getInstance().getHeight() == 10) {
                    DataHolder.getInstance().setHeight(12);
                } else {
                    DataHolder.getInstance().setHeight(10);
                }
                break;

            case R.id.time_setting_toggle_btn:
                if (DataHolder.getInstance().getTimeLimit() == 180) {
                    DataHolder.getInstance().setTimeLimit(240);
                } else {
                    DataHolder.getInstance().setTimeLimit(180);
                }
                break;

            case R.id.duplicate_toggle_btn:
                if (!DataHolder.getInstance().getAllowDuplicates()) {
                    DataHolder.getInstance().setAllowDuplicates(true);
                } else {
                    DataHolder.getInstance().setAllowDuplicates(false);
                }
                break;
        }
    }
}
