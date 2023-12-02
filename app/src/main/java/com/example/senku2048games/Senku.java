package com.example.senku2048games;

import static android.opengl.ETC1.isValid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class Senku extends AppCompatActivity {
    private GridLayout gridLayout;
    private int rows;
    private int columns;
    private Button firstClickedButton;
    private int firstClickedRow = -1;
    private int firstClickedCol = -1;

    private Button[][] buttonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senku);

        gridLayout = this.findViewById(R.id.gridLayoutSenku);

        this.rows = this.gridLayout.getRowCount();
        this.columns = this.gridLayout.getColumnCount();

        buttonArray = new Button[rows][columns];
        Button toMenu = findViewById(R.id.senkuMenuB);
        toMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Senku.this, Main.class);
                startActivity(intent);
            }
        });

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Button button = (Button) gridLayout.getChildAt(i * columns + j);
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onButtonClick(int clickedRow, int clickedCol, Button clickedButton) {
        if (firstClickedButton == null) {
            if ("voidcircle_background".equals(clickedButton.getTag())) {
            } else {
                firstClickedButton = clickedButton;
                firstClickedRow = clickedRow;
                firstClickedCol = clickedCol;
                firstClickedButton.setBackground(getDrawable(R.drawable.selectedcircle));
            }
        } else if (isValid(firstClickedRow, firstClickedCol, clickedRow, clickedCol)) {
            showToast("Entra!");
            firstClickedButton.setBackground(getDrawable(R.drawable.voidcircle));
            firstClickedButton.setTag("voidcircle_background");
            clickedButton.setBackground(getDrawable(R.drawable.filledcircle));
            clickedButton.setTag("filledcircle_background");
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

    private boolean isValid(int firstRow, int firstCol, int secondRow, int secondCol) {
        int verticalDifference = Math.abs(firstRow - secondRow);
        int horizontalDifference = Math.abs(firstCol - secondCol);

        boolean isSpecificPosition =
                (firstRow == 0 && (firstCol == 0 || firstCol == 1 || firstCol == 5 || firstCol == 6)) ||
                        (firstRow == 1 && (firstCol == 0 || firstCol == 1 || firstCol == 5 || firstCol == 6)) ||
                        (firstRow == 5 && (firstCol == 0 || firstCol == 1 || firstCol == 5 || firstCol == 6)) ||
                        (firstRow == 6 && (firstCol == 0 || firstCol == 1 || firstCol == 5 || firstCol == 6));
        boolean hasInvisibleButtonInBetween = false;
        boolean secondIsInvisible = false;
        if (verticalDifference == 2 && horizontalDifference == 0) {
            int rowInBetween = Math.min(firstRow, secondRow) + 1;
            int colInBetween = firstCol;

            hasInvisibleButtonInBetween = "voidcircle_background".equals(buttonArray[rowInBetween][colInBetween].getTag());
            secondIsInvisible = "voidcircle_background".equals(buttonArray[secondRow][secondCol].getTag());

            if(!hasInvisibleButtonInBetween && secondIsInvisible){
                this.buttonArray[rowInBetween][colInBetween].setTag("voidcircle_background");
                this.buttonArray[rowInBetween][colInBetween].setBackground(getDrawable(R.drawable.voidcircle));
            }

        } else if (verticalDifference == 0 && horizontalDifference == 2) {
            int rowInBetween = firstRow;
            int colInBetween = Math.min(firstCol, secondCol) + 1;

            hasInvisibleButtonInBetween = "voidcircle_background".equals(buttonArray[rowInBetween][colInBetween].getTag());
            secondIsInvisible = "voidcircle_background".equals(buttonArray[secondRow][secondCol].getTag());

            if(!hasInvisibleButtonInBetween && secondIsInvisible){
                this.buttonArray[rowInBetween][colInBetween].setTag("voidcircle_background");
                this.buttonArray[rowInBetween][colInBetween].setBackground(getDrawable(R.drawable.voidcircle));
            }

        } /*else if (verticalDifference == 2 && horizontalDifference == 2){
            int rowInBetween = Math.min(firstRow, secondRow) + 1;
            int colInBetween = Math.min(firstCol, secondCol) + 1;

            hasInvisibleButtonInBetween = "voidcircle_background".equals(buttonArray[rowInBetween][colInBetween].getTag());
            secondIsInvisible = "voidcircle_background".equals(buttonArray[secondRow][secondCol].getTag());

            if(!hasInvisibleButtonInBetween && secondIsInvisible){
                this.buttonArray[rowInBetween][colInBetween].setTag("voidcircle_background");
                this.buttonArray[rowInBetween][colInBetween].setBackground(getDrawable(R.drawable.voidcircle));
            }
        }
      return (verticalDifference == 2 || horizontalDifference == 2 || (verticalDifference == 2 && horizontalDifference == 2))
                && !isSpecificPosition && !hasInvisibleButtonInBetween && secondIsInvisible;

*/
        return (verticalDifference == 2 || horizontalDifference == 2)
                && !isSpecificPosition && !hasInvisibleButtonInBetween && secondIsInvisible;
    }
}