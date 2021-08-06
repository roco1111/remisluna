package com.rosario.hp.remisluna;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Fragment.login;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author mikesaurio
 *
 */
public class ServicioGeolocalizacion extends Service implements Runnable {
    /**
     * Declaraci—n de variables
     */
    public static MainViaje taxiActivity;
    private LocationManager mLocationManager;
    private MyLocationListener mLocationListener;
    private double latitud_inicial = 0;
    private double longitud_inicial = 0;
    private double distancia_acumulada = 0;
    private Location currentLocation = null;
    private Thread thread;
    private String ls_id_conductor;
    private JsonObjectRequest myRequest;
    private static final String TAG = login.class.getSimpleName();
    private String salida_coordenada;
    private Long l_inicio;
    private Long l_final;
    private Long l_diferencia = 0L;
    private Long tiempo_acumulado = 0L;
    private Long tiempo_tolerancia = 0L;
    private boolean lb_torerancia = true;
    private Integer minutos;
    private Integer segundos;
    private Integer resto;
    private String l_tiempo, l_tiempo_inicial;
    private Integer l_tipo;
    

    @Override
    public void onCreate() {
        Toast.makeText(this, "Taxímetro iniciado", Toast.LENGTH_SHORT).show();
        super.onCreate();
        distancia_acumulada = 0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ls_id_conductor     = settings.getString("id","");
        cargarDatos(getApplicationContext());
        mLocationListener = new MyLocationListener();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viaje");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    salida_coordenada = object.getString("salida_coordenadas");

                    if(!object.getString("tiempo_tolerancia").equals("null")) {

                        tiempo_tolerancia = Long.parseLong(object.getString("tiempo_tolerancia"));

                    }

                    if(!object.getString("tiempo_acumulado").equals("null")) {

                        tiempo_acumulado = Long.parseLong(object.getString("tiempo_acumulado"));

                    }

                    actualizar_coordenadas();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void actualizar_coordenadas(){

        Geocoder coder = new Geocoder(getApplicationContext());
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(salida_coordenada, 50);
            for(Address add : adresses){
                if (!adresses.isEmpty()) {

                    longitud_inicial = add.getLongitude();
                    latitud_inicial = add.getLatitude();
                    l_inicio = System.currentTimeMillis();

                } } }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        // Toast.makeText(this,"Servicio arrancado "+ idArranque,Toast.LENGTH_SHORT).show();
        obtenerSenalGPS();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        if (mLocationManager != null)
            if (mLocationListener != null)
                mLocationManager.removeUpdates(mLocationListener);

        Toast.makeText(this, "Taxímetro detenido ", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    /**
     * handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // mLocationManager.removeUpdates(mLocationListener);
            updateLocation(currentLocation);
        }
    };


    /**
     * metodo para actualizar la localizaci—n
     *
     * @param currentLocation
     * @return void
     */
    public void updateLocation(Location currentLocation) {
        if (currentLocation != null && latitud_inicial != 0) {
            double latitud = Double.parseDouble(currentLocation.getLatitude() + "");
            double longitud = Double.parseDouble(currentLocation.getLongitude() + "");

            Location locationA = new Location("punto A");
            locationA.setLatitude(latitud_inicial);
            locationA.setLongitude(longitud_inicial);

            Location locationB = new Location("punto B");
            locationB.setLatitude(latitud);
            locationB.setLongitude(longitud);

            float distance = locationA.distanceTo(locationB) ;

            latitud_inicial = latitud;
            longitud_inicial = longitud;



            if(distance > 6) {
                l_tipo = 1;
            }else{
                l_tipo = 2;
            }

            Log.d("DISTANCIA recorrida",String.valueOf(distance));


            if(l_tipo == 1) {
                l_inicio = System.currentTimeMillis();
                tiempo_acumulado = 0L;
                distancia_acumulada += distance;
                distancia_acumulada = Double.valueOf(getTwoDecimals(distancia_acumulada));
                if (distancia_acumulada >= 100) {
                    distancia_acumulada = distancia_acumulada - 100;

                    getApplicationContext().sendBroadcast(
                            new Intent("key").putExtra("coordenadas", latitud + ";"
                                    + longitud + ";" + 1 + ""));
                }

            }else if (l_tipo == 2){

                l_final = System.currentTimeMillis();
                l_diferencia = l_final - l_inicio;
                if(l_diferencia < 0) {
                    Log.d("diferencia negativa", "ok");
                }
                l_inicio = l_final;
                if(lb_torerancia){
                    tiempo_tolerancia += l_diferencia;
                    Log.d("tiempo_tolerancia",String.valueOf(tiempo_tolerancia));

                    if(tiempo_tolerancia / 60000 >= 1){
                        minutos = Integer.parseInt(String.valueOf(tiempo_tolerancia)) / 60000;
                        if(tiempo_tolerancia % 60000 > 0){
                            resto = Integer.parseInt(String.valueOf(tiempo_tolerancia)) % 60000;
                            segundos = resto / 1000;
                        }
                    }else{
                        minutos = 0;
                        segundos = Integer.parseInt(String.valueOf(tiempo_tolerancia)) / 1000;
                    }
                    String min="", seg="";
                    if (minutos < 10) min = "0" + minutos.toString();
                    else min = minutos.toString();
                    if (segundos < 10) seg = "0" + segundos.toString();
                    else seg = segundos.toString();

                    l_tiempo = min + ":" + seg;

                    tiempo_acumulado = 0L;
                    if(tiempo_tolerancia >= 300000){
                        lb_torerancia = false;
                    }
                    getApplicationContext().sendBroadcast(
                            new Intent("key").putExtra("coordenadas", latitud + ";"
                                    + longitud + ";" + 3 + ";"+ l_tiempo + ";"+ String.valueOf(tiempo_tolerancia)));

                }else {
                    tiempo_acumulado += l_diferencia;
                    Log.d("tiempo_acumulado","ok");
                    if(tiempo_acumulado >= 60000){

                        tiempo_acumulado = 0L;
                        getApplicationContext().sendBroadcast(
                                new Intent("key").putExtra("coordenadas", latitud + ";"
                                        + longitud + ";" + 2 + ""));
                    }else{
                        if(tiempo_acumulado / 60000 >= 1){
                            minutos = Integer.parseInt(String.valueOf(tiempo_acumulado)) / 60000;
                            if(tiempo_acumulado % 60000 > 0){
                                resto = Integer.parseInt(String.valueOf(tiempo_acumulado)) % 60000;
                                segundos = resto / 1000;
                            }
                        }else{
                            minutos = 0;
                            segundos = Integer.parseInt(String.valueOf(tiempo_acumulado)) / 1000;
                        }
                        String min="", seg="";
                        if (minutos < 10) min = "0" + minutos.toString();
                        else min = minutos.toString();
                        if (segundos < 10) seg = "0" + segundos.toString();
                        else seg = segundos.toString();

                        l_tiempo = min + ":" + seg;
                        getApplicationContext().sendBroadcast(
                                new Intent("key").putExtra("coordenadas", latitud + ";"
                                        + longitud + ";" + 4 + ";"+ l_tiempo+ ";" + String.valueOf(tiempo_acumulado)));
                    }
                }

            }

        }
    }

    private static String getTwoDecimals(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }


    /**
     * Hilo de la aplicacion para cargar las cordenadas del usuario
     */
    public void run() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Looper.prepare();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            Looper.loop();
            Looper.myLooper().quit();
        } else {
            taxiActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "GPS apagado inesperadamente", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Metodo para Obtener la se–al del GPS
     */
    private void obtenerSenalGPS() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Metodo para asignar las cordenadas del usuario
     * */
    private void setCurrentLocation(Location loc) {
        currentLocation = loc;
    }

    /**
     * Metodo para obtener las cordenadas del GPS
     */
    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            Log.d("Cambio",loc.getAccuracy()+"");
            if (loc != null) {
                setCurrentLocation(loc);
                handler.sendEmptyMessage(0);
            }
        }

        /**
         * metodo que revisa si el GPS esta apagado
         */
        public void onProviderDisabled(String provider) {
            taxiActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "GPS apagado inesperadamente", Toast.LENGTH_LONG).show();
                    Log.d("gps", "gps apagado");
                }
            });
        }

        // @Override
        public void onProviderEnabled(String provider) {
            Log.d("gps", "gps prendido");
        }

        // @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("status", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("status", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("status", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

}
