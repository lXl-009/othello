package com.lxl.othello.plus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

/**
 * User: lxl
 * Date: 6/15/15
 * Time: 5:43 PM
 */
public class ChessBoard extends View {
    private static final String TAG = "ChessBoard";
    private static final int COLOR_BACKGROUND = 0xff00ddff;
    private static final int COLOR_LINE = 0xffffffff;
    public static final int COLUMN_NUM = 10;
    public static final int ROW_NUM = 10;

    private Paint paint;
    private Piece[][] pieces;
    private GestureDetectorCompat gestureDetectorCompat;
    private OnCellClickListener onCellClickListener;
    private Direction[] directions;

    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
        invalidate();
    }

    public interface OnCellClickListener {
        void onCellClicked(int row, int column);
    }

    public void setOnCellClickListener(OnCellClickListener onCellClickListener) {
        this.onCellClickListener = onCellClickListener;
    }

    public ChessBoard(Context context) {
        super(context);
        init();
    }

    public ChessBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        pieces = new Piece[COLUMN_NUM][ROW_NUM];
        directions = new Direction[]{
                new Left(),
                new Top(),
                new Right(),
                new Bottom(),
                new LeftTop(),
                new RightTop(),
                new RightBottom(),
                new LeftBottom(),
        };
        gestureDetectorCompat = new GestureDetectorCompat(getContext(), new OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                if (onCellClickListener != null) {
                    float width = getWidth();
                    float height = getHeight();
                    float cellWidth = width / COLUMN_NUM;
                    float cellHeight = height / ROW_NUM;
                    int column = (int) (event.getX() / cellWidth);
                    int row = (int) (event.getY() / cellHeight);
                    onCellClickListener.onCellClicked(row, column);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    public int getWhiteCount() {
        return getCount(Piece.WHITE);
    }

    public int getBlackCount() {
        return getCount(Piece.BLACK);
    }

    private int getCount(Piece piece) {
        int count = 0;
        for (int row = 0; row < ROW_NUM; row++) {
            for (int column = 0; column < COLUMN_NUM; column++) {
                if (pieces[row][column] == piece) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / COLUMN_NUM;
        int cellHeight = height / ROW_NUM;
        paint.setColor(COLOR_BACKGROUND);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setColor(COLOR_LINE);
        for (int i = 1; i < COLUMN_NUM; i++) {
            float x = i * cellWidth;
            canvas.drawLine(x, 0, x, height, paint);
        }
        for (int i = 1; i < ROW_NUM; i++) {
            float y = i * cellHeight;
            canvas.drawLine(0, y, width, y, paint);
        }
        drawPieces(canvas, width, height);
    }

    private void drawPieces(Canvas canvas, int width, int height) {
        int cellWidth = width / COLUMN_NUM;
        int cellHeight = height / ROW_NUM;
        for (int i = 0; i < ROW_NUM; i++) {
            for (int j = 0; j < COLUMN_NUM; j++) {
                Piece piece = pieces[i][j];
                if (piece != null) {
                    float left = j * cellWidth;
                    float top = i * cellHeight;
                    piece.draw(canvas, left, top, left + cellWidth, top + cellHeight);
                }
            }
        }
    }

    public boolean putPiece(int row, int column, Piece piece) {
        if (pieces[row][column] == null) {
            int flippedCount = 0;
            for (Direction direction : directions) {
                flippedCount += flip(direction.reset(row, column), piece);
            }
            if (flippedCount > 0) {
                pieces[row][column] = piece;
                invalidate();
                return true;
            }
        }
        return false;
    }

    public boolean canPutPiece(Piece piece) {
        for (int row = 0; row < pieces.length; row++) {
            for (int column = 0; column < pieces[row].length; column++) {
                for (Direction direction : directions) {
                    if (willFlip(direction.reset(row, column), piece)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean willFlip(Direction direction, Piece piece) {
        if (direction.next(0) == null) {
            Piece nextPiece = direction.next(1);
            if (nextPiece != null && nextPiece != piece) {
                for (int i = 2; ; i++) {
                    nextPiece = direction.next(i);
                    if (nextPiece == null) {
                        return false;
                    } else if (nextPiece == piece) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private abstract class Direction {
        private int row;
        private int column;

        public Direction() {
            this(0, 0);
        }

        public Direction(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public Direction reset(int row, int column) {
            this.row = row;
            this.column = column;
            return this;
        }

        public Piece next(int count) {
            int row = nextRow(count);
            int column = nextColumn(count);
            if (row >= 0 && row < pieces.length && column >= 0 && column < pieces[row].length) {
                return pieces[row][column];
            }
            return null;
        }

        protected int getRow() {
            return row;
        }

        protected int getColumn() {
            return column;
        }

        protected abstract int nextRow(int count);

        protected abstract int nextColumn(int count);
    }

    private class Left extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow();
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn() - count;
        }
    }

    private class Top extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow() - count;
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn();
        }
    }

    private class Right extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow();
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn() + count;
        }
    }

    private class Bottom extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow() + count;
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn();
        }
    }

    private class LeftTop extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow() - count;
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn() - count;
        }
    }

    private class RightTop extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow() - count;
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn() + count;
        }
    }

    private class RightBottom extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow() + count;
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn() + count;
        }
    }

    private class LeftBottom extends Direction {

        @Override
        protected int nextRow(int count) {
            return getRow() + count;
        }

        @Override
        protected int nextColumn(int count) {
            return getColumn() - count;
        }
    }

    private int flip(Direction direction, Piece piece) {
        int flippedCount = 0;
        for (int i = 1; ; i++) {
            Piece next = direction.next(i);
            if (next == null) {
                break;
            } else if (next == piece) {
                for (int j = 1; j < i; j++) {
                    pieces[direction.nextRow(j)][direction.nextColumn(j)] = piece;
                    flippedCount += 1;
                }
                break;
            }
        }
        return flippedCount;
    }

    public enum Piece {
        WHITE(0xffffffff), BLACK(0xff000000);

        private final Paint paint;
        private final RectF rectF;

        Piece(int color) {
            paint = new Paint();
            paint.setColor(color);
            rectF = new RectF();
        }

        public void draw(Canvas canvas, float left, float top, float right, float bottom) {
            float horizontalPadding = (right - left) / 10;
            float verticalPadding = (bottom - top) / 10;
            rectF.set(left + horizontalPadding, top + verticalPadding,
                    right - horizontalPadding, bottom - verticalPadding);
            canvas.drawOval(rectF, paint);
        }
    }
}
