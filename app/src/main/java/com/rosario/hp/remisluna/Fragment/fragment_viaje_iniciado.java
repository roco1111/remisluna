package com.rosario.hp.remisluna.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.parametro;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.ServicioGeolocalizacion;
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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class fragment_viaje_iniciado extends Fragment {
    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_viaje_iniciado.class.getSimpleName();
    private String ls_id_conductor;
    private TextView id_viaje;
    private TextView solicitante;
    private TextView documento;
    private TextView dato_salida;
    private TextView destino;
    private TextView kms;
    private TextView importe;
    private String hora_inicio;
    private String hora_fin;
    private String fecha_tarifa;
    private String fecha;
    private String distancia;
    private String chofer;
    private String ls_precio = "0.00";
    private String ls_ficha = "0.00";
    private String ls_espera = "0.00";
    private String id_vehiculo;
    private String id_turno;
    private String movil;
    private Button sin_ticket;
    private Button con_ticket;
    private Button suspender;
    private Button alarma;
    private String importe_bajada;
    private String importe_ficha;
    private String importe_espera;
    private String l_latitud_destino;
    private String l_longitud_destino;
    private Long espera= 0L;
    private Long ficha= 0L;
    private Long cuadras = 0L;
    private Double precio_total = 0.00;
    private Double precio_ficha = 0.00;
    private Double precio_espera = 0.00;
    private Integer id_trayecto = 0;
    private static OutputStream outputStream;
    private Impresion impresion;
    byte FONT_TYPE;
    boolean mBound = false;
    private LocationManager mLocationManager;
    boolean lb_ticket;
    private Double l_porcentaje;

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(onBroadcast);
        super.onPause();
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(onBroadcast, new IntentFilter("key"));
        super.onResume();
    }

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
        View v = inflater.inflate(R.layout.activity_viaje_iniciado, container, false);


        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        id_viaje = v.findViewById(R.id.dato_viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        documento = v.findViewById(R.id.dato_documento);
        dato_salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        sin_ticket = v.findViewById(R.id.buttonSinTicket);
        con_ticket = v.findViewById(R.id.buttonConTicket);
        suspender = v.findViewById(R.id.buttonSuspender);
        alarma = v.findViewById(R.id.buttonAlarma);
        kms = v.findViewById(R.id.kms);
        importe = v.findViewById(R.id.precio);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_conductor     = settings.getString("id","");
        id_turno            = settings.getString("id_turno_chofer","");

        this.con_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            lb_ticket = true;
            getActivity().stopService(new Intent(getActivity(), ServicioGeolocalizacion.class));
            cargarDatosVehiculo(getContext()); }

        });

        this.sin_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lb_ticket = false;
                getActivity().stopService(new Intent(getActivity(), ServicioGeolocalizacion.class));
                cargarDatosVehiculo(getContext()); }

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

    private BroadcastReceiver onBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context ctxt, Intent i) {

            //blanqueamos el texto de las coordenadas si esta el texto default
            if (kms.getText().equals(getString(R.string.esperando))) {
                kms.setText("");
            }

            String datos = i.getStringExtra("coordenadas");//obtenemos las coordenadas envidas del servicioGeolocalizaci—n
            String[] tokens = datos.split(";");//separamos por token
            cuadras++;
            kms.setText( String.valueOf(cuadras));

            Double valor_ficha ;

            if(tokens[2].equals("1")){
                ficha++;
                valor_ficha = Double.parseDouble(importe_ficha);
                precio_ficha = precio_ficha + valor_ficha ;
            }else{
                espera++;
                valor_ficha = Double.parseDouble(importe_espera);
                precio_espera = precio_espera + valor_ficha ;
            }


            precio_total = precio_total + valor_ficha ;

            precio_total = Double.parseDouble(getTwoDecimals(precio_total));

            importe.setText("$ " + String.format(Locale.GERMANY,"%.2f",precio_total));

            ls_precio = String.format(Locale.GERMANY,"%.2f",precio_total);

            String latitud;
            String longitud;

            latitud = tokens[0];
            longitud = tokens[0];

            guardar_trayectoria(latitud,longitud);

            //kms.append("\n");//agregamos salto de linea
        }
    };

    private static String getTwoDecimals(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }


    private void guardar_trayectoria(String latitud, String longitud){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        id_trayecto++;
        map.put("id_viaje", id_viaje.getText().toString());
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
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarUbicacion(response);
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
    private void procesarRespuestaActualizarUbicacion(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
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

    public void cargarParametro(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ID_PARAMETRO + "?parametro=10";
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
                                procesarRespuestaParametro(response, context);
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

    private void procesarRespuestaParametro(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");

                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_porcentaje = Double.parseDouble(object.getString("valor"));

                    }

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
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viaje");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    id_viaje.setText(object.getString("id"));
                    solicitante.setText(object.getString("solicitante"));
                    documento.setText(object.getString("nro_documento"));
                    dato_salida.setText(object.getString("salida"));
                    destino.setText(object.getString("destino"));
                    id_vehiculo = object.getString("id_movil");
                    importe_bajada = object.getString("importe_bajada");
                    importe_ficha = object.getString("importe_ficha");
                    importe_espera = object.getString("importe_espera");
                    movil = object.getString("movil");

                    precio_total = Double.parseDouble(importe_bajada);

                    cargarParametro(context);

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

                    if(lb_ticket) {

                        if (!mBound) {
                            Intent intent2 = new Intent(getContext(), MainActivity.class);
                            getContext().startActivity(intent2);
                        } else {

                            printTicket();
                        }
                    }else{
                        Intent intent2 = new Intent(getContext(), MainActivity.class);
                        getContext().startActivity(intent2);
                        }

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

        distancia = String.valueOf(cuadras / 10);

        Double descuento;
        Double total;

        descuento = Double.parseDouble(ls_precio) * (l_porcentaje / 100);
        total = Double.parseDouble(ls_precio) - descuento;

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_viaje);
        map.put("latitud", l_latitud_destino);
        map.put("longitud", l_longitud_destino);
        map.put("distancia", distancia);
        map.put("precio", ls_precio);
        map.put("espera", ls_espera);
        map.put("descuento", String.format(Locale.GERMANY,"%.2f",descuento));
        map.put("total", String.format(Locale.GERMANY,"%.2f",total));


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
        map.put("recaudacion", ls_precio );

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
            printText(fecha);//fecha
            printNewLine();
            printCustom ("Chofer: " + chofer,1,0);
            printCustom ("Nro Remis: " + movil,1,0);
            printNewLine();
            printText("SALIDA  " + hora_inicio);
            printNewLine();
            printText("DESDE  " + dato_salida.getText().toString());
            printNewLine();
            printText("HASTA  " + destino.getText().toString());
            printNewLine();
            printText("LLEGADA  " + hora_fin);
            printNewLine();
            printText("RECORRIDO  " + String.valueOf(cuadras));
            printNewLine();
            printNewLine();
            printText("TARIFA AL  " + fecha_tarifa);
            printNewLine();
            printText("VIAJE  " + '$' + String.format(Locale.GERMAN,"%.2f",precio_ficha));
            printNewLine();
            printText("ESPERA  "+ '$' + String.format(Locale.GERMAN,"%.2f",precio_espera));
            printNewLine();
            printNewLine();
            printCustom ("TOTAL:  " + '$' +ls_precio,2,0);
            printNewLine();
            printNewLine();
            outputStream.flush();
            Intent intent2 = new Intent(getContext(), MainViaje.class);
            getContext().startActivity(intent2);
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
