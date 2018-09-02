package com.example.benny.apptest2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginPage extends AppCompatActivity {

    private EditText emailEntry;
    private EditText passwordEntry;

    private Button loginButton;

    private String getEmail() {
        return emailEntry.getText().toString();
    }
    private String getPassword() {
        return passwordEntry.getText().toString();
    }

    private Dialog invalidPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_invalid_account);
        builder.setPositiveButton(R.string.login_reset_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Util.accountResetEmail();
            }
        });
        builder.setNegativeButton(R.string.login_retry_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //doNothing
            }
        });
        return builder.create();
    }

    private void assignVariables() {
        loginButton = (Button) findViewById(R.id.login_submit);
        emailEntry = (EditText) findViewById(R.id.login_text_email);
        passwordEntry = (EditText) findViewById(R.id.login_text_password);
    }

    private void setOnclicks() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginPoster poster = new LoginPoster();
                poster.owner = LoginPage.this;
                poster.emailToSet = getEmail();
                poster.passwordEmailHashToSet = Util.hash(getPassword() + getEmail());

                RequestData data = new RequestData();
                data.url = Util.loginConnection;
                data.message = "{\"email\":\"" + poster.emailToSet + "\",\"passwordEmailHash\":\"" + poster.passwordEmailHashToSet + "\"}";

                poster.execute(data);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        assignVariables();

        setOnclicks();
    }



    private Dialog currentDialog;
    private Dialog info(String inf) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(inf);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            currentDialog = null;
            }
        });
        return (currentDialog = builder.create());
    }

    class LoginPoster extends POSTer {
        public LoginPage owner;
        public String emailToSet, passwordEmailHashToSet;

        @Override
        protected void onFinish() {
            if(currentDialog != null) {
                currentDialog.dismiss();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                if(jsonGood()) {
                    try {
                        if(response.getBoolean("good")) {
                            Util.setUserCredentials(emailToSet, passwordEmailHashToSet, getBaseContext());
                            Util.pageSwap(owner, SplashScreen2.class);
                            owner.finish();
                        }
                    } catch (Exception ex) {
                        owner.info("Couldn't Login").show();
                    }
                } else {
                    owner.info("Couldn't Login").show();
                }
                }
            });
        }
    }
}
