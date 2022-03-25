package com.rosario.hp.remisluna;


import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Impresion extends Service {

    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private volatile boolean pararLectura;
    private InputStream inputStream;
    private UUID aplicacionUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice dispositivoBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG_DEBUG = "impresion";
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public Impresion getService() {
            // Return this instance of LocalService so clients can call public methods
            return Impresion.this;
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public BluetoothSocket getbluetoothSocket() {
        return bluetoothSocket;
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onCreate(){
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String l_impresora  = settings.getString("impresora","");
        String l_conductor = settings.getString("id","");
        if(!l_impresora.equals("")){
            if (startId == 1) {
                dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(l_impresora);
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Conectamos los dispositivos

                            // Creamos un socket
                            bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(aplicacionUUID);
                            Log.d("impresora", "socket");
                            bluetoothSocket.connect();// conectamos el socket
                            outputStream = bluetoothSocket.getOutputStream();
                            inputStream = bluetoothSocket.getInputStream();

                            //empezarEscucharDatos();


                        } catch (IOException e) {

                            Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth");

                            showToast(getApplicationContext());
                        }

                    }
                };
                t.start();

                return START_STICKY;
            }
        }else {
            if (startId == 1) {
                Log.d("Impresión", "Servicio iniciado...");
                cerrarConexion();
                Intent intentLista = new Intent(getApplicationContext(), ListaBluetoohtActivity.class);
                intentLista.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentLista);
                return START_NOT_STICKY;
            } else {
                Log.d("Impresión", "Servicio ya iniciado...");
                settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String Direccion;
                Direccion = settings.getString("DireccionDispositivo", "");
                guardar_impresora(Direccion,l_conductor,getApplicationContext());
                dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(Direccion);
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Conectamos los dispositivos

                            // Creamos un socket
                            bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(aplicacionUUID);
                            Log.d("impresora","socket2");
                            bluetoothSocket.connect();// conectamos el socket
                            outputStream = bluetoothSocket.getOutputStream();
                            inputStream = bluetoothSocket.getInputStream();

                            //empezarEscucharDatos();


                        } catch (IOException e) {

                            Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth");
                            //Toast.makeText(getApplicationContext(), "No se pudo conectar el dispositivo", Toast.LENGTH_SHORT).show();
                        }

                    }
                };
                t.start();

                return START_STICKY;
            }
        }
        return START_STICKY;
    }

    private void showToast(Context ctx) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(ctx, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardar_impresora(String impresora, String conductor, final Context context){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("impresora", impresora);
        map.put("id", conductor);


        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);

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

        String newURL = Constantes.UPDATE_IMPRESORA + "?" + encodedParams;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarPosicion(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Impresión", "Error impresora: " + error.getMessage());

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
    private void procesarRespuestaActualizarPosicion(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        System.out.println("El servicio a Terminado");
    }

    private void cerrarConexion() {
        try {
            if (bluetoothSocket != null) {
                if (outputStream != null) outputStream.close();
                pararLectura = true;
                if (inputStream != null) inputStream.close();
                bluetoothSocket.close();
                Log.d("impresión","Conexion terminada");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}