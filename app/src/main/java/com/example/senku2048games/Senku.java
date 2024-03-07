package com.example.senku2048games;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
    private boolean gameEnded;
    private long remainingTime;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senku);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);


        gridLayout = this.findViewById(R.id.gridLayoutSenku);
        this.gameEnded = false;
        this.rows = this.gridLayout.getRowCount();
        this.columns = this.gridLayout.getColumnCount();

        oldArray = new int[rows][columns];
        buttonArray = new Button[rows][columns];

        startSenku();

        timerTextView = findViewById(R.id.tv_timer_number);

        if (savedInstanceState != null) {
            this.buttonArray = (Button[][]) savedInstanceState.getSerializable("array");
            oldArray = (int[][]) savedInstanceState.getSerializable("oldArray");
            this.arrayOfOldArrays = (ArrayList<int[][]>) savedInstanceState.getSerializable("arrayOfOldArrays");
            remainingTime = savedInstanceState.getLong("RemainingTime");
            startCountDown((int) remainingTime, this);
            updateSenku();
        } else {
            startCountDown(null, this);
            findViewById(R.id.undoSenku).setEnabled(false);
        }
    }

    public void startCountDown(Integer time, Context context) {
        long timerInterval = 1000;
        int startingTime = (time == null) ? 600000 : time;

        countDownTimer = new CountDownTimer(startingTime, timerInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the UI on the main thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimerText(millisUntilFinished);
                        remainingTime = millisUntilFinished;
                    }
                });
            }

            @Override
            public void onFinish() {
                // Update the UI on the main thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleFinish(context);
                    }
                });
            }
        };

        countDownTimer.start();
    }

    private void updateTimerText(long millisUntilFinished) {
        timerTextView.setText("Time remaining: " + formatSecondsToTime(millisUntilFinished/1000));
    }

    private void handleFinish(Context context) {
        String message = (check() && remainingTime / 1000 > 1) ? "Felicidades, has ganado." : "Lamentablemente has perdido.";
        String displayMessage = (check() && remainingTime / 1000 > 1) ? "You win, congratulations, your remaining time is: " + remainingTime : "Timer finished! You Lose";

        timerTextView.setText(displayMessage);
        popup_dialog pop = new popup_dialog(context, message);
        pop.show();

        gameEnded = true;
        findViewById(R.id.undoSenku).setEnabled(false);
    }

    public String formatSecondsToTime(Long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
    private void startSenku() {
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
                    button.setBackground(getDrawable(R.drawable.ic_void_circle));
                    button.setTag("voidcircle_background");
                } else {
                    button.setBackground(getDrawable(R.drawable.ic_filled_circle));
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
    }

    private void copyArray() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ("voidcircle_background".equals(buttonArray[i][j].getTag())) {
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
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (this.oldArray[i][j] == 0) {
                    this.buttonArray[i][j].setTag("voidcircle_background");
                    this.buttonArray[i][j].setBackground(getDrawable(R.drawable.ic_void_circle));
                } else {
                    this.buttonArray[i][j].setTag("filledcircle_background");
                    this.buttonArray[i][j].setBackground(getDrawable(R.drawable.ic_filled_circle));
                }
            }
        }
    }

    public void resetSenku(View v) {
        this.arrayOfOldArrays = new ArrayList<>();
        oldArray = new int[rows][columns];
        findViewById(R.id.undoSenku).setEnabled(false);
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }
        this.gameEnded = false;
        startCountDown(null, this);
        startSenku();
    }

    public void undoSenku(View v) {
        if (!this.arrayOfOldArrays.isEmpty()) {
            replaceNewWithOld();
            arrayOfOldArrays.remove(arrayOfOldArrays.size() - 1);
            if (!arrayOfOldArrays.isEmpty()) {
                this.oldArray = this.arrayOfOldArrays.get(arrayOfOldArrays.size() - 1);
            } else {
                findViewById(R.id.undoSenku).setEnabled(false);
            }
            updateSenku();
        } else {
            findViewById(R.id.undoSenku).setEnabled(false);
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
        if (!gameEnded) {
            if (firstClickedButton == null) {
                if ("voidcircle_background".equals(clickedButton.getTag())) {
                } else {
                    firstClickedButton = clickedButton;
                    firstClickedRow = clickedRow;
                    firstClickedCol = clickedCol;
                    firstClickedButton.setBackground(getDrawable(R.drawable.ic_selected_circle));
                }
            } else if (isValid(firstClickedRow, firstClickedCol, clickedRow, clickedCol)) {
                copyArray();
                findViewById(R.id.undoSenku).setEnabled(true);
                animateMove(firstClickedButton, buttonArray[clickedRow][clickedCol]);
                int rowInBetween = getRowInBetween(firstClickedRow, clickedRow);
                int colInBetween = getColInBetween(firstClickedCol, clickedCol);
                this.buttonArray[rowInBetween][colInBetween].setBackground(getDrawable(R.drawable.ic_void_circle));
                this.buttonArray[rowInBetween][colInBetween].setTag("voidcircle_background");
                firstClickedButton.setBackground(getDrawable(R.drawable.ic_void_circle));
                firstClickedButton.setTag("voidcircle_background");
                this.buttonArray[firstClickedRow][firstClickedCol] = firstClickedButton;
                clickedButton.setBackground(getDrawable(R.drawable.ic_filled_circle));
                clickedButton.setTag("filledcircle_background");
                this.buttonArray[clickedRow][clickedCol] = clickedButton;

                firstClickedButton = null;
                firstClickedRow = -1;
                firstClickedCol = -1;

                if(checkSinglePiece()) {
                    showToast("Has ganado");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    try {
                        Date oldRecord = format.parse(sharedPreferences.getString("recordSenku","00:00:00"));
                        Date newRecord = format.parse(formatSecondsToTime(this.remainingTime/1000));

                        if (oldRecord.compareTo(newRecord) > 0) {
                            editor.putString("recordSenku", formatSecondsToTime(this.remainingTime/1000));
                            editor.apply();
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    this.countDownTimer.cancel();
                }else if (!check()) {
                    showToast("Has perdido");
                    this.countDownTimer.cancel();
                }
            } else {
                if ("voidcircle_background".equals(clickedButton.getTag())) {
                } else {
                    firstClickedButton.setBackground(getDrawable(R.drawable.ic_filled_circle));
                    firstClickedButton = null;
                    firstClickedRow = -1;
                    firstClickedCol = -1;
                }
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
        outState.putSerializable("array",this.buttonArray);
        outState.putSerializable("oldArray", this.oldArray);
        outState.putSerializable("arrayOfOldArrays", this.arrayOfOldArrays);
        outState.putLong("RemainingTime", remainingTime);
        super.onSaveInstanceState(outState);
    }
}