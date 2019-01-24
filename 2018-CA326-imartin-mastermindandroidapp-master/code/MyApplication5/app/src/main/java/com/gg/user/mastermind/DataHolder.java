package com.gg.user.mastermind;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private int height = 10;
    private int width = 4;
    private int timeLimit = 300;
    private boolean allowDuplicates = false;
    private List<String> randSequence = new ArrayList<>();
    private GoogleSignInClient mGoogleSignClient = null;


    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean getAllowDuplicates() {
        return allowDuplicates;
    }
    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public List<String> getRandSequence(){
        return randSequence;
    }

    public void setRandSequence(List<String> randSequence){
        this.randSequence = randSequence;
    }

    public GoogleSignInClient getGoogleSignInClient(){return mGoogleSignClient;}
    public void setGoogleSignInClient(GoogleSignInClient mGoogleSignClient) {this.mGoogleSignClient = mGoogleSignClient;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {
        return holder;
    }
}
