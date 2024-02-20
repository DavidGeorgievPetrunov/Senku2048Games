package com.example.senku2048games;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Senku extends AppCompatActivity {
    private GridLayout gridLayout;
    private int rows;
    private int columns;
    private Button firstClickedButton;
    private int firstClickedRow = -1;
    private int firstClickedCol = -1;
    private Button[][] buttonArray;
    private int[][] oldArray;
    private ArrayList<int[][]> arrayOfOldArrays = new ArrayList<>();
    private TextView timerTextView;
    private CountDownTimer countDownTimer;

    private long remainingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senku);

        gridLayout = this.findViewById(R.id.gridLayoutSenku);

        this.rows = this.gridLayout.getRowCount();
        this.columns = this.gridLayout.getColumnCount();

        oldArray = new int[rows][columns];
        buttonArray = new Button[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Button button = (Button) gridLayout.getChildAt(i * columns + j);
                boolean isSpecificPosition =
                        (i == 0 && (j == 0 || j == 1 || j == 5 || j == 6)) ||
                                (i == 1 && (j == 0 || j == 1 || j == 5 || j == 6)) ||
                                (i == 5 && (j == 0 || j == 1 || j == 5 || j == 6)) ||
                                (i == 6 && (j == 0 || j == 1 || j == 5 || j == 6)) ||
                                (i == 3 && j == 3);
                if (isSpecificPosition) {
                    button.setTag("voidcircle_background");
                } else {
                    button.setTag("filledcircle_background");
                }
                final int row = i;
                final int col = j;

                buttonArray[i][j] = button;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick(row, col, (Button) v);
                    }
                });

            }
        }

        timerTextView = findViewById(R.id.tv_timer_number);

        long timerDuration = 10000;
        long timerInterval = 1000;

        if (savedInstanceState != null) {
            oldArray = (int[][]) savedInstanceState.getSerializable("oldArray");
            this.arrayOfOldArrays = (ArrayList<int[][]>) savedInstanceState.getSerializable("arrayOfOldArrays");
            remainingTime = savedInstanceState.getLong("RemainingTime");
            replaceNewWithOld();
            updateSenku();
            countDownTimer = new CountDownTimer(timerDuration, timerInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTextView.setText("Time remaining: " + millisUntilFinished / 1000 + " seconds");
                    remainingTime = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    if (!check() || remainingTime/1000 <= 1) {
                        timerTextView.setText("Timer finished! You Lose");
                    } else {
                        timerTextView.setText("You win, congratulations, your remaining time is: "+remainingTime);
                    }
                }
            };

            countDownTimer.start();
        } else {
            countDownTimer = new CountDownTimer(timerDuration, timerInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTextView.setText("Time remaining: " + millisUntilFinished / 1000 + " seconds");
                    remainingTime = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    if (!check() || remainingTime/1000 <= 1) {
                        timerTextView.setText("Timer finished! You Lose");
                    } else {
                        timerTextView.setText("You win, congratulations, your remaining time is: "+remainingTime);
                    }
                }
            };

            countDownTimer.start();
            findViewById(R.id.deshacer).setEnabled(false);
        }
    }
    private void copyArray(){
        for (int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if ("voidcircle_background".equals(buttonArray[i][j].getTag())){
                    this.oldArray[i][j] = 0;
                } else {
                    this.oldArray[i][j] = 1;
                }
            }
        }

        copyInsideArrayofArrays();
    }

    private void copyInsideArrayofArrays() {
        int[][] copyOfOldArray = new int[oldArray.length][];
        for (int i = 0; i < oldArray.length; i++) {
            copyOfOldArray[i] = Arrays.copyOf(oldArray[i], oldArray[i].length);
        }

        this.arrayOfOldArrays.add(copyOfOldArray);
    }

    private void replaceNewWithOld() {
        for (int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++) {
                if(this.oldArray[i][j] == 0){
                    this.buttonArray[i][j].setTag("voidcircle_background");
                    this.buttonArray[i][j].setBackground(getDrawable(R.drawable.voidcircle));
                } else {
                    this.buttonArray[i][j].setTag("filledcircle_background");
                    this.buttonArray[i][j].setBackground(getDrawable(R.drawable.filledcircle));
                }
            }
        }
    }

    public void undo(View v){
        if(!this.arrayOfOldArrays.isEmpty()) {
            replaceNewWithOld();
            arrayOfOldArrays.remove(arrayOfOldArrays.size() - 1);
            if (!arrayOfOldArrays.isEmpty()) {
                this.oldArray = this.arrayOfOldArrays.get(arrayOfOldArrays.size() - 1);
            } else {
                findViewById(R.id.deshacer).setEnabled(false);
            }
            updateSenku();
        } else {
            findViewById(R.id.deshacer).setEnabled(false);
        }
    }
    private void updateSenku() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                Button button = (Button) gridLayout.getChildAt(i * columns + j);
                button = this.buttonArray[i][j];
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onButtonClick(int clickedRow, int clickedCol, Button clickedButton) {
        if (!check()) {
            showToast("Has perdido");
            this.countDownTimer.cancel();
        } else if (checkSinglePiece()) {
            showToast("Has ganado");
            this.countDownTimer.cancel();
        }
        if (firstClickedButton == null) {
            if ("voidcircle_background".equals(clickedButton.getTag())) {
            } else {
                firstClickedButton = clickedButton;
                firstClickedRow = clickedRow;
                firstClickedCol = clickedCol;
                firstClickedButton.setBackground(getDrawable(R.drawable.selected_circle));
            }
        } else if (isValid(firstClickedRow, firstClickedCol, clickedRow, clickedCol)) {
            copyArray();
            findViewById(R.id.deshacer).setEnabled(true);
            animateMove(firstClickedButton, buttonArray[clickedRow][clickedCol]);
            int rowInBetween = getRowInBetween(firstClickedRow, clickedRow);
            int colInBetween = getColInBetween(firstClickedCol, clickedCol);
            this.buttonArray[rowInBetween][colInBetween].setBackground(getDrawable(R.drawable.voidcircle));
            this.buttonArray[rowInBetween][colInBetween].setTag("voidcircle_background");
            firstClickedButton.setBackground(getDrawable(R.drawable.voidcircle));
            firstClickedButton.setTag("voidcircle_background");
            this.buttonArray[firstClickedRow][firstClickedCol] = firstClickedButton;
            clickedButton.setBackground(getDrawable(R.drawable.filledcircle));
            clickedButton.setTag("filledcircle_background");
            this.buttonArray[clickedRow][clickedCol] = clickedButton;

            firstClickedButton = null;
            firstClickedRow = -1;
            firstClickedCol = -1;

        } else {
            if ("voidcircle_background".equals(clickedButton.getTag())) {
            } else {
                firstClickedButton.setBackground(getDrawable(R.drawable.filledcircle));
                firstClickedButton = null;
                firstClickedRow = -1;
                firstClickedCol = -1;
            }
        }
    }

    private boolean checkMiddleButton(int firstRow, int firstCol, int secondRow, int secondCol) {
        int rowInBetween = getRowInBetween(firstRow, secondRow);
        int colInBetween = getColInBetween(firstCol, secondCol);

        if ("voidcircle_background".equals(buttonArray[rowInBetween][colInBetween].getTag())) {
            return true;
        }
        return false;
    }

    private int getColInBetween(int firstCol, int secondCol) {
        if (firstCol == secondCol) {
            return Math.min(firstCol, secondCol);
        } else {
            return Math.min(firstCol, secondCol) + 1;
        }
    }

    private int getRowInBetween(int firstRow, int secondRow) {
        if (firstRow == secondRow) {
            return Math.min(firstRow, secondRow);
        } else {
            return Math.min(firstRow, secondRow) + 1;
        }
    }

    private boolean isValid(int firstRow, int firstCol, int secondRow, int secondCol) {
        int verticalDifference = Math.abs(firstRow - secondRow);
        int horizontalDifference = Math.abs(firstCol - secondCol);

        boolean isSpecificPositionFirst = isValidPosition(firstRow, firstCol);
        boolean isSpecificPositionSecond = isValidPosition(secondRow, secondCol);

        boolean isFirstInvisible = "voidcircle_background".equals(buttonArray[firstRow][firstCol].getTag());

        boolean hasInvisibleButtonInBetween = checkMiddleButton(firstRow, firstCol, secondRow, secondCol);
        ;
        boolean secondIsInvisible = "voidcircle_background".equals(buttonArray[secondRow][secondCol].getTag());

        return ((verticalDifference == 2 && horizontalDifference == 0)
                || (verticalDifference == 0 && horizontalDifference == 2))
                && isSpecificPositionFirst && isSpecificPositionSecond
                && !hasInvisibleButtonInBetween && secondIsInvisible
                && !isFirstInvisible;
    }

    private boolean isValidPosition(int x, int y) {
        if ((x == 0 && (y == 0 || y == 1 || y == 5 || y == 6)) ||
                (x == 1 && (y == 0 || y == 1 || y == 5 || y == 6)) ||
                (x == 5 && (y == 0 || y == 1 || y == 5 || y == 6)) ||
                (x == 6 && (y == 0 || y == 1 || y == 5 || y == 6))) {
            return false;
        }
        return true;
    }

    private boolean check() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (j < columns - 2 && isValid(i, j, i, j + 2)) {
               //     Log.d("Check1", "check1"+i + "j:"+j);;
                    return true;
                }

                if (i < rows - 2 && isValid(i, j, i + 2, j)) {
        //            Log.d("Check2", "check2" +i + "j:"+j);;
                    return true;
                }

                if (j > 1 && isValid(i, j, i, j - 2)) {
     //               Log.d("Check3", "check3"+i + "j:"+j);;
                    return true;
                }

                if (i > 1 && isValid(i, j, i - 2, j)) {
                //    Log.d("Check4", "check4"+i + "j:"+j);;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSinglePiece() {
        int countFilledCircles = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ("filledcircle_background".equals(buttonArray[i][j].getTag())) {
                    countFilledCircles++;
                }
            }
        }


        return countFilledCircles == 1;
    }

    private void animateMove(final View firstView, final View secondView) {
        float translationX = secondView.getX() - firstView.getX();
        float translationY = secondView.getY() - firstView.getY();

        firstView.animate()
                .translationXBy(translationX)
                .translationYBy(translationY)
                .setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        firstView.setTranslationX(0f);
                        firstView.setTranslationY(0f);
                    }
                })
                .start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Añadir oldArray y arrayOfOlds
        outState.putSerializable("oldArray", this.oldArray);
        outState.putSerializable("arrayOfOldArrays", this.arrayOfOldArrays);
        outState.putLong("RemainingTime", remainingTime);
        super.onSaveInstanceState(outState);
    }
}