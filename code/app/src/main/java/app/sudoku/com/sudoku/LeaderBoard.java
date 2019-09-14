package app.sudoku.com.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Result;

import java.text.SimpleDateFormat;
import java.util.List;


public class LeaderBoard extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_with_recycler_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView nothing = findViewById(R.id.nothing);
        TextView title = findViewById(R.id.title);
        title.setText("Leaderboard");
        try {
            LeaderBoardAdapter leaderBoardAdapter = new LeaderBoardAdapter(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(leaderBoardAdapter);
            if(leaderBoardAdapter.getItemCount()==0){
                nothing.setText("No record yet");
                nothing.setVisibility(View.VISIBLE);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


    }
}

class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>{
    private AppCompatActivity activity;
    private List<Result> results;
    LeaderBoardAdapter(AppCompatActivity activity) throws CouchbaseLiteException {
        this.activity = activity;
        this.results = AllData.getLeaderBoard().allResults();
    }

    @Override
    public LeaderBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeaderBoardViewHolder((ConstraintLayout) LayoutInflater.from(activity).inflate(R.layout.leader_board_item,parent,false));
    }

    @Override
    public void onBindViewHolder(LeaderBoardViewHolder holder, int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        int time = results.get(position).getInt("time");
        long s = time % 60;
        long m = (time / 60) % 60;
        long h = (time / (60 * 60));
        String curr=String.format("%d hours %d minutes %d seconds", h,m,s);
        holder.date.setText("Date complete: "+formatter.format(results.get(position).getDate("date")));
        holder.time.setText(curr);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class LeaderBoardViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView time;
        LeaderBoardViewHolder(ConstraintLayout itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_complete);
            time = itemView.findViewById(R.id.time_spend);
        }
    }
}
