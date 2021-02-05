package com.rosario.hp.remisluna;


import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        return null;
    }

    public void onCreate(){
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(startId == 1)
        {
        Log.d("Impresión", "Servicio iniciado...");
        cerrarConexion();
        Intent intentLista = new Intent(getApplicationContext(), ListaBluetoohtActivity.class);
        intentLista.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentLista);
        return START_NOT_STICKY;
        }else{
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String Direccion;
            Direccion = settings.getString("DireccionDispositivo","");
            dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(Direccion);
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        // Conectamos los dispositivos

                        // Creamos un socket
                        bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(aplicacionUUID);
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