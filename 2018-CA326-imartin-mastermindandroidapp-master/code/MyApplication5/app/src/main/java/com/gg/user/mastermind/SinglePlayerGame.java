package com.gg.user.mastermind;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;

public class SinglePlayerGame extends AppCompatActivity implements View.OnClickListener {
    private int width = DataHolder.getInstance().getWidth();
    private int height = DataHolder.getInstance().getHeight();

    private Button[][] buttons = new Button[height][width];

    private String[] stringColors = {"B", "R", "G", "K", "P", "O", "Y"};
    private List<String> listColors = new ArrayList<>(Arrays.asList(stringColors));

    private List<String> randSequence = new ArrayList<>();
    private int currentRow = height - 1;

    private boolean allowDuplicates = DataHolder.getInstance().getAllowDuplicates();
    private boolean stop = false;
    private int timeLeft = DataHolder.getInstance().getTimeLimit();

    private String buttonColor;
    Drawable pegColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (height == 12 && width == 6) {
            setContentView(R.layout.game_board_12_6);
        }
        if (height == 12 && width == 4) {
            setContentView(R.layout.game_board_12_4);
        }
        if (height == 10 && width == 6) {
            setContentView(R.layout.game_board_10_6);
        }
        if (height == 10 && width == 4) {
            setContentView(R.layout.game_board_10_4);
        }

        startGame();

        Button button_red = findViewById(R.id.button_color1);
        Button button_blue = findViewById(R.id.button_color2);
        Button button_green = findViewById(R.id.button_color3);
        Button button_pink = findViewById(R.id.button_color4);
        Button button_black = findViewById(R.id.button_color5);
        Button button_orange = findViewById(R.id.button_color7);
        Button button_yellow = findViewById(R.id.button_color8);
        Button button_confirm = findViewById(R.id.button_confirm);
        Button button_quit = findViewById(R.id.quit_game_button);
        Button button_restart = findViewById(R.id.restart_button);


        button_red.setOnClickListener(this);
        button_blue.setOnClickListener(this);
        button_green.setOnClickListener(this);
        button_pink.setOnClickListener(this);
        button_black.setOnClickListener(this);
        button_orange.setOnClickListener(this);
        button_yellow.setOnClickListener(this);
        button_confirm.setOnClickListener(this);
        button_quit.setOnClickListener(this);
        button_restart.setOnClickListener(this);

        for (int i = 0; i < width; i++) {
            Random rnd = new Random();
            String color = listColors.get(rnd.nextInt(listColors.size()));
            if (!allowDuplicates) {
                listColors.remove(color);
            }
            randSequence.add(color);
        }

