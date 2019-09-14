package app.sudoku.com.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import java.lang.reflect.Array;


public class SettingPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();

        setPreferencesFromResource(R.xml.setting,rootKey);

        Preference delete = findPreference("delete_save_data");
        delete.setOnPreferenceClickListener(p->{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure? All saved data will be delete")
                    .setPositiveButton("Yes", (d,i)->{
                        try {
                            AllData.deleteAllSave(context);
                            Toast.makeText(context,"All save data deleted",Toast.LENGTH_LONG).show();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("No", (d,i)->{

                    }).show();
            return true;
        });

        Preference deleteL = findPreference("clear_leader_board");
        deleteL.setOnPreferenceClickListener(p->{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure? Leader board will be clear")
                    .setPositiveButton("Yes", (d,i)->{
                        try {
                            AllData.deleteLeaderBoard(context);
                            Toast.makeText(context,"Leader Board cleared",Toast.LENGTH_LONG).show();
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("No", (d,i)->{

                    }).show();
            return true;
        });
    }


}

