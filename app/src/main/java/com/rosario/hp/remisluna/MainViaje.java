package com.rosario.hp.remisluna;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.ayuda;
import com.rosario.hp.remisluna.Fragment.fragment_viaje;
import com.rosario.hp.remisluna.Fragment.fragment_viaje_iniciado;
import com.rosario.hp.remisluna.Fragment.login;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
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

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, Impresion.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
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
        ayudas = new ArrayList<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ls_id_conductor     = settings.getString("id","");
        ls_vehiculo         = settings.getString("vehiculo","");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Viaje Asignado");
        i_pasadas = 0;
        cargarDatos(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        MenuItem myMenuItem = menu.findItem(R.id.menu_principal);
        getMenuInflater().inflate(R.menu.sub_menu_ayuda, myMenuItem.getSubMenu());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        Intent intent;
        switch (id) {

            case R.id.menu_ayuda:

                editor.putString("url", "https://remisluna.com.ar/remiseria/pagina_ayuda.php?id=7");
                editor.apply();
                intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                editor.commit();
                break;
            case R.id.menu_ayuda_app:

                editor.putString("url", "https://remisluna.com.ar/remiseria/paginas_ayuda.php");
                editor.apply();
                intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                editor.commit();
                break;
            case R.id.menu_app_impresa:
                if(mBound) {
                    cargarAyudas(getApplicationContext());

                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.menu_ayuda_impresa:
                if(mBound) {
                    cargarAyuda(getApplicationContext());

                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarAyuda(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ID_AYUDA + "?ayuda=7";
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
                                procesarRespuestaAyuda(response);
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

    private void procesarRespuestaAyuda(JSONObject response) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            switch (mensaje) {
                case "1":
                    JSONArray datos_ayuda = response.getJSONArray("ayuda");

                    ayudas.clear();

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = datos_ayuda.getJSONObject(i);
                        ayuda ay = new ayuda();


                        String titulo = object.getString("titulo");

                        ay.setTitulo(titulo);

                        String descripcion = object.getString("descripcion");

                        ay.setDescripcion(descripcion);


                        ayudas.add(ay);

                    }
                    ticket_ayuda_app(ayudas);

                    break;


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarAyudas(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_AYUDAS;
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
                                procesarRespuestaAyudas(response);
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

    private void procesarRespuestaAyudas(JSONObject response) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            if (mensaje.equals("1")) {

                JSONArray datos_ayuda = response.getJSONArray("ayuda");

                ayudas.clear();

                for(int i = 0; i < datos_ayuda.length(); i++)
                {JSONObject object = datos_ayuda.getJSONObject(i);
                    ayuda ay = new ayuda();

                    String id = object.getString("ID");

                    ay.setId(id);

                    String titulo = object.getString("TITULO");

                    ay.setTitulo(titulo);

                    String descripcion = object.getString("DESCRIPCION");

                    ay.setDescripcion(descripcion);


                    ayudas.add(ay);

                }
                ticket_ayuda_app(ayudas);




            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void ticket_ayuda_app( ArrayList<ayuda> ayudas) {

        outputStream = impresion.getOutputStream();

        //print command
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //outputStream.write(printformat);

            //print title
            printUnicode();
            //print normal text
            printCustom(getResources().getString(R.string.empresa), 2, 1);
            printNewLine();
            printCustom(getResources().getString(R.string.menu_ayuda), 1, 1); // total 32 char in a single line

            printNewLine();
            String titulo;
            String descripcion;
            printCustom("",1,0);
            for (ayuda Ayuda : ayudas) {
                titulo = Ayuda.getTitulo();
                printText(stringABytes(titulo));
                printNewLine();

                descripcion = Ayuda.getDescripcion();
                printText(stringABytes(descripcion));
                printNewLine();
            }
            printNewLine();

            //resetPrint(); //reset printer
            printUnicode();
            printNewLine();
            printNewLine();

            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print unicode
    public void printUnicode(){
        try {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
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
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                    } else {
                        locationStart();
                    }
                    break;

                case "2":

                    fragment = new fragment_viaje();
                    break;

            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void locationStart() {

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainViaje(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, (LocationListener) Local);

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MainViaje mainViaje;
        public MainViaje getMainViaje() {
            return mainViaje;
        }
        public void setMainViaje(MainViaje mainActivity) {
            this.mainViaje = mainViaje;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            String latitud;
            String longitud;
            latitud = String.valueOf(loc.getLatitude());
            longitud = String.valueOf(loc.getLongitude());
            guardar_trayectoria(latitud, longitud);
            String Text = "Lat = "+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
            Log.d("Guardar trayectoria",Text);
            setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Log.d("ubicación","GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Log.d("ubicación","GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    private void guardar_ubicacion(final String latitud, final String longitud){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("latitud", latitud);
        map.put("longitud", longitud);
        map.put("id", ls_vehiculo);


        // Crear nuevo objeto Json basado en el mapa
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

        String newURL = Constantes.UPDATE_UBICACION + "?" + encodedParams;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarUbicacion(response, latitud, longitud);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error inicio: " + error.getMessage());

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
    private void procesarRespuestaActualizarUbicacion(JSONObject response, String latitud, String longitud) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
            if(i_pasadas == 5){
                guardar_ubicacion(latitud,longitud);
                i_pasadas = 0;
            }else{
                i_pasadas++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void guardar_trayectoria(String latitud, String longitud){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        id_trayecto++;
        map.put("id_viaje", ls_viaje);
        map.put("id_trayecto", String.valueOf(id_trayecto));
        map.put("latitud", latitud);
        map.put("longitud", longitud);

        // Crear nuevo objeto Json basado en el mapa
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

        String newURL = Constantes.INSERTAR_TRAYECTORIA + "?" + encodedParams;
        Log.d(TAG,newURL);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
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
                                Log.d(TAG, "Error trayectoria: " + error.getMessage());

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

            switch (estado) {
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}