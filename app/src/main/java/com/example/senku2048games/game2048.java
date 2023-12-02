package com.example.senku2048games;

import static android.text.Selection.moveUp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.senku2048games.Main;
import com.example.senku2048games.R;

import java.util.Random;


public class game2048 extends AppCompatActivity {

    private GridLayout gridLayout;
    private int score;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private GestureDetector gestureDetector; // Declare as a class member

    private int[][] array2048 = new int[4][4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2048);
        gridLayout = findViewById(R.id.gridLayout2048);
        Button button = findViewById(R.id.to2048MenuB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(game2048.this, Main.class);
                startActivity(intent);
            }
        });

        // Initialize gestureDetector here
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        showToast("Right");
                        moveRight();
                        addRandom();
                    } else {
                        showToast("Left");
                        moveLeft();
                        addRandom();
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        showToast("Down");
                        moveDown();
                        addRandom();
                    } else {
                        showToast("Up");
                        moveUp();
                        addRandom();
                        // Swipe up
                    }
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
    }

    private void moveUp() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        for (int j = 0; j < cols; j++) {
            for (int i = 1; i < rows; i++) {
                int y = i - 1;
                int curr = i;
                while (y >= 0) {
                    if (this.array2048[y][j] == 0) {
                        this.array2048[y][j] = this.array2048[curr][j];
                        this.array2048[curr][j] = 0;
                        curr--;
                        y--;
                    } else if (this.array2048[y][j] == this.array2048[curr][j]) {
                        this.array2048[y][j] = this.array2048[curr][j] + this.array2048[y][j];
                        this.array2048[curr][j] = 0;
                        this.score += this.array2048[y][j];
                        y = -1;
                    } else {
                        y = -1;
                    }
                }
            }
        }
        update2048();
    }

    private void moveDown() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        for (int j = 0; j < cols; j++) {
            for (int i = rows - 2; i >= 0; i--) {
                int y = i + 1;
                int curr = i;
                while (y < rows) {
                    if (this.array2048[y][j] == 0) {
                        this.array2048[y][j] = this.array2048[curr][j];
                        this.array2048[curr][j] = 0;
                        curr++;
                        y++;
                    } else if (this.array2048[y][j] == this.array2048[curr][j]) {
                        this.array2048[y][j] = this.array2048[curr][j] + this.array2048[y][j];
                        this.array2048[curr][j] = 0;
                        this.score += this.array2048[y][j];
                        y = rows;
                    } else {
                        y = rows;
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
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

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
            showToast("No randoms will be added");
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
        addToArray(x, y, 2);
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
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        for (int i = 0; i < rows; i++) {
            for (int j = cols-1; j >= 0; j--) {
                int x = j + 1;
                int curr = j;
                while (x < cols) {
                    if (this.array2048[i][x] == 0) {
                        this.array2048[i][x] = this.array2048[i][curr];
                        this.array2048[i][curr] = 0;
                        curr++;
                        x++;
                    } else if (this.array2048[i][x] == this.array2048[i][curr]) {
                        this.array2048[i][x] = this.array2048[i][curr] + this.array2048[i][x];
                        this.array2048[i][curr] = 0;
                        this.score += this.array2048[i][x];
                        x = cols;
                    } else {
                        x = cols;
                    }
                }
            }
        }
        update2048();
    }

    public void moveLeft() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        for (int i = 0; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                int x = j - 1;
                int curr = j;
                while (x >= 0) {
                    if (this.array2048[i][x] == 0) {
                        this.array2048[i][x] = this.array2048[i][curr];
                        this.array2048[i][curr] = 0;
                        curr--;
                        x--;
                    } else if (this.array2048[i][x] == this.array2048[i][curr]) {
                        this.array2048[i][x] = this.array2048[i][curr] + this.array2048[i][x];
                        this.array2048[i][curr] = 0;
                        this.score += this.array2048[i][x];
                        x = -1;
                    } else {
                        x = -1;
                    }
                }
            }
        }
        update2048();
    }

    private void update2048() {
        int rows = gridLayout.getRowCount();
        int cols = gridLayout.getColumnCount();

        TextView score = (TextView) findViewById(R.id.ScoreNum);
        TextView record = (TextView) findViewById(R.id.RecordNum);
        score.setText(String.valueOf(this.score));
        record.setText(String.valueOf(this.score));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                TextView textView = (TextView) gridLayout.getChildAt(index);
                textView.setText(String.valueOf(array2048[i][j]));
            }
        }
    }

}


