package app.sudoku.com.sudoku;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.google.android.flexbox.FlexboxLayout;
import com.otaliastudios.zoom.ZoomApi;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity{
    private GameBoard gameBoard;
    private TextView timer;
    private Timer timerS=new Timer(true);
    private int time=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String timerType=sp.getString("timer_type","0");
        //https://github.com/natario1/ZoomLayout
        setContentView(R.layout.main_game);
        timer = findViewById(R.id.timer);
        if(timerType.equals("2")){
            timer.setVisibility(View.INVISIBLE);
        }
        ZoomLayout zoomLayout = findViewById(R.id.zoom_layout);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String id=extras!=null?extras.getString("id"):"";
        try {
            gameBoard=new GameBoard(this,id,i->{
                this.time=i;
                timerS.schedule((new TimerTask() {
                    @Override
                    public void run() {
                        time+=1;
                        long s = time % 60;
                        long m = (time / 60) % 60;
                        long h = (time / (60 * 60));
                        String curr=timerType.equals("0")?String.format("%d:%02d:%02d", h,m,s):
                                    timerType.equals("1")?String.format("%d hours %d mins %d secs", h,m,s):"";

                        runOnUiThread(()-> timer.setText(curr));
                    }
                }),1000,1000);

            });

            gameBoard.setCompleteAction((t)->{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have complete the game!")
                        .setPositiveButton("yay!", (d,i)->{

                        }).show();
                timerS.cancel();
                try {
                    gameBoard.saveLeaderBoard(this.time);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            });
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        zoomLayout.addView(gameBoard);
        RecyclerView numGroup = findViewById(R.id.num_button_group);
        numGroup.setLayoutManager(new GridLayoutManager(this,5));
        NumGroupAdapter numGroupAdapter =new NumGroupAdapter(this,9);
        numGroupAdapter.setOnNumClick( s -> gameBoard.setNum(s));
        numGroup.setAdapter(numGroupAdapter);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String saveType=sp.getString("save_type","0");

        try {
            this.timerS.cancel();
            if(saveType.equals("0")){
                gameBoard.saveGame(this.time);
                Toast.makeText(this,"Game Saved!",Toast.LENGTH_LONG).show();
                finish();
            }else if(saveType.equals("1")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Save game?")
                        .setPositiveButton("Yes", (d,i)->{
                            try {
                                gameBoard.saveGame(this.time);
                                Toast.makeText(this,"Game Saved!",Toast.LENGTH_LONG).show();
                                finish();
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton("No", (d,i)->{
                            finish();
                        }).show();
            }else{
                finish();
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}

class NumGroupAdapter extends RecyclerView.Adapter<NumGroupAdapter.NumGroupHolder>{
    private Context context;
    private int size;
    private OnNumClick onNumClick = (s)->{};
    NumGroupAdapter(Context context,int size){
        this.context=context;
        this.size=size;

    }
    @Override
    public NumGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NumGroupHolder(new NumButton(this.context));
    }

    @Override
    public void onBindViewHolder(NumGroupHolder holder, int position) {
        holder.numButton.setNum(position+1);
        holder.btnNum.setOnClickListener(v->{
            onNumClick.onClick((position+1)+"");
        });
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setOnNumClick(OnNumClick onNumClick) {
        this.onNumClick = onNumClick;
    }

    static class NumGroupHolder extends RecyclerView.ViewHolder{
        NumButton numButton;
        Button btnNum;
        NumGroupHolder(NumButton itemView) {
            super(itemView);
            numButton = itemView;
            btnNum = numButton.findViewById(R.id.btn_num);
        }
    }

    interface OnNumClick{
        void onClick(String s);
    }
}
