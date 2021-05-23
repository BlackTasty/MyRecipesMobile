package com.tastyapps.myrecipesmobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.Client;
import com.tastyapps.myrecipesmobile.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private Client client;

    private Button btnConnect;
    private EditText txtAddress;
    private EditText txtUsername;
    private EditText txtPassword;
    private CircularProgressIndicator progressConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLifecycleOwner(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.color.brown_700, null));
        }

        progressConnect = this.findViewById(R.id.progress_connect);
        btnConnect = this.findViewById(R.id.btn_connect);
        txtPassword = this.findViewById(R.id.txt_password);
        txtUsername = this.findViewById(R.id.txt_username);
        txtAddress = this.findViewById(R.id.txt_address);
        txtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnConnect.setEnabled(txtAddress.getText().toString().length() > 0);
            }
        });

        Activity current = this;
        //Return Singleton client and hook up connect event
        client = Client.getInstance();
        client.setOnClientConnectedEventListener(new OnClientConnectedEventListener() {
            @Override
            public void onConnected() {
                setIsConnecting(false);
                Log.d("LoginActivity", "Connection succeeded with MQTT server!");

                //On success, swap activity to MainActivity
                Intent mainIntent = new Intent(current, MainActivity.class);
                startActivity(mainIntent);
            }

            @Override
            public void onFail(Throwable ex) {
                Toast.makeText(current, "Verbindung fehlgeschlagen!", Toast.LENGTH_LONG).show();
                Log.d("LoginActivity", "Connection failed with MQTT server!");
                ex.printStackTrace();
                setIsConnecting(false);
            }
        });
    }

    public void onConnectClick(View view) {
        //Disable connect button while connecting
        setIsConnecting(true);

        Context appContext = this.getApplicationContext();

        Log.d("LoginActivity", "IP-address (Binding): " + binding.txtAddress.getText().toString());

        //Call connect(...) method in client
        client.connect(appContext, binding.txtAddress.getText().toString(), binding.txtUsername.getText().toString(),
                binding.txtPassword.getText().toString());
    }

    private void setIsConnecting(boolean isConnecting) {
        txtAddress.setEnabled(!isConnecting);
        txtUsername.setEnabled(!isConnecting);
        txtPassword.setEnabled(!isConnecting);
        btnConnect.setEnabled(!isConnecting);

        progressConnect.setVisibility(isConnecting ? View.VISIBLE : View.GONE);
    }
}