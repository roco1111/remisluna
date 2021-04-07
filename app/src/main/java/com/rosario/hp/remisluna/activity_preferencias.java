package com.rosario.hp.remisluna;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rosario.hp.remisluna.Fragment.fragment_preferencias;


public class activity_preferencias extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener instancia FirebaseAuth

        setContentView(R.layout.lista_main_inicial);


        Fragment fragment = null;
        fragment = new fragment_preferencias();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();

        }

        getSupportActionBar().setTitle("Preferencias");
    }

}
