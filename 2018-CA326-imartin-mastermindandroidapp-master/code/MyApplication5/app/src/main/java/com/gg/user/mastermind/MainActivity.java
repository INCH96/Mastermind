package com.gg.user.mastermind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.activity_main);

        // Locate the button in activity_main.xml
        Button nextpage_button = findViewById(R.id.btn_nextpage);

        // Capture button clicks
        nextpage_button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this,
                        SettingActivity.class);
                DataHolder.getInstance().setHeight(10);
                DataHolder.getInstance().setWidth(4);
                DataHolder.getInstance().setAllowDuplicates(false);
                DataHolder.getInstance().setTimeLimit(180);
                startActivity(myIntent);
            }
        });

        Button settings_button = findViewById(R.id.btn_settingpage);
        settings_button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this,
                        RulesPage.class);
                startActivity(myIntent);
            }
        });

        Button quit_button = findViewById(R.id.btn_quit);
        quit_button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Quit");
                builder.setMessage("Do you want to quit the App?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}