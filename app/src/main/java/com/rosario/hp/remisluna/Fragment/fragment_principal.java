package com.rosario.hp.remisluna.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.DeviceList;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.ListaBluetoohtActivity;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.turnos_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class fragment_principal extends Fragment {

    private static final String TAG = fragment_principal.class.getSimpleName();
    private JsonObjectRequest myRequest;
    private ImageButton viaje;
    private ImageButton historial;
    private ImageButton turno;
    private ImageButton impresora;
    private String ls_id_turno;
    private String recaudacion;
    private String kms;
    private String fecha;
    private String hora_inicio;
    private ArrayList<viaje> viajes = new ArrayList<>();
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    byte FONT_TYPE;
    private static final int REQUEST_DISPOSITIVO = 425;
    private BluetoothSocket bluetoothSocket;
    private TextView txtLabel;
    private volatile boolean pararLectura;
    private InputStream inputStream;
    private Impresion impresion;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_principal, container, false);

        this.viaje = v.findViewById(R.id.imageButtonViaje);
        this.historial = v.findViewById(R.id.imageButtonHistorial);
        this.turno = v.findViewById(R.id.imageButtonTurno);
        this.impresora = v.findViewById(R.id.imageButtonImpresora);
        this.txtLabel = v.findViewById(R.id.referencia);
        impresion = new Impresion();


        this.viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getContext(), MainViaje.class);
                getContext().startActivity(intent2);
            }
        });

        this.historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getContext(), turnos_activity.class);
                getContext().startActivity(intent2);
            }
        });

        this.turno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrar_turno();
            }
        });

        this.impresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new Intent(getActivity(), Impresion.class));
            }
        });

        return v;
    }


    public void datos_turno(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TURNO_BY_ID + "?id=" + ls_id_turno;
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
                    JSONArray mensaje1 = response.getJSONArray("turno");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    fecha = object.getString("fecha");
                    hora_inicio = object.getString("hora_inicio");
                    if(!object.getString("distancia").equals("null")){
                        kms =object.getString("distancia");}
                    if(!object.getString("recaudacion").equals("null")){
                        recaudacion = object.getString("recaudacion");}
                    datos_viajes_turno(context);
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

    public void datos_viajes_turno(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJES_TURNO + "?turno=" + ls_id_turno;
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
                                procesarRespuestaViajes(response, context);
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

    private void procesarRespuestaViajes(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viajes");

                    viajes.clear();

                    for(int i = 0; i < mensaje1.length(); i++) {
                        JSONObject object = mensaje1.getJSONObject(i);

                        viaje via = new viaje();

                        via.setId(String.valueOf(i));

                        String hora = object.getString("hora_inicio");

                        via.setHora_inicio(hora);

                        String importe = object.getString("importe");

                        via.setImporte(importe);

                        viajes.add(via);
                    }

                    ticket_turno(viajes);


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

    private void cerrar_turno(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_turno     = settings.getString("id_turno_chofer","");

        String newURL = Constantes.FIN_TURNO + "?id=" + ls_id_turno;
        Log.d(TAG,newURL);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaCerrarTurno(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error turno: " + error.getMessage());

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
    private void procesarRespuestaCerrarTurno(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    datos_turno(getContext());
                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void ticket_turno( ArrayList<viaje> viajes) {
        btsocket = impresion.getbluetoothSocket();
        if(btsocket == null){
            getActivity().startService(new Intent(getActivity(), Impresion.class));
        }
        else {
            outputStream = impresion.getOutputStream();

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printNewLine();
                printCustom(getResources().getString(R.string.telefono), 0, 1);

                printNewLine();
                printUnicode();
                printNewLine();
                printText(getResources().getString(R.string.ticket_turno)); // total 32 char in a single line

                printNewLine();
                printText(fecha);//fecha
                printText(" - ");
                printText(hora_inicio);//fecha
                printNewLine();
                printNewLine();

                String id;
                String importe;

                for (viaje Viaje : viajes) {
                    printNewLine();
                    id = Viaje.getId();
                    printCustom("VIAJE " + id, 1, 0);

                    importe = Viaje.getImporte();
                    printText("TOTAL:  " + importe);
                    printNewLine();
                }
                printNewLine();
                printText("K.TOTAL:  ");
                printText(kms);
                printNewLine();
                printNewLine();
                printText("RECAUDACION: ");
                printText(recaudacion);
                printNewLine();
                printNewLine();
                //resetPrint(); //reset printer
                printUnicode();
                printNewLine();
                printNewLine();

                outputStream.flush();
                Intent intent2 = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent2);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
