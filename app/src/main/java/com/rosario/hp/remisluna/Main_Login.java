package com.rosario.hp.remisluna;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.rosario.hp.remisluna.Fragment.login;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Main_Login extends AppCompatActivity implements login.Callback  {

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1;
    private int posicion;
    private String posicion_string;
    private int posicion_nue;



    @Override
    public void onResume()
    {
        super.onResume();

        SharedPreferences settings3 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        posicion_string = settings3.getString("posicion","0");

        posicion = Integer.parseInt(posicion_string);

        Fragment fragment = new login();

        Bundle args1 = new Bundle();

        fragment.setArguments(args1);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        posicion_string = String.valueOf(posicion);
        savedInstanceState.putString("posicion", posicion_string);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        posicion_nue = Integer.parseInt(savedInstanceState.getString("posicion"));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = settings.edit();

        posicion_string = String.valueOf(posicion_nue);

        editor.putString("posicion",posicion_string);

        editor.commit();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener instancia FirebaseAuth

        setContentView(R.layout.activity_main_basica);
        Fragment fragment = null;
        fragment = new login();


        Bundle args1 = new Bundle();
        args1.putInt(login.ARG_ARTICLES_NUMBER, R.id.nav_ingreso);
        fragment.setArguments(args1);


        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = settings1.edit();

        posicion_string = String.valueOf(R.id.nav_ingreso);

        editor.putString("posicion",posicion_string);
        editor.apply();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);


    }




    @Override
    public void onInvokeGooglePlayServices(int errorCode) {
        showPlayServicesErrorDialog(errorCode);
    }

    void showPlayServicesErrorDialog(
            final int errorCode) {
        Dialog dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(
                        Main_Login.this,
                        errorCode,
                        REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


}

