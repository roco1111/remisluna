package com.rosario.hp.remisluna;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rosario.hp.remisluna.Adapter.DispositivosAdapter;
import com.rosario.hp.remisluna.Entidades.ItemDispositivo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class ListaBluetoohtActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<ItemDispositivo> dispositivos;
    private DispositivosAdapter adapterDispositivos;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice dispositivoBluetooth;

    private UUID aplicacionUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bluetooht);

        recycler = (RecyclerView) findViewById(R.id.recycler_dispositivos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        dispositivos = new ArrayList<ItemDispositivo>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            if(!bluetoothAdapter.isEnabled()){// si no está activado
                // Mandamos a activarlo
                Intent habilitarBluIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(habilitarBluIntent, 243);
            }else {

                // Obtenemos la lista de dispositivos sincronizados
                Set<BluetoothDevice> dispositivosSync = bluetoothAdapter.getBondedDevices();

                // Si hay dispositivos sincronizados
                if(dispositivosSync.size() > 0){
                    // Llenamos el array de dispositivos para pasarlo al adapter
                    for(BluetoothDevice dispositivo : dispositivosSync){
                        dispositivos.add(new ItemDispositivo(dispositivo.getName(),  dispositivo.getAddress()));
                    }
                }
            }
        }else{
            Toast.makeText(this, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }


        adapterDispositivos = new DispositivosAdapter(new EscuchadorClick(), dispositivos);

        recycler.setAdapter(adapterDispositivos);
    }

    private class EscuchadorClick implements DispositivosAdapter.MiListenerClick{

        @Override
        public void clickItem(View itemView, int posicion) {
            // Mandamos la direccion al onActivityResult de la actividad que lanzo esta
            //Bundle bundle = new Bundle();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("DireccionDispositivo",adapterDispositivos.getDispositivos().get(posicion).getDireccion());
            editor.commit();

            startService(new Intent(getApplicationContext(), Impresion.class));
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed()  {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tipo;
        tipo = settings.getString("tipo_ventana","");

        Intent mainIntent = null;

        switch (tipo) {
            case "main":
                mainIntent = new Intent().setClass(getApplicationContext(), MainActivity.class);
                break;
            case "viaje":
                mainIntent = new Intent().setClass(getApplicationContext(), MainViaje.class);
                break;
            case "datos_viaje":
                mainIntent = new Intent().setClass(getApplicationContext(), Main_datos_viaje.class);
                break;
            case "turno":
                mainIntent = new Intent().setClass(getApplicationContext(), MainTurno.class);
                break;
        }
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK ) {
            if ( requestCode == 243 ) {
                if( bluetoothAdapter.isEnabled() ){
                    // Obtenemos la lista de dispositivos sincronizados
                    Set<BluetoothDevice> dispositivosSync = bluetoothAdapter.getBondedDevices();

                    // Si hay dispositivos sincronizados
                    if(dispositivosSync.size() > 0){
                        // Llenamos el array de dispositivos para pasarlo al adapter
                        for(BluetoothDevice dispositivo : dispositivosSync){
                            dispositivos.add(new ItemDispositivo(dispositivo.getName(),  dispositivo.getAddress()));
                            adapterDispositivos.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }
}