package com.rosario.hp.remisluna;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.ayuda;
import com.rosario.hp.remisluna.Fragment.fragment_vacia;
import com.rosario.hp.remisluna.Fragment.fragment_viaje;
import com.rosario.hp.remisluna.Fragment.fragment_viaje_iniciado;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class MainViaje extends AppCompatActivity {

    private JsonObjectRequest myRequest;
    private static final String TAG = MainViaje.class.getSimpleName();
    public static final String ACTION_NOTIFY_NEW_PROMO = "NOTIFY_NEW_PROMO";
    String ls_id_conductor;
    String ls_vehiculo;
    String ls_viaje;
    Integer id_trayecto;
    Integer i_pasadas;
    private ArrayList<ayuda> ayudas;
    boolean mBound = false;
    private static OutputStream outputStream;
    private Impresion impresion;
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 12;
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = 0;
    private static FragmentManager fragmentManager;
    private Activity act;

    @Override
    protected void onStart() {
        super.onStart();
        fragmentManager = getSupportFragmentManager();
        // Bind to LocalService
        //Intent intent = new Intent(this, Impresion.class);
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unbindService(connection);
        //mBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Impresion.LocalBinder binder = (Impresion.LocalBinder) service;
            impresion = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_basica);
        act = MainViaje.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ayudas = new ArrayList<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ls_id_conductor     = settings.getString("id","");
        ls_vehiculo         = settings.getString("vehiculo","");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Viaje Asignado");
        i_pasadas = 0;
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new fragment_vacia();


        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();

        cargarParametroImpresora(getApplicationContext());

    }
    private void habilitar_gps(){
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
    }

    private void permiso_back(){
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,}, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            //this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
    public void onBackPressed() {
        this.finish();
    }

    public void cargarParametroImpresora(final Context context) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String ls_remiseria = settings.getString("remiseria","");

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "18");
        map.put("remiseria", ls_remiseria);

        JSONObject jobject = new JSONObject(map);


        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8"));
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ID_PARAMETRO + "?" + encodedParams;
        Log.d(TAG,newURL);

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuestaParametroimpresion(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley tarifa desde: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaParametroimpresion(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");

                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                        SharedPreferences.Editor editor = settings1.edit();


                        editor.putString("impresion",object.getString("valor"));

                        cargarDatos(context);

                    }

                    break;
                case "2":
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarDatos(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_EN_CURSO + "?conductor=" + ls_id_conductor;
        Log.d(TAG,newURL);

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuesta(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley viaje: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            Fragment fragment = null;
            switch (mensaje) {
                case "1":
                    fragment = new fragment_viaje_iniciado();
                    JSONArray mensaje1 = response.getJSONArray("viaje");
                    JSONObject object = mensaje1.getJSONObject(0);
                    id_trayecto = 0;
                    ls_viaje = object.getString("id");

                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .commit();

                    permiso_back();
                    break;

                case "2":
                    cargarDatos_solicitados(context);
                    break;

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarDatos_solicitados(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_SOLICITADOS + "?conductor=" + ls_id_conductor;
        Log.d(TAG,newURL);

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json
                                procesarRespuesta_solicitados(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley viaje: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta_solicitados(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            Fragment fragment = null;
            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("viaje");
                    JSONObject object = mensaje1.getJSONObject(0);

                    ls_vehiculo = object.getString("id_movil");

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    String ls_viaje, ls_remiseria, ls_telefono_queja, ls_telefono;


                    ls_viaje = object.getString("id");

                    editor.putString("id_viaje",ls_viaje);

                    ls_remiseria = object.getString("remiseria");

                    editor.putString("nombre_remiseria",ls_remiseria);

                    ls_telefono_queja = object.getString("telefono_queja");

                    editor.putString("telefono_queja",ls_telefono_queja);

                    ls_telefono = object.getString("telefono");

                    editor.putString("telefono_remiseria",ls_telefono);

                    editor.apply();


                    fragment = new fragment_viaje();


                    fragmentManager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .commit();
                    habilitar_gps();
                    break;

                case "2":
                    Toast.makeText(
                            getApplicationContext(),
                            "No hay viajes asignados",
                            Toast.LENGTH_LONG).show();

                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    getApplicationContext().startActivity(intent2);
                    finish();
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }


}