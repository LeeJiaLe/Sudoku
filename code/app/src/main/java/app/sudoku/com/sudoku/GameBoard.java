package app.sudoku.com.sudoku;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameBoard extends RelativeLayout {
    private List<List<GameNumSpace>> gameNumSpaces = new ArrayList<>();
    private int selectedX=-1;
    private int selectedY=-1;
    private int size=9;
    private int time=-1;
    private int[] grid={3,3};
    private String[][] board;
    private String[][] initBoard;
    private Context context;
    private String id;
    private TimerAction timerAction;
    private TimerAction completeAction=(i)->{};
    public GameBoard(Context context,String id,TimerAction timerAction) throws CouchbaseLiteException {
        super(context);
        this.context=context;
        this.timerAction =timerAction;
        this.id=id;
        if(this.id.equals("")){
            init(context);
        }else{
            initSave(context);
        }
    }
    interface TimerAction{
        void action(int i);
    }

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initSave(Context context) throws CouchbaseLiteException {
        Result result = AllData.getSaveByID(this.id);
        this.size=result.getInt("size");
        this.time=result.getInt("time");
        timerAction.action(this.time);

        board=new String[size][size];
        initBoard=new String[size][size];

        float dp = context.getResources().getDisplayMetrics().density;
        int h = (int) (605 * dp + 0.5f);
        int w = (int) (605 * dp + 0.5f);
        this.setLayoutParams(new RelativeLayout.LayoutParams(w,h));

        for(int i=0;i<size;i++){
            List<GameNumSpace> numRow=new ArrayList<>();
            for(int j=0;j<size;j++){
                GameNumSpace gameNumSpace = new GameNumSpace(context,j,i);
                gameNumSpace.setGameNumOnClick((g,x,y)->{
                    if(selectedX!=-1){
                        this.gameNumSpaces.get(selectedY).get(selectedX).deselect();
                    }
                    selectedX=x;
                    selectedY=y;
                });

                int vh=(int) (60 * dp + 0.5f);
                int vw=(int) (60 * dp + 0.5f);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(vw,vh);
                params.leftMargin = (int) (5+((5+60)*j+(5*(j/3))) * dp + 0.5f);
                params.topMargin = (int) (5+((5+60)*i+(5*(i/3))) * dp + 0.5f);
                this.addView(gameNumSpace,params);

                Dictionary detail = result.getArray("cell_details").getArray(i).getDictionary(j);
                Array enabledNum = detail.getArray("enabled_number");
                for(int num=0;num<enabledNum.count();num++){
                    gameNumSpace.setNum(enabledNum.getString(num));
                }

                gameNumSpace.setEditable(detail.getBoolean("editable"));
                if(detail.getBoolean("warning")){
                    gameNumSpace.setWarning();
                    gameNumSpace.setWarningBack();
                }

                initBoard[i][j] = result.getArray("init_board").getArray(i).getString(j);
                this.board[i][j] = result.getArray("curr_board").getArray(i).getString(j);

                numRow.add(gameNumSpace);
            }

            this.gameNumSpaces.add(numRow);
        }
    }
    private void init(Context context){
        board = Sudoku.getBoard();
        initBoard=board;
        timerAction.action(this.time);


        float dp = context.getResources().getDisplayMetrics().density;
        int h = (int) (605 * dp + 0.5f);
        int w = (int) (605 * dp + 0.5f);
        this.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        String[][] board = this.board;

        for(int i=0;i<size;i++){
            List<GameNumSpace> numRow=new ArrayList<>();
            for(int j=0;j<size;j++){
                GameNumSpace gameNumSpace = new GameNumSpace(context,j,i);
                gameNumSpace.setGameNumOnClick((g,x,y)->{
                    if(selectedX!=-1){
                        this.gameNumSpaces.get(selectedY).get(selectedX).deselect();
                    }
                    selectedX=x;
                    selectedY=y;

                });

                if(!board[i][j].equals("x")){
                    gameNumSpace.setNum(board[i][j]);
                    gameNumSpace.setEditable(false);
                }
                int vh=(int) (60 * dp + 0.5f);
                int vw=(int) (60 * dp + 0.5f);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(vw,vh);
                params.leftMargin = (int) (5+((5+60)*j+(5*(j/3))) * dp + 0.5f);
                params.topMargin = (int) (5+((5+60)*i+(5*(i/3))) * dp + 0.5f);
                this.addView(gameNumSpace,params);
                numRow.add(gameNumSpace);
            }

            this.gameNumSpaces.add(numRow);
        }
    }

    public void setCompleteAction(TimerAction completeAction) {
        this.completeAction = completeAction;
    }

    public void setNum(String s){

        if(selectedY!=-1){
            GameNumSpace gameNumSpace = this.gameNumSpaces.get(selectedY).get(selectedX);
            boolean editable=gameNumSpace.getEditable();
            if(editable){

                int x=gameNumSpace.getPosX();
                int y=gameNumSpace.getPosY();
                gameNumSpace.setNum(s);

                if(gameNumSpace.getMarkCount()==1){
                    board[y][x]=gameNumSpace.getSelectedNum();
                    validate(board[y][x],x,y,true,true);
                }else{
                    board[y][x]="x";
                    validate("0",x,y,true,true);
                }

                int countX=0;
                for(int i=0;i<size;i++){
                    for(int j=0;j<size;j++){
                        if(board[i][j].equals("x")){
                            countX++;
                        }
                    }
                }

                if(countX==0){
                    boolean b=true;
                    for(int i=0;i<size;i++){
                        for(int j=0;j<size;j++){
                            boolean d=validate(board[i][j],j,i,false,false);
                            if(!d){
                                b=false;
                                break;
                            }
                        }
                        if(!b){
                            break;
                        }
                    }

                    if(!b){
                        Toast.makeText(this.context,"you still have some error!",Toast.LENGTH_LONG).show();
                    }else{
                        completeAction.action(this.time);
                    }
                }
            }
        }
        List<List<String>> nums=new ArrayList<>();
        for(int i=0;i<size;i++){
            List<String> row=new ArrayList<>();
            for(int j=0;j<size;j++){
                row.add(board[i][j]);
            }
            nums.add(row);
        }
        //Sudoku.printNumbers(nums,grid);
        //Log.v("hen","lo");

    }

    public boolean validate(String s,int x,int y,Boolean first,Boolean updateElement){
        boolean row=checkRow(s,x,y,first,updateElement);
        if(first){
            //Log.v("row",row+"");
        }
        boolean col=checkCol(s,x,y,first,updateElement);
        boolean grid=checkGrid(s,x,y,first,updateElement);
        return row&&col&&grid;
    }

    public boolean checkRow(String s,int x,int y,Boolean first,Boolean updateElement){
        boolean b=true;
        for(int j=0;j<size;j++){
            if(board[y][j].equals(s)){
                if(j!=x){
                    if(updateElement){
                        this.gameNumSpaces.get(y).get(j).setWarning();
                        this.gameNumSpaces.get(y).get(j).setWarningBack();
                    }
                    b=false;
                }
            }else if(!board[y][j].equals(s)){
                if(first){
                    boolean update=validate(board[y][j],j,y,false,false);
                    if(update){
                        this.gameNumSpaces.get(y).get(j).setToDefault();
                    }
                }
            }
        }
        if(first){
            //Log.v("b",!b+"");
            //Log.v("bo",board[y][x].equals(s)+"");
            //Log.v("e",updateElement+"");

        }
        if(!b&&board[y][x].equals(s)&&updateElement){
            this.gameNumSpaces.get(y).get(x).setWarning();
        }
        return b;
    }

    public boolean checkCol(String s,int x,int y,Boolean first,Boolean updateElement){
        boolean b=true;
        for(int i=0;i<size;i++){
            if(board[i][x].equals(s)){
                if(i!=y){
                    if(updateElement){
                        this.gameNumSpaces.get(i).get(x).setWarning();
                        this.gameNumSpaces.get(i).get(x).setWarningBack();
                    }
                    b=false;
                }
            }else if(!board[i][x].equals(s)){
                if(first){
                    boolean update=validate(board[i][x],x,i,false,false);
                    if(update){
                        this.gameNumSpaces.get(i).get(x).setToDefault();
                    }
                }
            }
        }
        if(!b&&board[y][x].equals(s)&&updateElement){
            this.gameNumSpaces.get(y).get(x).setWarning();
        }
        return b;
    }

    public boolean checkGrid(String s,int x,int y,Boolean first,Boolean updateElement){
        int gxs=(x/grid[1])*grid[1];
        int gxe=gxs+grid[1];
        int gys=(y/grid[0])*grid[0];
        int gye=gys+grid[0];
        boolean b=true;
        for(int i=gys;i<gye;i++){
            for(int j=gxs;j<gxe;j++){
                if(board[i][j].equals(s)){
                    if(i!=y&&j!=x){
                        if(updateElement){
                            this.gameNumSpaces.get(i).get(j).setWarning();
                            this.gameNumSpaces.get(i).get(j).setWarningBack();
                        }
                        b=false;
                    }
                }else if(!board[i][j].equals(s)){
                    if(first){
                        boolean update=validate(board[i][j],j,i,false,false);
                        if(update){
                            this.gameNumSpaces.get(i).get(j).setToDefault();
                        }
                    }
                }
            }
        }

        if(!b&&board[y][x].equals(s)&&updateElement){
            this.gameNumSpaces.get(y).get(x).setWarning();
        }
        return b;
    }



    public void saveGame(int time) throws CouchbaseLiteException {
        MutableDocument mutableDocument = new MutableDocument()
                .setDate("date",new Date())
                .setInt("time",time)
                .setInt("size",size);
        mutableDocument.setString("id", mutableDocument.getId());
        MutableArray initGameBoard = new MutableArray();
        MutableArray cellDetails = new MutableArray();
        MutableArray currBoard = new MutableArray();

        for(int i=0;i<size;i++){
            MutableArray row = new MutableArray();
            MutableArray rowC = new MutableArray();
            MutableArray rowD = new MutableArray();

            for(int j=0;j<size;j++){
                row.addString(this.initBoard[i][j]);
                rowC.addString(this.board[i][j]);
                rowD.addDictionary(getCellDetail(j,i));
            }
            initGameBoard.addArray(row);
            currBoard.addArray(rowC);
            cellDetails.addArray(rowD);
        }

        mutableDocument.setArray("init_board",initGameBoard);
        mutableDocument.setArray("curr_board",currBoard);
        mutableDocument.setArray("cell_details",cellDetails);

        AllData.saveGame(mutableDocument);

        if(!this.id.equals("")){
            AllData.deleteSaveByID(this.id);
        }
    }

    public void saveLeaderBoard(int time) throws CouchbaseLiteException {
        MutableDocument mutableDocument = new MutableDocument();
        mutableDocument.setDate("date",new Date());
        mutableDocument.setInt("time",time);
        AllData.saveLeaderBoard(mutableDocument);
    }
    public MutableDictionary getCellDetail(int x, int y){
        GameNumSpace gameNumSpace = this.gameNumSpaces.get(y).get(x);
        MutableDictionary cellDetail = new MutableDictionary();
        cellDetail.setBoolean("editable",gameNumSpace.getEditable());
        cellDetail.setBoolean("warning",gameNumSpace.getWarning());
        MutableArray enabledNum = new MutableArray();
        gameNumSpace.getEnabledNum().forEach((s,b)->{
            if(b){
                enabledNum.addString(s);
            }
        });

        cellDetail.setArray("enabled_number",enabledNum);
        return cellDetail;
    }

}
