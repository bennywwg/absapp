package com.example.benny.apptest2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.MessageDigest;

public class RegisterPage extends AppCompatActivity {

    private EditText emailEntry;
    private EditText passwordEntry;
    private EditText passwordAgainEntry;
    private EditText[] entries; //for enumerating easily

    private Button registerButton;

    private Dialog currentDialog;

    private boolean informationValid;

    private String getEmail() {
        return emailEntry.getText().toString();
    }
    private String getPassword() {
        return passwordEntry.getText().toString();
    }
    private String getPasswordAgain() {
        return passwordAgainEntry.getText().toString();
    }

    private void updateRegisterButton() {
        String color = " ";
        String message = " ";

        color = getString(R.string.register_color_invalid);
        informationValid = false;
        if(getEmail().length() == 0 ){
            message = getString(R.string.register_message_noemail);
        } else if(!Util.isEmailValid(getEmail())) {
            message = getString(R.string.register_message_invalidemail);
        } else {
            if(getPassword().length() == 0) {
                message = getString(R.string.register_message_nopassword);
            } else {
                if(!getPassword().equals(getPasswordAgain())) {
                    message = getString(R.string.register_message_passwordsdifferent);
                } else {
                    color = getString(R.string.register_color_valid);
                    message = getString(R.string.register_message_allgood);
                    informationValid = true;
                }
            }
        }

        registerButton.setBackgroundColor(Color.parseColor(color));
        registerButton.setText("Register!\n(" + message + ")");
    }

    private Dialog invalidPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.register_popup_invalid);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                currentDialog = null;
            }
        });
        return (currentDialog = builder.create());
    }

    private Dialog pendingPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.register_popup_pending);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                currentDialog = null;
            }
        });
        return (currentDialog = builder.create());
    }

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

    public void tryRegister() {
        if (informationValid) {
            Dialog pending = pendingPopup();
            pending.show();

            RequestData data = new RequestData();
            data.url = Util.registerConnection;

            String email = getEmail();
            String password = getPassword();
            String hashedPassword = Util.hash(password + email);

            data.message = "{\"email\":\"" + email + "\", \"passwordEmailHash\":\"" + hashedPassword + "\"}";

            RegisterPoster p = new RegisterPoster();
            p.owner = this;
            p.execute(data);
        } else {
            invalidPopup().show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        emailEntry = (EditText) findViewById(R.id.register_text_email);
        passwordEntry = (EditText) findViewById(R.id.register_text_password);
        passwordAgainEntry = (EditText) findViewById(R.id.register_text_passwordagain);
        entries = new EditText[]{emailEntry, passwordEntry, passwordAgainEntry};

        registerButton = (Button) findViewById(R.id.register_text_submit);

        updateRegisterButton();

        for (EditText field : entries) {
            field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    updateRegisterButton();
                }
            });
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryRegister();
            }
        });
    }

    class RegisterPoster extends POSTer {
        public RegisterPage owner;

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
                            owner.info(response.getString("message")).show();
                        } catch (Exception ex) {
                            owner.info("Couldn't Create Account").show();
                        }
                    } else {
                        owner.info("Couldn't Create Account").show();
                    }
                }
            });
        }
    }
}
