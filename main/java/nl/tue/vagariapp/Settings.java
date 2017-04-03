package nl.tue.vagariapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by s157091 on 29-3-2017.
 */
public class Settings extends Activity {

    ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mAuth = FirebaseAuth.getInstance();
        mAuthList = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed in
                    startActivity(new Intent(Settings.this, Login.class));

                }
            }
        };
        listView = (ListView) findViewById(R.id.list);
        String[] values = new String[]{
                "Location",
                "Sort albums by",
                "Logout",
                "Delete account"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values) ;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                switch (position){
                    case 1: {

                    }
                    case 2: {

                    }
                    case 3: {
                        mAuth.signOut();
                        startActivity(new Intent(Settings.this, Login.class));
                    }
                    case 4: {

                    }

                }
                Intent intent = new Intent(Settings.this, Login.class);
                startActivity(intent);
            }
        });



        // Assign adapter to ListView
        listView.setAdapter(adapter);

    }
}
