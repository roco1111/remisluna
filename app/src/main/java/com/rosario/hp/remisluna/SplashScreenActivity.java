package com.rosario.hp.remisluna;
 
import java.util.Timer;
import java.util.TimerTask;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.Window;

import androidx.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashScreenActivity extends Activity {
 
    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String id_firebase;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crearAccesoDirectoAlInstalar(this);
 
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        setContentView(R.layout.splash);

        mAuth = FirebaseAuth.getInstance();


 
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                id_firebase = FirebaseInstanceId.getInstance().getToken();
                currentUser = mAuth.getCurrentUser();

                if (currentUser == null) {
                     //Start the next activity

                    Intent mainIntent = new Intent().setClass(
                           SplashScreenActivity.this,Main_Login .class);
                   startActivity(mainIntent);




                }else{

                   Intent mainIntent = new Intent().setClass(
                            SplashScreenActivity.this, MainActivity.class);
                    startActivity(mainIntent);

              }
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };
 
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

    public void crearAccesoDirectoAlInstalar(Activity actividad)
    {
        SharedPreferences preferenciasapp;
        boolean aplicacioninstalada ;

/*
* Compruebo si es la primera vez que se ejecuta la alicación,
* entonces es cuando creo el acceso directo
*/
        preferenciasapp = PreferenceManager.getDefaultSharedPreferences(actividad);
        aplicacioninstalada = preferenciasapp.getBoolean("aplicacioninstalada", Boolean.FALSE);

        if(!aplicacioninstalada)
        {
/*
* Código creación acceso directo
*/
            Intent shortcutIntent = new Intent(actividad, SplashScreenActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,  getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(actividad, R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            actividad.sendBroadcast(intent);

/*
* Indico que ya se ha creado el acceso directo para que no se vuelva a crear mas
*/
            SharedPreferences.Editor editor = preferenciasapp.edit();
            editor.putBoolean("aplicacioninstalada", true);
            editor.apply();
            editor.commit();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

    }

}