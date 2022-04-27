package com.rosario.hp.remisluna;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Fragment.fragment_turno;


public class MainViajes extends AppCompatActivity {
    private static final String TAG = MainViajes.class.getSimpleName();
    String ls_id_turno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_basica);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ls_id_turno     = settings.getString("id_turno","");
        getSupportActionBar().setTitle("Historial Turnos");

        Fragment fragment = null;

        fragment = new fragment_turno();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();



    }

}

