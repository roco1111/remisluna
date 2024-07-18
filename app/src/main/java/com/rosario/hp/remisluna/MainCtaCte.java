package com.rosario.hp.remisluna;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rosario.hp.remisluna.Fragment.fragment_cta_cte;
import com.rosario.hp.remisluna.Fragment.fragment_turnos;

import java.util.Objects;


public class MainCtaCte extends AppCompatActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener instancia FirebaseAuth

        setContentView(R.layout.lista_main_inicial);

        context = getApplicationContext();

        Fragment fragment ;
        fragment = new fragment_cta_cte();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();

        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cta Cte");
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


        Intent intent2 = new Intent(getApplicationContext(), activity_preferencias.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent2);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }


}
