package nl.tue.vagariapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by s157091 on 28-3-2017.
 */
public class Login extends Activity {

    Button bLogin;
    Button bRegister;
    EditText nInput;
    EditText pInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        bLogin = (Button) findViewById(R.id.loginButton);
        bRegister = (Button) findViewById(R.id.registerButton);

        bLogin.setEnabled(false);
        bRegister.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sUsername = nInput.getText().toString();
                String sPassword = pInput.getText().toString();
                if (sUsername.matches("") || sPassword.matches("")) {
                    bLogin.setEnabled(false);
                    bRegister.setEnabled(false);
                } else {
                    bLogin.setEnabled(true);
                    bRegister.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        nInput = (EditText) findViewById(R.id.userinput);
        nInput.addTextChangedListener(textWatcher);
        pInput = (EditText) findViewById(R.id.passwordinput);
        pInput.addTextChangedListener(textWatcher);

        Button.OnClickListener listener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Tabs.class);
                startActivity(intent);
            }
        };

        bRegister.setOnClickListener(listener);
        bLogin.setOnClickListener(listener);
    }
}
