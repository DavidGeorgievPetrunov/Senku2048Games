package com.example.senku2048games;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game2048 extends AppCompatActivity {

    private GridLayout gridLayout;
    private int score;
    private int time;
    private boolean isStarted;
    private boolean isAnimating;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    int rows;
    int cols;
    private GestureDetector gestureDetector;
    private int size;
    private int[][] array2048;
    private ArrayList<int[][]> arrayOfOldArrays = new ArrayList<>();
    ArrayList<Integer> arrayScores = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    TextView tvRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStarted = false;
        time = 0;
        isAnimating = false;
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        size = sharedPreferences.getInt("sizeSenku",4);
        showToast(String.valueOf(this.size));
        if (size == 3) {
            setContentView(R.layout.activity_2048_3x3);
        } else if (size == 4) {
            setContentView(R.layout.activity_2048_4x4);
        } else if (size == 5) {
            setContentView(R.layout.activity_2048_5x5);
        }

        gridLayout = findViewById(R.id.gridLayout2048);

        this.array2048 =  new int[size][size];
        tvRecord = findViewById(R.id.RecordNum);
        tvRecord.setText(String.valueOf(sharedPreferences.getInt("record2048",0)));

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (isStarted && !isAnimating) {
                        copyInsideArrayofArrays();
                        if (diffX > 0) {
                            //         showToast("Right");
                            moveRight();
                            addRandom();
                        } else {
                            //        showToast("Left");
                            moveLeft();
                            addRandom();
                        }                    }
                    findViewById(R.id.Undo).setEnabled(true);
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (isStarted && !isAnimating) {
                        copyInsideArrayofArrays();
                        if (diffY > 0) {
                            //         showToast("Down");
                            moveDown();
                            addRandom();
                        } else {
                            //        showToast("Up");
                            moveUp();
                            addRandom();
                        }
                    }
                    findViewById(R.id.Undo).setEnabled(true);
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });


        GridLayout gridLayout = findViewById(R.id.gridLayout2048);

        gridLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        Timer timer = new Timer();
        boolean stopTimer = false;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stopTimer) {
                    timer.cancel();
                    return;
                }
                if (isStarted) {
                    time += 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView t = findViewById(R.id.TimerNum);
                            t.setText(formatSecondsToTime(time));
                        }
                    });
                }
            }
        }, 0, 1000);

        rows = gridLayout.getRowCount();
        cols = gridLayout.getColumnCount();

        findViewById(R.id.Undo).setEnabled(false);

        if (savedInstanceState != null) {
            this.score = savedInstanceState.getInt("Score");
            this.arrayScores = (ArrayList<Integer>) savedInstanceState.getSerializable("ArrayScores");
            this.array2048 = (int[][]) savedInstanceState.getSerializable("Array");
            this.arrayOfOldArrays = (ArrayList<int[][]>) savedInstanceState.getSerializable("ArrayOfOld");
            this.time = savedInstanceState.getInt("Time");
            if(!this.arrayOfOldArrays.isEmpty()){
                findViewById(R.id.Undo).setEnabled(true);
            }
            update2048();
        }

    }

    public String formatSecondsToTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private void copyInsideArrayofArrays() {
        int[][] copyOfOldArray = new int[this.array2048.length][];
        for (int i = 0; i < array2048.length; i++) {
            copyOfOldArray[i] = Arrays.copyOf(array2048[i], array2048[i].length);
        }

        this.arrayScores.add(score);
        this.arrayOfOldArrays.add(copyOfOldArray);
    }

    public void deshacer(View v) {
        if (!this.arrayOfOldArrays.isEmpty()) {
            this.array2048 = this.arrayOfOldArrays.get(arrayOfOldArrays.size() - 1);
            arrayOfOldArrays.remove(arrayOfOldArrays.size() - 1);
            this.score = this.arrayScores.get(arrayScores.size()-1);
            arrayScores.remove(arrayScores.size()-1);
            if (arrayOfOldArrays.isEmpty()) {
                findViewById(R.id.Undo).setEnabled(false);
            }
            update2048();
        } else {
            findViewById(R.id.Undo).setEnabled(false);
        }
    }


    private void moveUp() {
        for (int j = 0; j < cols; j++) {
            for (int i = 1; i < rows; i++) {
                int y = i - 1;
                int curr = i;
                int index = curr * cols + j;
                int m = 1;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                while (y >= 0) {
                    if (this.array2048[y][j] == 0) {
                        this.array2048[y][j] = this.array2048[curr][j];
                        this.array2048[curr][j] = 0;
                        curr--;
                        y--;
                        m += 1;
                        if (y == 0 && this.array2048[curr][j] != 0) {
                            animateTextTranslationY(textView, -200f * m);
                            m = 1;
                        }
                    } else if (this.array2048[y][j] == this.array2048[curr][j]) {
                        this.array2048[y][j] = this.array2048[curr][j] + this.array2048[y][j];
                        this.array2048[curr][j] = 0;
                        this.score += this.array2048[y][j];
                        animateTextTranslationY(textView, -200f * m);
                        y = -1;
                        m = 1;
                    } else {
                        animateTextTranslationY(textView, -200f * m);
                        y = -1;
                        m = 1;
                    }
                }
            }
        }
        update2048();
    }

    private void moveDown() {
        for (int j = cols - 1; j >= 0; j--) {
            for (int i = rows - 2; i >= 0; i--) {
                int y = i + 1;
                int curr = i;
                int index = curr * cols + j;
                int m = 1;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                while (y < rows) {
                    if (this.array2048[y][j] == 0) {
                        this.array2048[y][j] = this.array2048[curr][j];
                        this.array2048[curr][j] = 0;
                        curr++;
                        y++;
                        m += 1;
                        if (y == rows && this.array2048[curr][j] != 0) {
                            animateTextTranslationY(textView, 200f * m);
                            m = 1;
                        }
                    } else if (this.array2048[y][j] == this.array2048[curr][j]) {
                        this.array2048[y][j] = this.array2048[curr][j] + this.array2048[y][j];
                        this.array2048[curr][j] = 0;
                        this.score += this.array2048[y][j];
                        animateTextTranslationY(textView, 200f * m);
                        y = rows;
                        m = 1;
                    } else {
                        animateTextTranslationY(textView, 200f * m);
                        y = rows;
                        m = 1;
                    }
                }
            }
        }
        update2048();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void start(View v) {
        isStarted = true;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                textView.setText("0");
                addToArray(i, j, 0);
            }
        }
        this.addRandom();
        this.findViewById(R.id.start).setEnabled(false);
    }

    private void addToArray(int i, int j, int value) {
        this.array2048[i][j] = value;
    }

    public void addRandom() {
        if (isFull()) {
            return;
        }
        Random random = new Random();

        int x = random.nextInt(4);
        int y = random.nextInt(4);

        while (this.array2048[x][y] != 0) {
            x = random.nextInt(4);
            y = random.nextInt(4);
        }

        int index = x * gridLayout.getColumnCount() + y;
        int chancesTo4 = random.nextInt(100);
        if (chancesTo4 <= 20){
            addToArray(x, y, 4);
        } else {
            addToArray(x, y, 2);
        }
        TextView textView = (TextView) gridLayout.getChildAt(index);
        textView.setText("2");

    }

    private boolean isFull() {
        for (int i = 0; i < this.array2048.length; i++) {
            for (int j = 0; j < this.array2048.length; j++) {
                if (array2048[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void moveRight() {

        for (int i = 0; i < rows; i++) {
            for (int j = cols - 1; j >= 0; j--) {
                int x = j + 1;
                int curr = j;
                int index = i * cols + curr;
                int m = 1;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                while (x < cols) {
                    if (this.array2048[i][x] == 0) {
                        this.array2048[i][x] = this.array2048[i][curr];
                        this.array2048[i][curr] = 0;
                        curr++;
                        x++;
                        m += 1;
                        if (x == cols && this.array2048[i][curr] != 0) {
                            animateTextTranslation(textView, 200f * m);
                            m = 1;
                        }
                    } else if (this.array2048[i][x] == this.array2048[i][curr]) {
                        this.array2048[i][x] = this.array2048[i][curr] + this.array2048[i][x];
                        this.array2048[i][curr] = 0;
                        this.score += this.array2048[i][x];
                        animateTextTranslation(textView, 200f * m);
                        x = cols;
                        m = 1;
                    } else {
                        animateTextTranslation(textView, 200f * m);
                        x = cols;
                        m = 1;
                    }
                }
            }
        }
    }

    public void moveLeft() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        for (int i = 0; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                int x = j - 1;
                int m = 1;
                int curr = j;
                int index = i * cols + curr;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                while (x >= 0) {

                    if (this.array2048[i][x] == 0) {
                        this.array2048[i][x] = this.array2048[i][curr];
                        this.array2048[i][curr] = 0;
                        curr--;
                        x--;
                        m += 1;
                        if (x == 0 && this.array2048[i][curr] != 0) {
                            animateTextTranslation(textView, -200f * m);
                            m = 1;
                        }
                    } else if (this.array2048[i][x] == this.array2048[i][curr]) {
                        this.array2048[i][x] = this.array2048[i][curr] + this.array2048[i][x];
                        this.array2048[i][curr] = 0;
                        this.score += this.array2048[i][x];
                        animateTextTranslation(textView, -200f * m);
                        x = -1;
                        m = 1;
                    } else {
                        animateTextTranslation(textView, -200f * m);
                        m = 1;
                        x = -1;
                    }
                }
            }
        }
    }

    private void animateTextTranslation(final TextView textView, final float translationX) {
        isAnimating = true;
        textView.animate()
                .translationX(translationX)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        textView.setTranslationX(0f);
                        update2048();
                        isAnimating = false;
                    }
                })
                .start();
    }

    private void animateTextTranslationY(final TextView textView, final float translationY) {
        isAnimating = true;
        textView.animate()
                .translationY(translationY)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        textView.setTranslationY(0f);
                        update2048();
                        isAnimating = false;
                    }
                })
                .start();
    }


    private void update2048() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        TextView tvScore = (TextView) findViewById(R.id.ScoreNum);
        tvScore.setText(String.valueOf(this.score));
        if(this.score > sharedPreferences.getInt("record2048",0)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("record2048", this.score);
            editor.apply();
            tvRecord.setText(String.valueOf(this.score));
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                textView.setText(String.valueOf(array2048[i][j]));
            }
        }
        if (this.score >= 2048) {
            showToast("Has ganado");
            popup_dialog pop = new popup_dialog(this, "Felicidades, has ganado.");
            pop.show();
        } else if (!check()) {
            showToast("Has perdido");
            popup_dialog pop = new popup_dialog(this, "Lamentablemente has perdido.");
            pop.show();
        }
    }

    private boolean check() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int value = array2048[i][j];

                if (value == 0 || checkAdjacent(i, j, value)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void reset(View v) {
        int rows = array2048.length;
        int cols = array2048[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array2048[i][j] = 0;
            }
        }

        this.time=0;
        this.score = 0;
        findViewById(R.id.Undo).setEnabled(false);
        this.arrayOfOldArrays = new ArrayList<>();
        start(v);
        update2048();
    }


    private boolean checkAdjacent(int row, int col, int value) {

        if (col > 0 && array2048[row][col - 1] == value) {
            return true;
        }

        if (col < gridLayout.getColumnCount() - 1 && array2048[row][col + 1] == value) {
            return true;
        }

        if (row > 0 && array2048[row - 1][col] == value) {
            return true;
        }

        if (row < gridLayout.getRowCount() - 1 && array2048[row + 1][col] == value) {
            return true;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Score", this.score);
        outState.putSerializable("ArrayScores", this.arrayScores);
        outState.putSerializable("Array", this.array2048);
        outState.putSerializable("ArrayOfOld", this.arrayOfOldArrays);
        outState.putInt("Time", this.time);
        super.onSaveInstanceState(outState);
    }
}


