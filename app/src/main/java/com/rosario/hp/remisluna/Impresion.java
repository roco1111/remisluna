package com.rosario.hp.remisluna;


import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    private Context context;
    private UUID deviceUUID;

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

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetoothSocket = null;
        Log.d("startid", String.valueOf(startId));
        context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String l_impresora = settings.getString("impresora", "");
        String l_conductor = settings.getString("id", "");
        if (!l_impresora.equals("")) {
            Log.d("startid_a", String.valueOf(startId));
            try {
                dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(l_impresora);
            } catch (IllegalArgumentException exception) {
                Log.w("imp", "Device not found with provided address.");
                return START_NOT_STICKY;
            }
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                            // Conectamos los dispositivos

                            // Creamos un socket

                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                //return;
                            }

                            ParcelUuid list[] = dispositivoBluetooth.getUuids();
                            if (list != null) {
                                deviceUUID = UUID.fromString(list[0].toString());

                                bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(deviceUUID);

                                Log.d("impresora", "socket");
                                int bond = dispositivoBluetooth.getBondState();
                                if (!bluetoothSocket.isConnected() ) {
                                    bluetoothSocket.connect();// conectamos el socket

                                    outputStream = bluetoothSocket.getOutputStream();
                                    Log.d("impresora", "outputStream");
                                    inputStream = bluetoothSocket.getInputStream();
                                    Log.d("impresora", "inputStream");
                                }
                            }else{
                                showToastBlu(getApplicationContext());
                            }


                        } catch (IOException e) {

                            Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth " + e);



                            showToast(getApplicationContext());
                        }

                    }
                };
            t.start();

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
                verificar_impresora(getApplicationContext(),Direccion,l_conductor);
                try {
                    dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(l_impresora);
                } catch (IllegalArgumentException exception) {
                    Log.w("imp", "Device not found with provided address.");
                    return START_NOT_STICKY;
                }
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Conectamos los dispositivos
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                //return;
                            }
                            // Creamos un socket

                            ParcelUuid list[] = dispositivoBluetooth.getUuids();
                            if (list != null) {
                                deviceUUID = UUID.fromString(list[0].toString());
                                bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(deviceUUID);

                                Log.d("impresora", "socket2");
                                if (!bluetoothSocket.isConnected()) {
                                    bluetoothSocket.connect();// conectamos el socket
                                    outputStream = bluetoothSocket.getOutputStream();
                                    inputStream = bluetoothSocket.getInputStream();
                                }
                            }else{
                                showToastBlu(getApplicationContext());
                            }



                            //empezarEscucharDatos();


                        } catch (IOException e) {

                            Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth");
                            showToast(getApplicationContext());

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
                try {
                    bluetoothSocket.close();
                    Log.e(TAG_DEBUG, "cerrado");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showToastBlu(Context ctx) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(ctx, "El bluetooth no está conectado", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void verificar_impresora(final Context context, final String impresora, final String conductor) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("conductor", conductor);
        map.put("impresora", impresora);

        JSONObject jobject = new JSONObject(map);


        // Depurando objeto Json...
        Log.d("Impresora", jobject.toString());

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

        String newURL = Constantes.VERIFICAR_IMPRESORA + "?" + encodedParams;
        Log.d("Impresora",newURL);

        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaImpresora(response, context, impresora, conductor);
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

    private void procesarRespuestaImpresora(JSONObject response, Context context,final String impresora, final String conductor ) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parada = response.getJSONArray("conductor");

                    for(int i = 0; i < datos_parada.length(); i++)
                    {
                        JSONObject object = datos_parada.getJSONObject(i);

                        String cantidad = object.getString("cantidad");

                        if(cantidad.equals("0"))
                        {
                            guardar_impresora(impresora, conductor,context);
                        }else{
                            Toast.makeText(
                                    context,
                                    "La impresora ya fue asignada a otro chofer",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void guardar_impresora(String impresora, String conductor, final Context context){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("impresora", impresora);
        map.put("id", conductor);


        // Crear nuevo objeto Json basado en el mapa

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
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()

        cerrarConexion();

        return false;
    }
}