        for (int j = 0; j < width; j++) {
            String buttonID = "button_" + currentRow + j;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[currentRow][j] = findViewById(resID);
            buttons[currentRow][j].setOnClickListener(this);
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

            case R.id.play_again_button:
                this.recreate();
                break;

            case R.id.quit_to_menu_button:
                Intent quitIntent = new Intent(SinglePlayerGame.this,
                        SettingActivity.class);
                DataHolder.getInstance().setHeight(10);
                DataHolder.getInstance().setWidth(4);
                DataHolder.getInstance().setAllowDuplicates(false);
                DataHolder.getInstance().setTimeLimit(120);
                startActivity(quitIntent);
                break;

            case R.id.restart_button:
                this.recreate();
                break;

            case R.id.quit_game_button:
                Intent quitGameIntent = new Intent(SinglePlayerGame.this,
                        SettingActivity.class);
                DataHolder.getInstance().setHeight(10);
                DataHolder.getInstance().setWidth(4);
                DataHolder.getInstance().setAllowDuplicates(false);
                DataHolder.getInstance().setTimeLimit(120);
                startActivity(quitGameIntent);
                break;

            case R.id.button_confirm:
                if(checkCurrentRow()){
                    for (int i = 0; i < width; i++) {
                        buttons[currentRow][i].setOnClickListener(null);
                    }
                    if(!currentRowCorrect()) {
                        currentRow--;
                        if(currentRow >= 0) {
                            for (int j = 0; j < width; j++) {
                                String buttonID = "button_" + currentRow + j;
                                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                                buttons[currentRow][j] = findViewById(resID);
                                buttons[currentRow][j].setOnClickListener(this);
                            }
                        }
                        else {
                            stop = true;
                            setContentView(R.layout.victory_screen);
                            TextView result = findViewById(R.id.result_textview);
                            result.setText(R.string.loss);
                            setVictoryTextView();
                            initaliseButtons();
                        }
                    }
                    else {
                        stop = true;
                        setContentView(R.layout.victory_screen);
                        TextView result = findViewById(R.id.result_textview);
                        result.setText(R.string.win);
                        setVictoryTextView();
                        initaliseButtons();
                    }
                }
                break;
        }
        if(!(v.getId() == R.id.button_confirm) && !(v.getId() == R.id.play_again_button) && !(v.getId() == R.id.quit_to_menu_button) && !(v.getId() == R.id.quit_game_button)) {
            ((Button) v).setText(buttonColor);
            v.setBackground(pegColor);
        }
    }

    public boolean checkCurrentRow() {
        for (int i = 0; i < width; i++) {
            if (buttons[currentRow][i].getText().equals("")) {
                Toast.makeText(this, "Please fill ever space in row " + (height - currentRow), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!allowDuplicates) {
            for (int i = 0; i < width; i++) {
                for (int j = i + 1; j < width; j++) {
                    if (!(i == j) && buttons[currentRow][i].getText().equals(buttons[currentRow][j].getText())) {
                        Toast.makeText(this, "No duplicates ", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean currentRowCorrect() {
        return numInCorrectPos() == width;
    }

    public int numInCorrectPos() {
        int countCorrectPos = 0;
        int countCorrectCol = 0;

        for (int i = 0; i < width; i++) {
            if (buttons[currentRow][i].getText().equals(randSequence.get(i))) {
                countCorrectPos++;
            }
        }

        for (int i = 0; i < width; i++) {
            CharSequence currentColor = buttons[currentRow][i].getText();
            if (randSequence.contains(currentColor.toString()) && !currentColor.equals(randSequence.get(i))) {
                countCorrectCol++;
            }
        }
        changeTextView(countCorrectPos, countCorrectCol);
        return countCorrectPos;
    }

    public void changeTextView(int countCorrectPos, int countCorrectCol){
        if (currentRow > 0) {
            String TextViewID = "Text_" + currentRow;
            int resID = getResources().getIdentifier(TextViewID, "id", getPackageName());
            final TextView feedbackTextView = findViewById(resID);
            String feedbackText = "RPos: " + countCorrectPos + "\nRC: " + countCorrectCol;
            feedbackTextView.setText(feedbackText);
        }
    }

    void startGame() {
        // run the gameTick() method every second to update the game.
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    return;
                }
                if(!stop) {
                    gameTick();
                    h.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    // Game tick -- update countdown, check if game ended.
    void gameTick() {
        if (timeLeft > 0) {
            --timeLeft;
        }

        // update countdown
        String stringTimer = String.valueOf(timeLeft / 60) + ":";
        if (timeLeft % 60 < 10) {
            stringTimer += "0" + String.valueOf(timeLeft % 60);
        } else {
            stringTimer += String.valueOf(timeLeft % 60);
        }
        ((TextView) findViewById(R.id.countdown)).setText(stringTimer);

        if (timeLeft <= 0) {
            // finish game
            stop = true;
            setContentView(R.layout.victory_screen);
            TextView result = findViewById(R.id.result_textview);
            result.setText(R.string.time_up);
            setVictoryTextView();
            initaliseButtons();
        }
    }

    void setVictoryTextView(){
        TextView code = findViewById(R.id.code_textview);
        TextView time_taken = findViewById(R.id.time_taken_numberview);
        TextView tries_taken = findViewById(R.id.trie_numberview);

        code.setText(randSequence.toString());

        int time = DataHolder.getInstance().getTimeLimit() - timeLeft;
        String time_used = String.valueOf(time / 60) + ":";
        if (time % 60 < 10) {
            time_used += "0" + String.valueOf(time % 60);
        } else {
            time_used += String.valueOf(time % 60);
        }

        time_taken.setText(time_used);

        int tries = DataHolder.getInstance().getHeight() - currentRow;
        tries_taken.setText(String.valueOf(tries));
    }

    void initaliseButtons(){
        Button play_again = findViewById(R.id.play_again_button);
        Button quit = findViewById(R.id.quit_to_menu_button);

        play_again.setOnClickListener(this);
        quit.setOnClickListener(this);
    }
}
