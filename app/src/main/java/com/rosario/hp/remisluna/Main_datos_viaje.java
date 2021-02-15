package com.rosario.hp.remisluna;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Fragment.fragment_datos_viaje;
import com.rosario.hp.remisluna.Fragment.fragment_turno;


public class Main_datos_viaje extends AppCompatActivity {
    private static final String TAG = Main_datos_viaje.class.getSimpleName();
    String ls_id_viaje;
    private JsonObjectRequest myRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_basica);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ls_id_viaje     = settings.getString("id_turno","");
        getSupportActionBar().setTitle("Datos Viaje");

        Fragment fragment = null;

        fragment = new fragment_datos_viaje();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();

    }


}
