package com.rosario.hp.remisluna;
 
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.Window;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends Activity {
 
    // Set the duration of the splash screen
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private static final long SPLASH_SCREEN_DELAY = 3000;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String id_firebase;
    Activity act;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crearAccesoDirectoAlInstalar(this);

        act = this;
 
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        setContentView(R.layout.splash);

        mAuth = FirebaseAuth.getInstance();


 
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Task<String> l_id_firebase;
                l_id_firebase = FirebaseMessaging.getInstance().getToken();
                id_firebase = l_id_firebase.toString();
                currentUser = mAuth.getCurrentUser();

                if (currentUser == null) {
                     //Start the next activity

                    Intent mainIntent = new Intent().setClass(
                           SplashScreenActivity.this,Main_Login.class);
                   startActivity(mainIntent);
                    finish();



                }else{

                    if(verificar_internet()) {
                        actualizar_token(String.valueOf(id_firebase));
                    }else{
                        sin_internet();
                        }

              }
                // Close the activity so the user won't able to go back this
                // activity pressing Back button

            }
        };
 
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

    public void sin_internet()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);

                        alertDialogBuilder.setTitle("Parece que no hay internet");

                        alertDialogBuilder
                                .setMessage("Compruebe su conexión a internet para seguir utilizando la app")
                                .setCancelable(false)
                                .setPositiveButton("Cerrar",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        act.finish();
                                        finishAffinity ();
                                        System.exit(0);
                                    }
                                })
                                .setNegativeButton("Continuar",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,int id) {
                                        Intent intent2 = new Intent(act, SplashScreenActivity.class);
                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        act.startActivity(intent2);
                                    }
                                })
                                .create().show();
                    }
                }
        );
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
            editor.apply();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

    }

    private Boolean verificar_internet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            return true;
        } else {
            // No hay conexión a Internet en este momento
            return false;
        }
    }

    private void actualizar_token(String token){
        // TODO: Send any registration to your app's servers.

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ls_id_conductor     = settings.getString("id","");

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_id_conductor);
        map.put("id_firebase", token);

        JSONObject jobject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8")).toString();
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));

        String newURL = Constantes.UPDATE_TOKEN + "?" + encodedParams;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(act).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizar(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley Token: " + error.getMessage());

                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    private void procesarRespuestaActualizar(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            Intent mainIntent = new Intent().setClass(
                    SplashScreenActivity.this, MainActivity.class);
            //Intent mainIntent = new Intent().setClass(
             //     SplashScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}