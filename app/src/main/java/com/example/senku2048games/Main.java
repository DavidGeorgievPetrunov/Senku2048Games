package com.example.senku2048games;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Main extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Games> mData;
    private GamesAdapter mGamesAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        createUser();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mData = new ArrayList<>();
        mGamesAdapter = new GamesAdapter(this, mData);
        recyclerView.setAdapter(mGamesAdapter);

        initializeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createUser();
    }

    public void createUser(){
        String storedUser = sharedPreferences.getString("user", "");
        int storedIcon = sharedPreferences.getInt("iconColor", 1);
        ImageView userIcon = findViewById(R.id.iv_icon_user);

        switch (storedIcon) {
            case 2:
                userIcon.setImageResource(R.drawable.ic_brown_square);
                break;
            case 3:
                userIcon.setImageResource(R.drawable.ic_orange_square);
                break;
            case 4:
                userIcon.setImageResource(R.drawable.ic_red_square);
                break;
            case 1:
            default:
                userIcon.setImageResource(R.drawable.ic_yellow_square);
        }

        TextView username = findViewById(R.id.tv_username);
        username.setText(storedUser);
    }
    private void initializeData() {
        String[] gamesList = getResources()
                .getStringArray(R.array.games_titles);
        TypedArray gamesImages = getResources().obtainTypedArray(R.array.games_images);

        mData.clear();

        for(int i=0;i<gamesList.length;i++){
            mData.add(new Games(gamesList[i], gamesImages.getResourceId(i, -1)));
        }

        gamesImages.recycle(); // Don't forget to recycle the TypedArray
        mGamesAdapter.notifyDataSetChanged();
    }


    public void goSettings(View v){
        Intent intent = new Intent(v.getContext(), Settings.class);
        v.getContext().startActivity(intent);
    }

}