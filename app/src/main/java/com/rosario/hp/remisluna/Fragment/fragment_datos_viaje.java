package com.rosario.hp.remisluna.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.viajes_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class fragment_datos_viaje extends Fragment{

    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_datos_viaje.class.getSimpleName();
    private TextView fecha;
    private TextView nro_viaje;
    private TextView solicitante;
    private TextView documento;
    private TextView salida;
    private TextView destino;
    private TextView hora_salida;
    private TextView hora_destino;
    private TextView importe;
    private TextView motivo;
    private String chofer;
    private String distancia;
    private String fecha_tarifa;
    private String movil;
    private LinearLayout suspension;
    private Button imprimir;

    private String ls_id_viaje;
    private Impresion impresion;
    boolean mBound = false;
    private ArrayList<viaje> viaje = new ArrayList<>();
    private static OutputStream outputStream;
    byte FONT_TYPE;

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getActivity(), Impresion.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(connection);
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
            if(impresion.getbluetoothSocket() != null){
                mBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_datos_viaje, container, false);


        fecha = v.findViewById(R.id.dato_fecha);
        nro_viaje = v.findViewById(R.id.dato_viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        documento = v.findViewById(R.id.dato_documento);
        salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        hora_salida = v.findViewById(R.id.hora_salida);
        hora_destino = v.findViewById(R.id.hora_destino);
        importe = v.findViewById(R.id.dato_importe);
        motivo = v.findViewById(R.id.dato_motivo);
        impresion = new Impresion();
        imprimir = v.findViewById(R.id.buttonTicket);
        suspension = v.findViewById(R.id.id_suspension);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_viaje     = settings.getString("id_viaje","");
        cargarDatos(getContext());

        this.imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("tipo_ventana","datos_viaje");
                    editor.commit();
                    getActivity().startService(new Intent(getActivity(), Impresion.class));
                } else {
                    printTicket();
                }
            }
        });


        return v;
    }

    public void cargarDatos(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_BY_ID + "?id=" + ls_id_viaje;
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
                                Log.d(TAG, "Error Volley turno: " + error.getMessage());

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

                    fecha.setText(object.getString("fecha"));
                    nro_viaje.setText(object.getString("id"));
                    solicitante.setText(object.getString("solicitante"));
                    documento.setText(object.getString("nro_documento"));
                    salida.setText(object.getString("salida"));
                    destino.setText(object.getString("destino"));
                    hora_salida.setText(object.getString("hora_inicio"));
                    hora_destino.setText(object.getString("hora_fin"));
                    importe.setText(object.getString("importe"));
                    motivo.setText(object.getString("motivo"));
                    chofer = object.getString("chofer");
                    distancia = object.getString("distancia");
                    fecha_tarifa = object.getString("fecha_tarifa");
                    movil = object.getString("movil");
                    if(object.getString("estado").equals("4")){
                        suspension.setVisibility(View.VISIBLE);
                    }else{
                        suspension.setVisibility(View.INVISIBLE);
                    }

                case "2":
                    Toast.makeText(
                            getContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void printTicket() {

        outputStream = impresion.getOutputStream();

        //print command
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            byte[] printformat = { 0x1B,0x21,0x08 };
            outputStream.write(printformat);

            //print title
            printUnicode();
            //print normal text
            printCustom (getResources().getString(R.string.empresa),2,1);
            printNewLine();
            printPhoto(R.drawable.remisluna_logo_impresion);
            printCustom (getResources().getString(R.string.telefono),1,1);
            printNewLine();
            printText(getResources().getString(R.string.recibo)); // total 32 char in a single line
            printNewLine();
            printText(stringABytes(getResources().getString(R.string.servicio)));
            printNewLine();
            printText(fecha.getText().toString());//fecha
            printNewLine();
            printCustom ("Chofer: " + chofer,1,0);
            printCustom ("Nro Remis: " + movil,1,0);
            printNewLine();
            printText("SALIDA  " + hora_salida.getText());
            printNewLine();
            printText("DESDE  " + salida.getText().toString());
            printNewLine();
            printText("HASTA  " + destino.getText().toString());
            printNewLine();
            printText("LLEGADA  " + hora_destino.getText());
            printNewLine();
            printText("RECORRIDO  " + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(distancia)) + " Kms.");
            printNewLine();
            printNewLine();
            printText("TARIFA AL  " + fecha_tarifa);
            printNewLine();
            printText("VIAJE  " + '$' + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(importe.getText().toString())));
            printNewLine();
            printText("ESPERA  ");
            printNewLine();
            printNewLine();
            printCustom ("TOTAL:  " + '$' + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(importe.getText().toString())),2,0);
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

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
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

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }
}
