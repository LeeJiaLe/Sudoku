package app.sudoku.com.sudoku;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.Button;

public class NumButton extends ConstraintLayout {
    private Button btnNum;

    public NumButton(Context context) {
        super(context);
        init(context);
    }

    public NumButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context){
        inflate(context, R.layout.num_button, this);
        btnNum = findViewById(R.id.btn_num);
    }

    public void setNum(int i){
        btnNum.setText(i+"");
    }
}
