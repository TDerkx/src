package nl.tue.vagariapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by s157091 on 29-3-2017.
 */
public class Settings extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        listView = (ListView) findViewById(R.id.list);
        String[] values = new String[]{
                "Location",
                "Sort albums by",
                "Logout",
                "Delete account"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values) ;



        // Assign adapter to ListView
        listView.setAdapter(adapter);

    }
}
