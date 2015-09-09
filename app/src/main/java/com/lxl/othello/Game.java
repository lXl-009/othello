package com.lxl.othello;

import com.lxl.othello.ChessBoard.OnCellClickListener;
import com.lxl.othello.ChessBoard.Piece;

/**
 * User: lxl
 * Date: 6/30/15
 * Time: 4:52 PM
 */
public class Game {

    private static final int COLUMN_NUM = ChessBoard.COLUMN_NUM;
    private static final int ROW_NUM = ChessBoard.ROW_NUM;
    private WhitePlayer whitePlayer;
    private BlackPlayer blackPlayer;
    private ChessPlayer currentPlayer;
    private ChessBoard chessBoard;
    private Piece[][] pieces;
    private GameListener gameListener;
    public interface GameListener {
        void onChessPut();
        void onGameOver();
        void onPlayerChanged();
    }

    public void setGameListener(GameListener listener) {
        gameListener = listener;
    }


    public Game(ChessBoard chessBoard) {
        whitePlayer = new WhitePlayer(new Player("White"));
        blackPlayer = new BlackPlayer(new Player("Black"));
        this.chessBoard = chessBoard;
        pieces = new Piece[COLUMN_NUM][ROW_NUM];
        pieces[COLUMN_NUM / 2 - 1][ROW_NUM / 2 - 1] = whitePlayer.getPiece();
        pieces[COLUMN_NUM / 2][ROW_NUM / 2] = whitePlayer.getPiece();
        pieces[COLUMN_NUM / 2 - 1][ROW_NUM / 2] = blackPlayer.getPiece();
        pieces[COLUMN_NUM / 2][ROW_NUM / 2 - 1] = blackPlayer.getPiece();
        chessBoard.setPieces(pieces);
    }

    public void start() {
        chessBoard.setOnCellClickListener(new OnCellClickListener() {
            @Override
            public void onCellClicked(int row, int column) {
                putPiece(row, column);
            }
        });
        nextPlayerTurn();
    }

    private void putPiece(int row, int column) {
        if (chessBoard.putPiece(row, column, currentPlayer.getPiece())) {
            if (gameListener != null) {
                gameListener.onChessPut();
            }
            nextPlayerTurn();
        }
    }

    private void nextPlayerTurn() {
        ChessPlayer formerPlayer = currentPlayer;
        changePlayer();
        if (!chessBoard.canPutPiece(currentPlayer.getPiece())) {
            changePlayer();
            if (!chessBoard.canPutPiece(currentPlayer.getPiece())) {
                onGameOver();
            }
        } else if (gameListener != null && formerPlayer != null && formerPlayer != currentPlayer) {
            gameListener.onPlayerChanged();
        }
    }

    private void onGameOver() {
        if (gameListener != null) {
            gameListener.onGameOver();
        }
    }

    private void changePlayer() {
        if (currentPlayer != whitePlayer) {
            currentPlayer = whitePlayer;
        } else {
            currentPlayer = blackPlayer;
        }
    }


    public ChessPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    private void switchPlayer() {
        if (currentPlayer == whitePlayer) {
            currentPlayer = blackPlayer;
        } else {
            currentPlayer = whitePlayer;
        }
    }


    public static abstract class ChessPlayer {
        private final Player player;

        public ChessPlayer(Player player) {
            this.player = player;
        }

        public abstract Piece getPiece();
    }

    static class WhitePlayer extends ChessPlayer {

        public WhitePlayer(Player player) {
            super(player);
        }

        @Override
        public Piece getPiece() {
            return Piece.WHITE;
        }
    }

    static class BlackPlayer extends ChessPlayer {
        public BlackPlayer(Player player) {
            super(player);
        }

        @Override
        public Piece getPiece() {
            return Piece.BLACK;
        }
    }
}
