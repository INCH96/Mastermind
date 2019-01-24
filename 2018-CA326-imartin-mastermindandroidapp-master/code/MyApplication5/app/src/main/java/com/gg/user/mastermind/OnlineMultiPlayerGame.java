package com.gg.user.mastermind;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OnlineMultiPlayerGame extends Activity implements View.OnClickListener {

    // Request codes
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;
    private InvitationsClient mInvitationsClient = null;

    // Room ID of current game
    String mRoomId = null;

    // Holds room config
    RoomConfig mRoomConfig;

    boolean mMultiplayer = true;

    // Users in the current room
    ArrayList<Participant> mParticipants = null;

    // My ID for the current game, null until in room
    String mMyId = null;

    // ID of incoming invitation received via the listener
    String mIncomingInvitationId = null;

    //width and height of the board
    private int width = 4;
    private int height = 10;

    //Array of buttons for the boards and code select
    private Button[][] buttons = new Button[height][width];
    private Button[] code = new Button[width];

    //Array used to pick the random sequence for round 3
    private String[] stringColors = {"B", "R", "G", "K", "P", "O", "Y"};
    private List<String> listColors = new ArrayList<>(Arrays.asList(stringColors));

    //Array used to store the current code to break
    private List<String> randSequence = new ArrayList<>();

    //Variables used by game
    private int currentRow = height - 1;
    private boolean allowDuplicates = false;
    private int timeLeft = 180;
    private int round = 0;
    private boolean startOfGame = true;
    private String buttonColor;
    private Drawable pegColour;

    //Used to store message to be sent to opponent
    byte[] mMsgBuf = new byte[width + 1];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_multi_player_game);

        // Sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        // set up a click listener for everything we care about
        findViewById(R.id.button_quick_game).setOnClickListener(this);
        findViewById(R.id.button_accept_popup_invitation).setOnClickListener(this);
        findViewById(R.id.button_invite_players).setOnClickListener(this);
        findViewById(R.id.bn_login).setOnClickListener(this);
        findViewById(R.id.button_see_invitations).setOnClickListener(this);

        pegColour = getDrawable(R.drawable.pin40);
        switchToMainScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();

        signInSilently();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregister our listeners.
        if (mInvitationsClient != null) {
            mInvitationsClient.unregisterInvitationCallback(mInvitationCallback);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //Menu_buttons
            case R.id.bn_login:
                startSignInIntent();
                break;
            case R.id.button_invite_players:
                switchToScreen(R.id.screen_wait);
                mRealTimeMultiplayerClient.getSelectOpponentsIntent(1, 3).addOnSuccessListener(
                        new OnSuccessListener<Intent>() {
                            @Override
                            public void onSuccess(Intent intent) {
                                startActivityForResult(intent, RC_SELECT_PLAYERS);
                            }
                        });
                break;
            case R.id.button_see_invitations:
                switchToScreen(R.id.screen_wait);

                mInvitationsClient.getInvitationInboxIntent().addOnSuccessListener(
                        new OnSuccessListener<Intent>() {
                            @Override
                            public void onSuccess(Intent intent) {
                                startActivityForResult(intent, RC_INVITATION_INBOX);
                            }
                        });
                break;
            case R.id.button_accept_popup_invitation:
                acceptInviteToRoom(mIncomingInvitationId);
                mIncomingInvitationId = null;
                break;
            case R.id.button_quick_game:
                startQuickGame();
                break;

            //Game buttons
            case R.id.button_color1:
                buttonColor = "R";
                pegColour = getDrawable(R.drawable.red_pin40);
                break;

            case R.id.button_color2:
                buttonColor = "B";
                pegColour = getDrawable(R.drawable.blue_pin40);
                break;

            case R.id.button_color3:
                buttonColor = "G";
                pegColour = getDrawable(R.drawable.green_pin40);
                break;

            case R.id.button_color4:
                buttonColor = "P";
                pegColour = getDrawable(R.drawable.pink_pin40);
                break;

            case R.id.button_color5:
                buttonColor = "K";
                pegColour = getDrawable(R.drawable.black_pin40);
                break;

            case R.id.button_color7:
                buttonColor = "O";
                pegColour = getDrawable(R.drawable.orange_pin40);
                break;

            case R.id.button_color8:
                buttonColor = "Y";
                pegColour = getDrawable(R.drawable.yellow_pin40);
                break;

            case R.id.button_confirm:
                //If in code select screen and confirm button is clicked
                if(!playerTurn(round, mParticipants.get(0), mParticipants.get(1))){
                    startRound = true;
                    setContentView(R.layout.activity_online_multi_player_game);
                    LinearLayout pegs_slots = findViewById(R.id.peg_slots);
                    pegs_slots.setVisibility(View.VISIBLE);
                    LinearLayout code_slots = findViewById(R.id.code_select_buttons);
                    code_slots.setVisibility(View.GONE);
                    sendMessage();
                    resetGameVars();
                    startGame(mMultiplayer);
                    Button button_confirm = findViewById(R.id.button_confirm);
                    button_confirm.setOnClickListener(null);
                }
                //If in game screen and confirm button is clicked
                else {
                    if (checkCurrentRow()) {
                        //Turns off listeners for previous row
                        for (int i = 0; i < width; i++) {
                            buttons[currentRow][i].setOnClickListener(null);
                        }
                        if (!currentRowCorrect()) {
                            //Sets listeners for next row
                            currentRow--;
                            for (int j = 0; j < width; j++) {
                                String buttonID = "button_" + currentRow + j;
                                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                                buttons[currentRow][j] = findViewById(resID);
                                buttons[currentRow][j].setOnClickListener(this);
                            }
                        }
                        //If the submitted try is correct
                        else {
                            //On last round
                            if(round >= 3){
                                sendMessage();
                                leaveRoom();
                                lastRound = false;
                                round = 0;
                                this.recreate();
                            }
                            else {
                                round++;
                                //First Round
                                if(round == 1){
                                    resetGameVars();
                                    switchToScreen(R.id.screen_game);
                                    LinearLayout peg_slots = findViewById(R.id.peg_slots);
                                    peg_slots.setVisibility(View.GONE);
                                    LinearLayout code_slots = findViewById(R.id.code_select_buttons);
                                    code_slots.setVisibility(View.VISIBLE);
                                    sendMessage();
                                    playerSelectCode();

                                }
                                //Second Round
                                else {
                                    lastRound = true;
                                    resetGameVars();
                                    setContentView(R.layout.activity_online_multi_player_game);
                                    round++;
                                    startGame(mMultiplayer);
                                }
                            }
                        }
                    }
                }
                break;
        }
        //Changes Text and background of game buttons to desired color
        if (!(v.getId() == R.id.button_confirm || v.getId() == R.id.button_quick_game)) {
            ((Button) v).setText(buttonColor);
            v.setBackground(pegColour);
    }
}

    void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
    }

    public void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    //If user has signed in previously then no prompt is required
    public void signInSilently() {

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            onConnected(task.getResult());
                        } else {
                            onDisconnected();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            handleSelectPlayersResult(resultCode, intent);

        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            handleInvitationInboxResult(resultCode, intent);

        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                // ready to start playing
                playerSelectCode();
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                leaveRoom();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.

    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            switchToMainScreen();
            return;
        }

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }

        // create the room
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria).build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            switchToMainScreen();
            return;
        }
        
        Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        if (invitation != null) {
            acceptInviteToRoom(invitation.getInvitationId());
        }
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invitationId) {
        // accept the invitation

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .build();

        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        mRealTimeMultiplayerClient.join(mRoomConfig);
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {

        // if we're in a room, leave it.
        leaveRoom();

        // stop trying to keep the screen on
        stopKeepingScreenOn();

        switchToMainScreen();

        super.onStop();
    }

    // Leave room cleanly if back button is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == R.id.screen_game) {
            leaveRoom();
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    // Leave the room.
    void leaveRoom() {
        stopKeepingScreenOn();
        if (mRoomId != null) {
            mRealTimeMultiplayerClient.leave(mRoomConfig, mRoomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRoomId = null;
                            mRoomConfig = null;
                        }
                    });
            switchToScreen(R.id.screen_wait);
        } else {
            this.recreate();
        }
    }

    void showWaitingRoom(Room room) {
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        mRealTimeMultiplayerClient.getWaitingRoomIntent(room, MIN_PLAYERS)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        // show waiting room UI
                        startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                });
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Called when we get an invitation to play a game. We react by showing that to the user.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            // We got an invitation to play a game! So, store it in
            // mIncomingInvitationId
            // and show the popup on the screen.
            mIncomingInvitationId = invitation.getInvitationId();
            ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                    invitation.getInviter().getDisplayName() + " " +
                            getString(R.string.is_inviting_you));
            switchToScreen(mCurScreen); // This will show the invitation popup
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {

            if (mIncomingInvitationId.equals(invitationId) && mIncomingInvitationId != null) {
                mIncomingInvitationId = null;
                switchToScreen(mCurScreen);
            }
        }
    };

    private String mPlayerId;

    // The currently signed in account
    GoogleSignInAccount mSignedInAccount = null;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        if (mSignedInAccount != googleSignInAccount) {
            mSignedInAccount = googleSignInAccount;

            // update the clients
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount);
            mInvitationsClient = Games.getInvitationsClient(OnlineMultiPlayerGame.this, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            mPlayerId = player.getPlayerId();

                            switchToMainScreen();
                        }
                    });
        }

        // Listener to receive invites
        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        GamesClient gamesClient = Games.getGamesClient(OnlineMultiPlayerGame.this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            Invitation invitation =
                                    hint.getParcelable(Multiplayer.EXTRA_INVITATION);

                            if (invitation != null && invitation.getInvitationId() != null) {
                                // retrieve and cache the invitation ID
                                acceptInviteToRoom(invitation.getInvitationId());
                            }
                        }
                    }
                });
    }


    public void onDisconnected() {

        mRealTimeMultiplayerClient = null;
        mInvitationsClient = null;

        switchToMainScreen();
    }

    private RoomStatusUpdateCallback mRoomStatusUpdateCallback = new RoomStatusUpdateCallback() {
        @Override
        public void onConnectedToRoom(Room room) {
            mParticipants = room.getParticipants();
            mMyId = room.getParticipantId(mPlayerId);

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (mRoomId == null) {
                mRoomId = room.getRoomId();
            }
        }

        @Override
        public void onDisconnectedFromRoom(Room room) {
            mRoomId = null;
            mRoomConfig = null;
            showGameError();
        }

        //List of interactions the room uses, and updates the room info
        @Override
        public void onPeerDeclined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onP2PDisconnected(@NonNull String participant) {
        }

        @Override
        public void onP2PConnected(@NonNull String participant) {
        }

        @Override
        public void onPeerJoined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, @NonNull List<String> peersWhoLeft) {
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeersConnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }
    };

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create();

        switchToMainScreen();
    }

    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {

        // Called when room has been created
        @Override
        public void onRoomCreated(int statusCode, Room room) {
            if (statusCode != GamesCallbackStatusCodes.OK) {
                showGameError();
                return;
            }

            // save room ID so we can leave cleanly before the game starts.
            mRoomId = room.getRoomId();

            showWaitingRoom(room);
        }

        // Called when room is fully connected.
        @Override
        public void onRoomConnected(int statusCode, Room room) {
            if (statusCode != GamesCallbackStatusCodes.OK) {
                showGameError();
                return;
            }
            updateRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            if (statusCode != GamesCallbackStatusCodes.OK) {
                showGameError();
                return;
            }
            showWaitingRoom(room);
        }

        @Override
        public void onLeftRoom(int statusCode, @NonNull String roomId) {
            // we have left the room; return to main screen.
            switchToMainScreen();
        }
    };

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
    }

    //Game section


    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        timeLeft = 180;
        buttons = new Button[height][width];
        stringColors = new String[] {"B", "R", "G", "K", "P", "O", "Y"};
        listColors = new ArrayList<>(Arrays.asList(stringColors));
        randSequence = new ArrayList<>();
        currentRow = height - 1;
    }


    // Start the gameplay phase of the game.
    void startGame(boolean multiplayer) {
        mMultiplayer = multiplayer;
        if (startOfGame){
            startTimer();
            startOfGame = false;
        }
        Participant user1 = mParticipants.get(0);
        Participant user2 = mParticipants.get(1);
        String playerOne = user1.getDisplayName();
        String playerTwo = user2.getDisplayName();
        TextView usernameOne = findViewById(R.id.usernameOne);
        TextView usernameTwo = findViewById(R.id.usernameTwo);
        usernameOne.setText(playerOne);
        usernameTwo.setText(playerTwo);

        if (playerTurn(round, user1, user2))
            {
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

                if(round >= 3 && mMyId.equals(user2.getParticipantId())) {
                    startRound = true;
                    lastRound = true;
                    for (int i = 0; i < width; i++) {
                        Random rnd = new Random();
                        String color = listColors.get(rnd.nextInt(listColors.size()));
                        if (!allowDuplicates) {
                            listColors.remove(color);
                        }
                        randSequence.add(color);
                    }
                    sendMessage();
                }
                Toast.makeText(this, randSequence.toString(), Toast.LENGTH_LONG).show();

                for (int j = 0; j < width; j++) {
                    String buttonID = "button_" + currentRow + j;
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    buttons[currentRow][j] = findViewById(resID);
                    buttons[currentRow][j].setOnClickListener(this);
                }
            }
        switchToScreen(R.id.screen_game);
        LinearLayout peg_slots = findViewById(R.id.peg_slots);
        peg_slots.setVisibility(View.VISIBLE);
        LinearLayout code_slots = findViewById(R.id.code_select_buttons);
        code_slots.setVisibility(View.GONE);
    }

    //Send player to code select scene
    void playerSelectCode(){

        Participant user1 = mParticipants.get(0);
        Participant user2 = mParticipants.get(1);

        if (!playerTurn(round, user1, user2)) {
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
                String buttonID = "button_code_" + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                code[j] = findViewById(resID);
                code[j].setOnClickListener(this);
            }
        }
        switchToScreen(R.id.screen_game);
        LinearLayout peg_slots = findViewById(R.id.peg_slots);
        peg_slots.setVisibility(View.GONE);
        LinearLayout code_slots = findViewById(R.id.code_select_buttons);
        code_slots.setVisibility(View.VISIBLE);
    }


    OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
            byte[] buf = realTimeMessage.getMessageData();
            LinearLayout code_slots = findViewById(R.id.code_select_buttons);
            LinearLayout peg_slots = findViewById(R.id.peg_slots);
            Toast.makeText(OnlineMultiPlayerGame.this, String.valueOf((char)buf[width]), Toast.LENGTH_SHORT).show();

            if (buf[width] == 'F') {
                if(lastRound){
                    restartMainScreen();
                    lastRound = false;
                    round = 0;
                }
                resetGameVars();
                switchToScreen(R.id.screen_game);
                peg_slots.setVisibility(View.VISIBLE);
                code_slots.setVisibility(View.GONE);
                playerSelectCode();
            }

            if (buf[width] == 'S'){
                resetGameVars();
                code_slots.setVisibility(View.GONE);
                peg_slots.setVisibility(View.VISIBLE);

                for(int i = 0; i < width;i++){
                    char code = (char) buf[i];
                    String letter = String.valueOf(code);
                    randSequence.add(letter);
                }
                startRound = false;
                startGame(mMultiplayer);
            }

            else {
                round++;
                resetGameVars();
                code_slots.setVisibility(View.GONE);
                peg_slots.setVisibility(View.VISIBLE);

                for(int i = 0; i < width;i++){
                    char code = (char) buf[i];
                    String letter = String.valueOf(code);
                    randSequence.add(letter);
                }
                round++;
                startGame(mMultiplayer);
            }
        }
    };

    Boolean lastRound = false;
    Boolean startRound = true;
    void sendMessage() {
        mMsgBuf = new byte[width + 1];


        if(startRound){
            if(lastRound){
                mMsgBuf[width] = 'L';
                for(int i = 0; i < width;i++){
                    char color = randSequence.get(i).charAt(0);
                    mMsgBuf[i] = (byte) color;
                }
            }
            else {
                mMsgBuf[width] = 'S';
                for (int i = 0; i < width; i++) {
                    char color = code[i].getText().toString().charAt(0);
                    mMsgBuf[i] = (byte) color;
                }
            }
            startRound = false;

        }
        else {
            for (int i = 0; i < width; i++) {
                mMsgBuf[i] = (byte) 0;
            }
            mMsgBuf[width] = (byte) 'F';
        }

        // Send to every other participant.
        for (Participant p : mParticipants)
                mRealTimeMultiplayerClient.sendUnreliableMessage(mMsgBuf, mRoomId,
                        p.getParticipantId());
        }

   //UI Methods

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_game, R.id.screen_main, R.id.screen_sign_in,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            showInvPopup = false;
        } else if (mMultiplayer) {
            showInvPopup = (mCurScreen == R.id.screen_main);
        } else {
            showInvPopup = (mCurScreen == R.id.screen_main || mCurScreen == R.id.screen_game);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    void switchToMainScreen() {
        if (mRealTimeMultiplayerClient != null) {
            switchToScreen(R.id.screen_main);
        } else {
            switchToScreen(R.id.screen_sign_in);
        }
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public boolean checkCurrentRow() {
        for (int i = 0; i < width; i++) {
            if (buttons[currentRow][i].getText().equals("")) {
                Toast.makeText(this, "Please fill every space in row " + (height - currentRow), Toast.LENGTH_SHORT).show();
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

    public void changeTextView(int countCorrectPos, int countCorrectCol) {
        String TextViewID = "Text_" + currentRow;
        int resID = getResources().getIdentifier(TextViewID, "id", getPackageName());
        final TextView feedbackTextView = findViewById(resID);
        String feedbackText = "RPos: " + countCorrectPos + "\nRC: " + countCorrectCol;
        feedbackTextView.setText(feedbackText);
    }

    void startTimer() {
        // run the gameTick() method every second to update the game.
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    return;
                }
                if (mRoomId != null) {
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
        String stringTimer = String.valueOf(timeLeft / 60) + ":" + String.valueOf(timeLeft % 60);
        ((TextView) findViewById(R.id.countdown)).setText(stringTimer);

        if (timeLeft <= 0) {
            if(round >= 3){
                leaveRoom();
                lastRound = false;
                round = 0;
                this.recreate();
            }
            else {
                round++;
                Toast.makeText(this, String.valueOf(round), Toast.LENGTH_SHORT).show();

                if(round == 1){
                    resetGameVars();
                    switchToScreen(R.id.screen_game);
                    LinearLayout peg_slots = findViewById(R.id.peg_slots);
                    peg_slots.setVisibility(View.GONE);
                    LinearLayout code_slots = findViewById(R.id.code_select_buttons);
                    code_slots.setVisibility(View.VISIBLE);
                    playerSelectCode();

                }
                else {
                    lastRound = true;
                    resetGameVars();
                    setContentView(R.layout.activity_online_multi_player_game);
                    round++;
                    startGame(mMultiplayer);
                }
            }
        }
    }
    boolean playerTurn(int round, Participant user1, Participant user2) {
        if (round == 0){
            return mMyId.equals(user1.getParticipantId());
        }
        if (round == 1){
            return mMyId.equals(user2.getParticipantId());
        }
        else{
            return true;
        }
    }

    void restartMainScreen(){

        findViewById(R.id.button_quick_game).setOnClickListener(this);
        findViewById(R.id.button_accept_popup_invitation).setOnClickListener(this);
        findViewById(R.id.button_invite_players).setOnClickListener(this);
        findViewById(R.id.bn_login).setOnClickListener(this);
        findViewById(R.id.button_see_invitations).setOnClickListener(this);
        
        switchToMainScreen();
    }
}