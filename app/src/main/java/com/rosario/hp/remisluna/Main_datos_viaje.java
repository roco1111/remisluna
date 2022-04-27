package com.rosario.hp.remisluna;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rosario.hp.remisluna.Fragment.fragment_datos_viaje;
import java.util.Objects;


public class Main_datos_viaje extends AppCompatActivity {
    String ls_id_viaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_basica);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ls_id_viaje     = settings.getString("id_turno","");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Datos Viaje");

        Fragment fragment;

        fragment = new fragment_datos_viaje();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        Intent intent;

        editor.putString("url", "https://remisluna.com.ar/remiseria/paginas_ayuda.php");
        editor.apply();
        intent = new Intent(getApplicationContext(), WebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

}
