package com.example.senku2048games;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class popup_dialog extends Dialog {
    private String message;
    private Context mContext;

    public popup_dialog(Context context, String message) {
        super(context);
        this.mContext = context;
        this.message = message;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_dialog);

        TextView messageTextView = findViewById(R.id.tv_message);
        messageTextView.setText(message);

        Button closeButton = findViewById(R.id.b_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button goToMainButton = findViewById(R.id.b_goToMain);
        goToMainButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Main.class);
                mContext.startActivity(intent);
                dismiss();
            }
        });
    }
}
