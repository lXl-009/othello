package com.lxl.othello;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.lxl.othello.ChessBoard.Piece;
import com.lxl.othello.Game.ChessPlayer;
import com.lxl.othello.Game.GameListener;
import com.lxl.othello.Game.WhitePlayer;


public class LocalGameActivity extends BaseActivity {

    private static final String TAG = "GameActivity";
    private ChessBoard chessBoard;
    private TextView blackCount;
    private TextView whiteCount;
    private View rootView;
    private Game game;

    public static void navigateFrom(Context context) {
        Intent intent = new Intent(context, LocalGameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_game_activity);
        rootView = findViewById(R.id.root);
        blackCount = (TextView) findViewById(R.id.black_count);
        whiteCount = (TextView) findViewById(R.id.white_count);
        blackCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("can put, piece: %s, result: %b",
                        Piece.BLACK, chessBoard.canPutPiece(Piece.BLACK)));
            }
        });
        whiteCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("can put, piece: %s, result: %b",
                        Piece.WHITE, chessBoard.canPutPiece(Piece.WHITE)));
            }
        });
        chessBoard = (ChessBoard) findViewById(R.id.local_game_board);
        game = new Game(chessBoard);
        game.setGameListener(new GameListener() {
            @Override
            public void onChessPut() {
                updateStatus();
            }

            @Override
            public void onGameOver() {
                String result;
                if (chessBoard.getWhiteCount() > chessBoard.getBlackCount()) {
                    result = "White win!";
                } else if (chessBoard.getWhiteCount() < chessBoard.getBlackCount()) {
                    result = "black win!";
                } else {
                    result = "Draw game";
                }
                Toast toast = Toast.makeText(LocalGameActivity.this, result, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onPlayerChanged() {
                animateCurrentPlayer();
            }
        });
        game.start();
        updateStatus();
    }

    private void updateStatus() {
        blackCount.setText(String.valueOf(chessBoard.getBlackCount()));
        whiteCount.setText(String.valueOf(chessBoard.getWhiteCount()));
    }

    private void animateCurrentPlayer() {
        ChessPlayer currentPlayer = game.getCurrentPlayer();

        int startColor;
        int endColor;

        if (currentPlayer instanceof WhitePlayer) {
            startColor = 0x88000000;
            endColor = 0xffffffff;
        } else {
            startColor = 0xffffffff;
            endColor = 0x88000000;
        }
        ObjectAnimator currentPlayerAnimator = ObjectAnimator.ofObject(
                rootView, "backgroundColor", new ArgbEvaluator(), startColor, endColor);
        currentPlayerAnimator.setDuration(500);
        currentPlayerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        currentPlayerAnimator.start();
    }
}
