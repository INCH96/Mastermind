package com.gg.user.mastermind;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LocalCodeSelect extends AppCompatActivity implements View.OnClickListener{

    private int width = DataHolder.getInstance().getWidth();

    private Button[] buttons = new Button[width];

    private List<String> randSequence = new ArrayList<>();

    private boolean allowDuplicates = DataHolder.getInstance().getAllowDuplicates();

    private String buttonColor;
    private Drawable pegColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(width == 4) {
            setContentView(R.layout.code_select_length_4);
        }
        else {
            setContentView(R.layout.code_select_length_6);
        }
        Button button_red = findViewById(R.id.button_color1);
        Button button_blue = findViewById(R.id.button_color2);
        Button button_green = findViewById(R.id.button_color3);
        Button button_pink = findViewById(R.id.button_color4);
        Button button_black = findViewById(R.id.button_color5);
        Button button_orange = findViewById(R.id.button_color7);
        Button button_yellow = findViewById(R.id.button_color8);
        Button button_confirm = findViewById(R.id.button_confirm);

        button_red.setOnClickListener(this);
        button_blue.setOnClickListener(this);
        button_green.setOnClickListener(this);
        button_pink.setOnClickListener(this);
        button_black.setOnClickListener(this);
        button_orange.setOnClickListener(this);
        button_yellow.setOnClickListener(this);
        button_confirm.setOnClickListener(this);

        for (int j = 0; j < width; j++) {
            String buttonID = "button_0" + j;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[j] = findViewById(resID);
            buttons[j].setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_color1:
                buttonColor = "R";
                pegColor = getDrawable(R.drawable.red_pin40);
                break;

            case R.id.button_color2:
                buttonColor = "B";
                pegColor = getDrawable(R.drawable.blue_pin40);
                break;

            case R.id.button_color3:
                buttonColor = "G";
                pegColor = getDrawable(R.drawable.green_pin40);
                break;

            case R.id.button_color4:
                buttonColor = "P";
                pegColor = getDrawable(R.drawable.pink_pin40);
                break;

            case R.id.button_color5:
                buttonColor = "K";
                pegColor = getDrawable(R.drawable.black_pin40);
                break;

            case R.id.button_color7:
                buttonColor = "O";
                pegColor = getDrawable(R.drawable.orange_pin40);
                break;

            case R.id.button_color8:
                buttonColor = "Y";
                pegColor = getDrawable(R.drawable.yellow_pin40);
                break;

            case R.id.button_confirm:
                if(checkCurrentRow()) {
                    for(int i = 0; i < width; i++){
                        randSequence.add(buttons[i].getText().toString());
                    }
                    DataHolder.getInstance().setRandSequence(randSequence);
                    Intent myIntent = new Intent(LocalCodeSelect.this,
                            LocalMultiPlayerGame.class);
                    startActivity(myIntent);
                }
                break;
        }
        if(!(v.getId() == R.id.button_confirm)) {
            ((Button) v).setText(buttonColor);
            v.setBackground(pegColor);
        }
    }

    public boolean checkCurrentRow() {
        for(int i = 0; i < width; i++) {
            if (buttons[i].getText().equals("")) {
                Toast.makeText(this,"Please fill ever space in row ", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(!allowDuplicates){
            for(int i = 0; i < width; i++) {
                for(int j = i+1; j < width; j++) {
                    if(!(i == j) && buttons[i].getText().equals(buttons[j].getText()) ){
                        Toast.makeText(this,"No duplicates ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }
}