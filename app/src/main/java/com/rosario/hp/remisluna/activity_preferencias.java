package com.rosario.hp.remisluna;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.rosario.hp.remisluna.Fragment.fragment_preferencias;
import java.util.Objects;
public class activity_preferencias extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener instancia FirebaseAuth

        setContentView(R.layout.lista_main_inicial);

        Fragment fragment ;
        fragment = new fragment_preferencias();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();

        }

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.menu_preferencias));
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
        editor.apply();

        return super.onOptionsItemSelected(item);
    }



}
