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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;


public class SavedGame extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_with_recycler_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView title = findViewById(R.id.title);
        title.setText("Saved Game");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            TextView nothing = findViewById(R.id.nothing);
            SavedGameAdapter savedGameAdapter = new SavedGameAdapter(this);
            recyclerView.setAdapter(savedGameAdapter);
            if(savedGameAdapter.getItemCount()==0){
                nothing.setText("No saved game yet");
                nothing.setVisibility(View.VISIBLE);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}

class SavedGameAdapter extends RecyclerView.Adapter<SavedGameAdapter.SavedGameViewHolder>{
    private AppCompatActivity activity;
    private List<Result> results;
    SavedGameAdapter(AppCompatActivity activity) throws CouchbaseLiteException {
        this.activity = activity;
        this.results = AllData.getAllSavedGame().allResults();
    }

    @Override
    public SavedGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SavedGameViewHolder((ConstraintLayout) LayoutInflater.from(activity).inflate(R.layout.saved_game_item,parent,false));
    }

    @Override
    public void onBindViewHolder(SavedGameViewHolder holder, int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        int time = results.get(position).getInt("time");
        long s = time % 60;
        long m = (time / 60) % 60;
        long h = (time / (60 * 60));
        String curr=String.format("Time Spend: %d hours %d minutes %d seconds", h,m,s);
        holder.date.setText(formatter.format(results.get(position).getDate("date")));
        holder.time.setText(curr);
        holder.mainContent.setOnClickListener(v->{
            Intent intent= new Intent(this.activity,Game.class);
            intent.putExtra("id",results.get(position).getString("id"));
            activity.startActivity(intent);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class SavedGameViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout mainContent;
        TextView date;
        TextView time;
        SavedGameViewHolder(ConstraintLayout itemView) {
            super(itemView);

            mainContent = itemView.findViewById(R.id.saved_main_view);
            date = itemView.findViewById(R.id.saved_date);
            time = itemView.findViewById(R.id.duration);
        }
    }
}