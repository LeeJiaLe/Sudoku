package app.sudoku.com.sudoku;


import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.FullTextExpression;
import com.couchbase.lite.FullTextIndexItem;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.security.AccessControlContext;

public class AllData {

    private static Database saveData;
    private static Database leaderBoard;

    AllData(Context context) throws CouchbaseLiteException {
        DatabaseConfiguration conf=new DatabaseConfiguration(context);
        saveData = new Database("save_data",conf);
        leaderBoard = new Database("leader_board",conf);
        saveData.createIndex("idSearch", IndexBuilder.fullTextIndex(FullTextIndexItem.property("id")).ignoreAccents(false));
    }

    static void saveGame(MutableDocument mutableDocument) throws CouchbaseLiteException {
        saveData.save(mutableDocument);
    }

    static void saveLeaderBoard(MutableDocument mutableDocument) throws CouchbaseLiteException {
        leaderBoard.save(mutableDocument);
    }

    static ResultSet getAllSavedGame() throws CouchbaseLiteException {
        Query q=QueryBuilder.select(
                SelectResult.property("date"),
                SelectResult.property("time"),
                SelectResult.property("id")
        ).from(DataSource.database(saveData)).orderBy(Ordering.property("date").descending());

        return q.execute();
    }

    static ResultSet getLeaderBoard() throws CouchbaseLiteException {
        Query q=QueryBuilder.select(
                SelectResult.property("date"),
                SelectResult.property("time")
        ).from(DataSource.database(leaderBoard)).orderBy(Ordering.property("time").ascending());

        return q.execute();
    }
    static Result getSaveByID(String id) throws CouchbaseLiteException {
        Query q=QueryBuilder.select(
                SelectResult.property("date"),
                SelectResult.property("time"),
                SelectResult.property("id"),
                SelectResult.property("size"),
                SelectResult.property("init_board"),
                SelectResult.property("curr_board"),
                SelectResult.property("cell_details")
        ).from(DataSource.database(saveData)).where(FullTextExpression.index("idSearch").match(id));

        return q.execute().next();
    }

    static void deleteSaveByID(String id) throws CouchbaseLiteException {
        saveData.delete(saveData.getDocument(id));
    }

    static void deleteAllSave(Context context) throws CouchbaseLiteException {
        saveData.delete();
        DatabaseConfiguration conf=new DatabaseConfiguration(context);
        saveData = new Database("save_data",conf);
    }

    static void deleteLeaderBoard(Context context) throws CouchbaseLiteException {
        leaderBoard.delete();
        DatabaseConfiguration conf=new DatabaseConfiguration(context);
        leaderBoard = new Database("leader_board",conf);
    }
}
