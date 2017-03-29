package nl.tue.vagariapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by s157091 on 29-3-2017.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Splash.this, Login.class);
        startActivity(intent);
        finish();
    }
}
