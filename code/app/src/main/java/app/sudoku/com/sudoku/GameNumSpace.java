package app.sudoku.com.sudoku;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameNumSpace extends ConstraintLayout{
    private TextView mainNum;
    private Map<String,TextView> markNums=new HashMap<>();
    private int x;
    private int y;
    private boolean editable=true;
    private boolean warning=false;
    private boolean selected=false;
    private Map<String,Boolean> enabledNum=new HashMap<>();
    private GameNumOnClick gameNumOnClick = (g,x,y)->{};
    private int markCount=0;
    private String selectedNum="";
    public GameNumSpace(Context context,int x,int y) {
        super(context);
        this.x=x;
        this.y=y;
        init(context);
    }

    public GameNumSpace(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameNumSpace(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        if(!editable){
            this.setBackgroundResource(R.drawable.num_noneditable);
        }
    }

    public boolean getEditable(){
        return this.editable;
    }

    public void setGameNumOnClick(GameNumOnClick g){
        this.gameNumOnClick = g;
    }
    private void init(Context context){
        inflate(context, R.layout.game_num_space,this);
        mainNum = findViewById(R.id.main_num);
        int[] markNumIds={
          R.id.num_mark_1,
          R.id.num_mark_2,
          R.id.num_mark_3,
          R.id.num_mark_4,
          R.id.num_mark_5,
          R.id.num_mark_6,
          R.id.num_mark_7,
          R.id.num_mark_8,
          R.id.num_mark_9,
        };

        for(int i=0;i<9;i++){
            TextView tx = findViewById(markNumIds[i]);
            markNums.put((i+1)+"",tx);
            enabledNum.put((i+1)+"",false);
        }

        this.setBackgroundResource(R.drawable.num_non_selected);

        this.setOnClickListener(v->{
            this.gameNumOnClick.onClick(this,this.x,this.y);
            select();

        });
    }

    public void setNum(String s){
        if(this.editable){
            enabledNum.put(s,!enabledNum.get(s));
            int count = 0;
            for(Boolean b:enabledNum.values()){
                if(b){
                    count++;
                }
            }

            this.markCount=count;
            if(count==1){
                enabledNum.forEach((v,b)->{
                    markNums.get(v).setVisibility(INVISIBLE);
                    if(b){
                        mainNum.setVisibility(VISIBLE);
                        mainNum.setText(v);
                        selectedNum=v;
                    }
                });
            }else if(count>1){
                mainNum.setVisibility(INVISIBLE);
                selectedNum="";
                setToDefault();
                enabledNum.forEach((v,b)->{
                    if(b){
                        markNums.get(v).setVisibility(VISIBLE);
                    }else{
                        markNums.get(v).setVisibility(INVISIBLE);
                    }
                });
            }else if(count==0){
                mainNum.setVisibility(INVISIBLE);
                selectedNum="";
                setToDefault();
                enabledNum.forEach((v,b)-> markNums.get(v).setVisibility(INVISIBLE));
            }
        }
    }

    public int getPosX() {
        return x;
    }

    public int getPosY() {
        return y;
    }

    public int getMarkCount(){
        return this.markCount;
    }
    public void setWarning(){
        this.warning = true;
    }

    public String getSelectedNum(){
        return this.selectedNum;
    }

    public boolean getWarning(){
        return this.warning;
    }

    public void setWarningBack(){
        this.setBackgroundResource(R.drawable.num_warning);
    }

    public void select(){
        this.selected=true;
        if(editable){
            this.setBackgroundResource(R.drawable.num_selected);
        }else{
            if(this.warning){
                this.setBackgroundResource(R.drawable.num_warning);
            }else{
                this.setBackgroundResource(R.drawable.num_noneditable);
            }
        }
    }
    public void deselect(){
        this.selected=false;
        if(this.warning){
            this.setBackgroundResource(R.drawable.num_warning);
        }else{
            setToDefault();
        }
    }

    public void setToDefault(){
        this.warning=false;
        if(editable){
            if(selected){
                this.setBackgroundResource(R.drawable.num_selected);
            }else{
                this.setBackgroundResource(R.drawable.num_non_selected);
            }
        }else{
            this.setBackgroundResource(R.drawable.num_noneditable);
        }
    }

    public Map<String, Boolean> getEnabledNum() {
        return enabledNum;
    }

    interface GameNumOnClick{
        void onClick(GameNumSpace g,int x, int y);
    }
}
