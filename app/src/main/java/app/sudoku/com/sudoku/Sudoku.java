package app.sudoku.com.sudoku;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sudoku {
    private static int size= 9;
    private static int[] gridSize=getGridSize(size);

    static String[][] getBoard(){
        return randBoard();
    }

    private static String[][] randBoard(){
        String[][][] b={
             {
                {"5","3","x",   "x","7","x",   "x","x","x"},
                {"6","x","x",   "1","9","5",   "x","x","x"},
                {"x","9","8",   "x","x","x",   "x","6","x"},

                {"8","x","x",   "x","6","x",   "x","x","3"},
                {"4","x","x",   "8","x","3",   "x","x","1"},
                {"7","x","x",   "x","2","x",   "x","x","6"},

                {"x","6","x",   "x","x","x",   "2","8","x"},
                {"x","x","x",   "4","1","9",   "x","x","5"},
                {"x","x","x",   "x","8","x",   "x","7","9"},
            },
            {
                {"8","x","x",   "x","x","x",   "x","x","x"},
                {"x","x","3",   "6","x","x",   "x","x","x"},
                {"x","7","x",   "x","9","x",   "2","x","x"},

                {"x","5","x",   "x","x","7",   "x","x","x"},
                {"x","x","x",   "x","4","5",   "7","x","x"},
                {"x","x","x",   "1","x","x",   "x","3","x"},

                {"x","x","1",   "x","x","x",   "x","6","8"},
                {"x","x","8",   "5","x","x",   "x","1","x"},
                {"x","9","x",   "x","x","x",   "4","x","x"},
            },
        };

        int rnd = new Random().nextInt(b.length);

        String[][] nBoard=b[rnd];
        nBoard=randBoardRowCol(nBoard);
        nBoard=randBoardGrid(nBoard);
        nBoard=randBoardNum(nBoard);
        return nBoard;
    }
    private static String[][] randBoardNum(String[][] board){
        List<String> nums=new ArrayList<>();
        for(int i=1;i<=size;i++){
            nums.add(i+"");
        }

        for(int t=0;t<size;t++){
            Collections.shuffle(nums);
            String s1=nums.get(0);
            String s2=nums.get(1);
            for(int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    if(board[i][j].equals(s1)){
                        board[i][j]="a";
                    }

                    if(board[i][j].equals(s2)){
                        board[i][j]="b";
                    }
                }
            }

            for(int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    if(board[i][j].equals("a")){
                        board[i][j]=s2;
                    }

                    if(board[i][j].equals("b")){
                        board[i][j]=s1;
                    }
                }
            }
        }

        return board;
    }

    private static String[][] randBoardGrid(String[][] board){
        String[][] nBoard= copyArray(board);
        List<Integer> xPos=new ArrayList<>();
        for(int i=0;i<size/gridSize[1];i++){
            xPos.add(i);
        }
        Collections.shuffle(xPos);

        List<Integer> yPos=new ArrayList<>();
        for(int i=0;i<size/gridSize[0];i++){
            yPos.add(i);
        }
        Collections.shuffle(yPos);

        for(int i=0;i<size;i++){

            for(int j=0;j<size;j++){
                int nxg=j/gridSize[1];
                int nxp=j%gridSize[1];

                nBoard[i][j]=board[i][xPos.get(nxg)*gridSize[1]+nxp];
            }
        }
        String[][] nnBoard=copyArray(nBoard);
        for(int i=0;i<size;i++){
            int nyg=i/gridSize[0];
            int nyp=i%gridSize[0];
            for(int j=0;j<size;j++){
                nBoard[i][j]=nnBoard[yPos.get(nyg)*gridSize[0]+nyp][j];
            }
        }
        return nBoard;
    }

    private static String[][] randBoardRowCol(String[][] board){
        String[][] nBoard= copyArray(board);
        List<Integer> xPos=new ArrayList<>();
        for(int i=0;i<gridSize[1];i++){
            xPos.add(i);
        }
        Collections.shuffle(xPos);

        List<Integer> yPos=new ArrayList<>();
        for(int i=0;i<gridSize[0];i++){
            yPos.add(i);
        }
        Collections.shuffle(yPos);

        for(int j=0;j<size;j++){
            int nxg=j/gridSize[1];
            int nxp=j%gridSize[1];
            if(nxp==0){
                Collections.shuffle(xPos);
            }
            for(int i=0;i<size;i++){
                nBoard[i][j]=board[i][nxg*gridSize[1]+xPos.get(nxp)];
            }
        }

        String[][] nnBoard=copyArray(nBoard);
        for(int i=0;i<size;i++){
            int nyg=i/gridSize[0];
            int nyp=i%gridSize[0];
            if(nyp==0){
                Collections.shuffle(yPos);
            }
            for(int j=0;j<size;j++){

                nBoard[i][j]=nnBoard[nyg*gridSize[0]+yPos.get(nyp)][j];
            }
        }

        return nBoard;
    }

    private static String[][] copyArray(String[][] arr){
        String[][] newArr = new String[size][size];

        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
               newArr[i][j] = arr[i][j];
            }
        }
        return newArr;
    }

    private static int[] getGridSize(int size){
        int bestA=1;
        int bestB=size;
        for(int i=1;i<=size;i++){
            int d=size/i;
            if(!(size%i>0)){
                if(i==bestB||bestA==bestB){
                    break;
                }else{
                    bestA=i;
                    bestB=d;
                }
            }
        }
        return new int[]{
                bestA,
                bestB
        };
    }

}