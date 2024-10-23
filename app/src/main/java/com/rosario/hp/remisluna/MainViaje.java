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
import com.rosario.hp.remisluna.Fragment.fragment_viaje_iniciado;
import com.rosario.hp.remisluna.Fragment.fragment_viaje_vacia;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    String  ls_remiseria;
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
        ls_remiseria = settings.getString("remiseria","");

        fragmentManager = getSupportFragmentManager();

        Fragment fragment = new fragment_viaje_vacia();


        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();

        feriado(getApplicationContext());

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

    public void feriado(final Context context){
        String newURL = Constantes.GET_FERIADO;
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

                                procesarRespuestaFeriado(response, context);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley feriado: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaFeriado(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("feriado",response.getString("feriado"));
            editor.apply();

            cargarRemiseria(context);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarRemiseria(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_REMISERIA + "?remiseria=" + ls_remiseria;
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
                                procesarRespuesta_remiserias(response, context);
                            }
                        },
                        error -> Log.d(TAG, "Error Volley viaje: " + error.getMessage())
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta_remiserias(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("remiseria");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("habilitada",object.getString("HABILITADA"));
                    editor.putString("telefono_base",object.getString("TELEFONO_BASE"));
                    editor.putString("porcentaje", object.getString("porcentaje"));
                    editor.putString("tarifa_desde",object.getString("tarifa_desde"));
                    editor.putString("tarifa_hasta",object.getString("tarifa_hasta"));
                    editor.putInt("metros_ficha", Integer.parseInt(object.getString("metros_ficha")));
                    editor.putLong("tiempo_espera",Long.parseLong(object.getString("tiempo_espera")) * 1000);
                    editor.putString("turno_app",object.getString("turno_app"));
                    editor.putString("impresion",object.getString("impresion"));
                    editor.putString("mercado_pago",object.getString("mercado_pago"));
                    editor.putString("tipo_rendicion",object.getString("tipo_rendicion"));
                    editor.putString("valor_dia_rendicion",object.getString("valor_dia_rendicion"));
                    editor.putString("valor_noche_rendicion",object.getString("valor_noche_rendicion"));
                    editor.putString("valor_feriado_rendicion",object.getString("valor_feriado_rendicion"));
                    Long l_tolerancia_tope = Long.parseLong(object.getString("tiempo_tolerancia")) * 60000;

                    editor.putLong("tolerancia_tope",l_tolerancia_tope);
                    editor.apply();

                    cargarDatos(context);


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

            switch (mensaje) {
                case "1":

                    JSONArray mensaje1 = response.getJSONArray("viaje");
                    JSONObject object = mensaje1.getJSONObject(0);
                    id_trayecto = 0;
                    ls_viaje = object.getString("id");

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    editor.putString("id_viaje",object.getString("id"));
                    editor.putString("solicitante",object.getString("solicitante"));
                    editor.putString("salida",object.getString("salida"));
                    editor.putString("destino",object.getString("destino"));
                    editor.putString("id_movil",object.getString("id_movil"));
                    editor.putString("porc_titular",object.getString("porc_titular"));
                    editor.putString("importe_ficha",object.getString("importe_ficha"));
                    editor.putString("importe_ficha_nocturno",object.getString("importe_ficha_nocturno"));

                    editor.putString("importe_espera",object.getString("importe_espera"));
                    editor.putString("importe_espera_nocturno",object.getString("importe_espera_nocturno"));

                    editor.putString("importe_bajada",object.getString("importe_bajada"));
                    editor.putString("bajada",object.getString("bajada"));
                    editor.putString("importe_bajada_nocturno",object.getString("importe_bajada_nocturno"));

                    editor.putString("movil",object.getString("movil"));
                    editor.putString("total",object.getString("total"));
                    editor.putString("tiempo",object.getString("tiempo"));
                    editor.putString("fichas",object.getString("fichas"));
                    editor.putString("importe_espera_viaje",object.getString("importe_espera_viaje"));
                    editor.putString("fichas_espera",object.getString("fichas_espera"));
                    editor.putString("trayectoria",object.getString("trayectoria"));
                    editor.putString("tipo_espera",object.getString("tipo_espera"));
                    editor.putString("importe_fichas",object.getString("importe_fichas"));
                    editor.putString("importe_espera_viaje",object.getString("importe_espera_viaje"));
                    editor.putString("salida_coordenadas",object.getString("salida_coordenadas"));
                    editor.putLong("tiempo_tolerancia",Long.parseLong(object.getString("tiempo_tolerancia")));
                    editor.putLong("tiempo_acumulado",Long.parseLong(object.getString("tiempo_acumulado")));
                    editor.putString("viajes_automaticos_chofer",object.getString("viajes_automaticos"));
                    editor.putString("estado_viaje","en curso");
                    editor.putString("chofer",object.getString("chofer"));


                    editor.apply();
                    fragmentManager = getSupportFragmentManager();
                    Fragment fragment = new fragment_viaje_iniciado();


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

                    Log.d("viaje asignado main",ls_viaje);

                    ls_remiseria = object.getString("remiseria");

                    editor.putString("nombre_remiseria",ls_remiseria);

                    ls_telefono_queja = object.getString("telefono_queja");

                    editor.putString("telefono_queja",ls_telefono_queja);

                    ls_telefono = object.getString("telefono");

                    editor.putString("telefono_remiseria",ls_telefono);
                    editor.putString("estado_viaje","asignado");
                    editor.putString("salida_coordenadas",object.getString("salida_coordenadas"));
                    editor.putString("destino_coordenadas",object.getString("destino_coordenadas"));
                    editor.putString("id_movil",object.getString("id_movil"));
                    editor.putString("salida",object.getString("salida"));
                    editor.putString("destino",object.getString("destino"));
                    editor.putString("solicitante",object.getString("solicitante"));
                    editor.putString("porc_titular",object.getString("porc_titular"));
                    editor.putString("viajes_automaticos_chofer",object.getString("viajes_automaticos"));
                    editor.putString("saldo_vehiculo",object.getString("saldo_vehiculo"));
                    editor.putString("chapa",object.getString("chapa"));
                    editor.putString("nro_movil",object.getString("movil"));
                    editor.putString("chofer",object.getString("chofer"));

                    editor.apply();

                    fragmentManager = getSupportFragmentManager();
                    Fragment fragment = new fragment_viaje_iniciado();


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