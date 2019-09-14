package app.sudoku.com.sudoku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnNewGame = findViewById(R.id.btn_new_game);
        Button btnContinue = findViewById(R.id.btn_continue);
        Button btnLeaderBoard = findViewById(R.id.btn_leaderboard);
        Button btnSetting = findViewById(R.id.btn_setting);
        Button btnExit = findViewById(R.id.btn_exit);


        try {
            new AllData(this);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        btnLeaderBoard.setOnClickListener(v->{
            Intent intent = new Intent(this, LeaderBoard.class);
            startActivity(intent);
        });
        btnNewGame.setOnClickListener(v->{
            Intent intent = new Intent(this, Game.class);
            startActivity(intent);
        });

        btnContinue.setOnClickListener(v->{
            Intent intent = new Intent(this, SavedGame.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v->{
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
        });

        btnExit.setOnClickListener(v-> finish());
    }

}
