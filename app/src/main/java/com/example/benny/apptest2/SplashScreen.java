package com.example.benny.apptest2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class SplashScreen extends AppCompatActivity {

    Button guest, login, register;

    public void setOnClicks() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent it = new Intent(SplashScreen.this, LoginPage.class);
                    startActivity(it);
                } catch (Exception ex) {
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent it = new Intent(SplashScreen.this, RegisterPage.class);
                    startActivity(it);
                } catch (Exception ex) {
                }
            }
        });
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Util.pageSwap(SplashScreen.this, SplashScreen2.class);
                } catch(Exception ex) {
                }
            }
        });
    }

    private void assignVariables() {
        guest = (Button) findViewById(R.id.splash_guest);
        login = (Button) findViewById(R.id.splash_login);
        register = (Button) findViewById(R.id.splash_register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        assignVariables();

        setOnClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String loginInfo = Util.readFromFile("login.txt", getBaseContext());

        if(loginInfo.length() != 0) {
            String[] parts = loginInfo.split(" ");
            if(parts.length == 2) {
                LoginPoster poster = new LoginPoster();
                poster.owner = SplashScreen.this;

                RequestData data = new RequestData();
                data.url = Util.loginConnection;
                data.message = "{\"email\":\"" + parts[0] + "\",\"passwordEmailHash\":\"" + parts[1] + "\"}";

                poster.execute(data);
            }
        }
    }

    class LoginPoster extends POSTer {
        public SplashScreen owner;

        @Override
        protected void onFinish() {
            if(jsonGood()) {
                try {
                    if (response.getBoolean("good")) {
                        Util.pageSwap(owner, SplashScreen2.class);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}
