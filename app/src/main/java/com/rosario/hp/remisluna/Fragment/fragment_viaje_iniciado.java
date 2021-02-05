package com.rosario.hp.remisluna.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothSocket;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.DeviceList;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class fragment_viaje_iniciado extends Fragment {
    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_viaje_iniciado.class.getSimpleName();
    private String ls_id_conductor;
    private TextView id_viaje;
    private TextView solicitante;
    private TextView documento;
    private TextView dato_salida;
    private TextView destino;
    private String hora_inicio;
    private String hora_fin;
    private String fecha_tarifa;
    private String fecha;
    private String distancia;
    private String chofer;
    private String precio;
    private String id_vehiculo;
    private String id_turno;
    private Button terminar;
    private Button suspender;
    private Button alarma;
    private String importe_bajada;
    private String importe_ficha;
    private String l_latitud_destino;
    private String l_longitud_destino;
    private Double latitud_salida;
    private Double longitud_salida;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    private Impresion impresion;
    byte FONT_TYPE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_viaje_iniciado, container, false);

        id_viaje = v.findViewById(R.id.dato_viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        documento = v.findViewById(R.id.dato_documento);
        dato_salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        terminar = v.findViewById(R.id.buttonTerminar);
        suspender = v.findViewById(R.id.buttonSuspender);
        alarma = v.findViewById(R.id.buttonAlarma);

        impresion = new Impresion();

        conectar_impresora();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_conductor     = settings.getString("id","");
        id_turno            = settings.getString("id_turno_chofer","");

        this.terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cargarDatosVehiculo(getContext());
            }
        });

        this.suspender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suspender_viaje();
            }
        });

        this.alarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarma_viaje();
            }
        });

        cargarDatos(getContext());

        return v;
    }

    protected void conectar_impresora(){
        outputStream = impresion.getOutputStream();

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

                    id_viaje.setText(object.getString("id"));
                    solicitante.setText(object.getString("solicitante"));
                    documento.setText(object.getString("nro_documento"));
                    dato_salida.setText(object.getString("salida"));
                    destino.setText(object.getString("destino"));
                    id_vehiculo = object.getString("movil");
                    importe_bajada = object.getString("importe_bajada");
                    importe_ficha = object.getString("importe_ficha");
                    latitud_salida = Double.parseDouble(object.getString("latitud_salida"));
                    longitud_salida = Double.parseDouble(object.getString("longitud_salida"));

                    break;

                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje2,
                            Toast.LENGTH_LONG).show();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void cargarDatosFinal(final Context context) {

        String ls_viaje = id_viaje.getText().toString();

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_TERMINADO + "?id=" + ls_viaje;
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
                                procesarRespuestaFinal(response, context);
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

    private void procesarRespuestaFinal(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viaje");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    hora_inicio = object.getString("hora_inicio");
                    hora_fin = object.getString("hora_fin");
                    fecha_tarifa = object.getString("fecha_tarifa");
                    fecha = object.getString("fecha");
                    chofer = object.getString("chofer");

                    printTicket();

                    break;

                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje2,
                            Toast.LENGTH_LONG).show();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarDatosVehiculo(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VEHICULO + "?id=" + id_vehiculo;
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
                                procesarRespuestaVehiculo(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley vehiculo: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaVehiculo(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("vehiculo");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    l_latitud_destino = object.getString("latitud");
                    l_longitud_destino = object.getString("longitud");

                    terminar_viaje();

                    break;

                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje2,
                            Toast.LENGTH_LONG).show();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void terminar_viaje(){

        String ls_viaje = id_viaje.getText().toString();

        Location location_salida = new Location("salida");
        location_salida.setLatitude(latitud_salida);  //latitud
        location_salida.setLongitude(longitud_salida); //longitud
        Location location_destino = new Location("destino");
        location_destino.setLatitude(Double.parseDouble(l_latitud_destino));  //latitud
        location_destino.setLongitude(Double.parseDouble(l_longitud_destino)); //longitud
        double distance = location_salida.distanceTo(location_destino);
        distancia = String.valueOf(distance);

        Double importe = Double.parseDouble(importe_bajada) + ((distance * 10) * Double.parseDouble(importe_ficha));

        precio = String.valueOf(importe);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_viaje);
        map.put("latitud", l_latitud_destino);
        map.put("longitud", l_longitud_destino);
        map.put("distancia", distancia);
        map.put("precio", precio);

        JSONObject jobject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8")).toString();
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));


        String newURL = Constantes.TERMINAR_VIAJE + "?" + encodedParams;

        Log.d("viaje",newURL);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
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
    private void procesarRespuestaActualizar(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    actualizar_turno();

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

    private void actualizar_turno(){


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", id_turno);
        map.put("distancia", distancia);
        map.put("recaudacion", precio );

        JSONObject jobject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8")).toString();
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));


        String newURL = Constantes.UPDATE_TURNO + "?" + encodedParams;
        Log.d(TAG,newURL);
                // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarTurno(response);
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
    private void procesarRespuestaActualizarTurno(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    cargarDatosFinal( getContext());
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

    private void suspender_viaje(){

        String ls_viaje = id_viaje.getText().toString();

        String newURL = Constantes.SUSPENDER_VIAJE + "?id=" + ls_viaje;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarSuspender(response);
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
    private void procesarRespuestaActualizarSuspender(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Intent intent2 = new Intent(getContext(), MainViaje.class);
                    getContext().startActivity(intent2);
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

    private void alarma_viaje(){

        String ls_viaje = id_viaje.getText().toString();

        String newURL = Constantes.ALARMA_VIAJE + "?id=" + ls_viaje;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarAlarma(response);
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
    private void procesarRespuestaActualizarAlarma(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Intent intent2 = new Intent(getContext(), MainViaje.class);
                    getContext().startActivity(intent2);
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

    protected void printTicket() {
        btsocket = impresion.getbluetoothSocket();
        if(btsocket == null){
            getActivity().startService(new Intent(getActivity(), Impresion.class));
        }
        else{
            outputStream = impresion.getOutputStream();

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = { 0x1B, 0*21, FONT_TYPE };
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom (getResources().getString(R.string.empresa),2,1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printNewLine();
                printCustom (getResources().getString(R.string.telefono),0,1);

                printNewLine();
                printUnicode();
                printNewLine();
                printText(getResources().getString(R.string.recibo)); // total 32 char in a single line
                printNewLine();
                printText(fecha);//fecha
                printNewLine();
                printCustom ("Chofer: " + chofer,0,0);
                printNewLine();
                printNewLine();
                printCustom ("SALIDA  " + hora_inicio,0,0);
                printNewLine();
                printCustom ("DESDE  " + dato_salida.getText().toString(),0,0);
                printNewLine();
                printText("HASTA  " + destino.getText().toString());
                printNewLine();
                printCustom ("LLEGADA  " + hora_fin,0,0);
                printNewLine();
                printCustom ("RECORRIDO  " + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(distancia)),0,0);
                printNewLine();
                printNewLine();

                printCustom ("TARIFA AL  " + fecha_tarifa,0,0);
                printNewLine();
                printCustom ("VIAJE  " + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(precio)),0,0);
                printNewLine();
                printCustom ("ESPERA  ",0,0);
                printNewLine();
                printNewLine();

                printCustom ("TOTAL:  " + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(precio)),2,0);
                printNewLine();

                //resetPrint(); //reset printer
                printUnicode();
                printNewLine();
                printNewLine();

                outputStream.flush();
                Intent intent2 = new Intent(getContext(), MainViaje.class);
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
