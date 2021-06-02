package com.tastyapps.myrecipesmobile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.tastyapps.myrecipesmobile.core.mobile.MqttClient;
import com.tastyapps.myrecipesmobile.databinding.ActivityLoginBinding;
import com.tastyapps.myrecipesmobile.storage.CategoryStorage;
import com.tastyapps.myrecipesmobile.storage.IngredientStorage;
import com.tastyapps.myrecipesmobile.storage.RecipeStorage;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

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

        RecipeStorage.getInstance().clear();
        IngredientStorage.getInstance().clear();
        CategoryStorage.getInstance().clear();
    }

    public void onConnectClick(View view) {
        //Disable connect button while connecting
        setIsConnecting(true);

        //Context appContext = this.getApplicationContext();

        Log.d("LoginActivity", "IP-address (Binding): " + binding.txtAddress.getText().toString());

        //Set connection data
        MqttClient.getInstance().setConnectionData(binding.txtAddress.getText().toString(), binding.txtUsername.getText().toString(),
                binding.txtPassword.getText().toString());

        //Forward to MainActivity, which handles connecting
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        setIsConnecting(false);

        /*//Call connect(...) method in client
        client.connect(appContext, binding.txtAddress.getText().toString(), binding.txtUsername.getText().toString(),
                binding.txtPassword.getText().toString());*/
    }

    private void setIsConnecting(boolean isConnecting) {
        txtAddress.setEnabled(!isConnecting);
        txtUsername.setEnabled(!isConnecting);
        txtPassword.setEnabled(!isConnecting);
        btnConnect.setEnabled(!isConnecting);

        progressConnect.setVisibility(isConnecting ? View.VISIBLE : View.GONE);
    }
}