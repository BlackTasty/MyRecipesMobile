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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tastyapps.myrecipesmobile.core.events.OnClientConnectedEventListener;
import com.tastyapps.myrecipesmobile.core.events.OnClientDisconnectedEventListener;
import com.tastyapps.myrecipesmobile.core.mobile.Client;
import com.tastyapps.myrecipesmobile.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

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
    }

    public void onConnectClick(View view) {
        Button btn = this.findViewById(R.id.btn_connect);
        //Disable connect button while connecting
        btn.setEnabled(false);

        Context appContext = this.getApplicationContext();
        Activity current = this;

        Log.d("LoginActivity", "IP-address (Binding): " + binding.txtAddress.getText().toString());

        //Return Singleton client, hook up connect event and call connect(...) method
        Client client = Client.getInstance();
        client.setOnClientConnectedEventListener(new OnClientConnectedEventListener() {
            @Override
            public void onConnected() {
                btn.setEnabled(true);
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
                btn.setEnabled(true);
            }
        });

        client.connect(appContext, binding.txtAddress.getText().toString(), binding.txtUsername.getText().toString(),
                binding.txtPassword.getText().toString());
    }
}