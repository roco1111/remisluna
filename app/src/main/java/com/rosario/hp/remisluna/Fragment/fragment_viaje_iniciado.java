package com.rosario.hp.remisluna.Fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.android.volley.misc.AsyncTask;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.ServicioGeolocalizacion;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class fragment_viaje_iniciado extends Fragment {
    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_viaje_iniciado.class.getSimpleName();
    private String ls_id_conductor;
    private TextView id_viaje;
    private TextView solicitante;
    private TextView dato_salida;
    private TextView destino;
    private TextView kms;
    private TextView importe;
    private TextView texto_tarifa;
    private TextView tiempo_viaje;
    private TextView ficha_espera;
    private String hora_inicio;
    private String hora_fin;
    private String fecha_tarifa;
    private String fecha;
    private String distancia;
    private String chofer;
    private String ls_precio = "0.00";
    private String ls_ficha = "0.00";
    private String ls_espera = "0.00";
    private String ls_bajada = "0.00";
    private String id_vehiculo;
    private String id_turno;
    private String movil;
    private Button sin_ticket;

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
    private Double precio_bajada= 0.00;
    private Integer id_trayecto = 0;
    boolean lb_ticket;
    private Double l_porcentaje;
    boolean cronometroActivo;
    private Calendar c;
    private String l_hora_desde;
    private String l_hora_hasta;
    private String getMyTime_desde;
    private String getMyTime_hasta;
    private Long tiempo_acumulado = 0L;
    private Long tiempo_tolerancia = 0L;
    private Integer tipo_espera;
    private SimpleDateFormat sdf;
    private boolean lb_viaje_terminado = false;
    private Double ldb_porcentaje_titular;
    private String ls_remiseria;
    private String ls_es_feriado;
    private Activity act;
    private Context context;
    private Long l_tolerancia_tope;
    private Boolean lb_torerancia;
    private String salida_coordenada;
    private Iniciar_servicio_tiempo Iniciar_servicio_tiempo;
    private String l_nro_recibo;
    private String l_recibo_parametro;

    private Button boton_whatsapp;
    private String telefono_base;

    @Override
    public void onPause() {
        if(isMyServiceRunning(ServicioGeolocalizacion.class)) {
            act.unregisterReceiver(onBroadcast);
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        act.registerReceiver(onBroadcast, new IntentFilter("key"));
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_viaje_iniciado, container, false);

        act = getActivity();

        context = getContext();
        //((MainActivity) getActivity()).locationEnd();

        id_viaje = v.findViewById(R.id.dato_viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        dato_salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        sin_ticket = v.findViewById(R.id.buttonSinTicket);
        boton_whatsapp = v.findViewById(R.id.imageWa);

        suspender = v.findViewById(R.id.buttonSuspender);
        alarma = v.findViewById(R.id.buttonAlarma);
        kms = v.findViewById(R.id.kms);
        importe = v.findViewById(R.id.precio);
        texto_tarifa = v.findViewById(R.id.tarifa);
        tiempo_viaje = v.findViewById(R.id.tiempo);
        ficha_espera = v.findViewById(R.id.ficha_espera);
        sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        lb_viaje_terminado = false;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        ls_id_conductor     = settings.getString("id","");
        id_turno            = settings.getString("id_turno_chofer","");
        ls_remiseria     = settings.getString("remiseria","");
        l_porcentaje = getValor(settings.getString("porcentaje","0"));
        l_hora_desde = settings.getString("tarifa_desde","");
        l_hora_hasta= settings.getString("tarifa_hasta","");
        ls_es_feriado = settings.getString("feriado","");
        l_tolerancia_tope = settings.getLong("tolerancia_tope",0L);


        String l_hoy;

        c = Calendar.getInstance();
        SimpleDateFormat shoy = new SimpleDateFormat("MM/dd/yyyy");
        l_hoy = shoy.format(c.getTime());
        getMyTime_desde = l_hoy + ' ' + l_hora_desde;
        getMyTime_hasta = l_hoy + ' ' + l_hora_hasta;

        MediaPlayer mediaPlayer = MediaPlayer.create(act, R.raw.doorbell);

        this.sin_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lb_ticket = false;
                lb_viaje_terminado = true;
                mediaPlayer.start();
                if(isMyServiceRunning(ServicioGeolocalizacion.class)) {
                    act.unregisterReceiver(onBroadcast);
                    act.stopService(new Intent(act, ServicioGeolocalizacion.class));
                    //act.stopService(new Intent(act, ServicioGeolocalizacion_metros.class));
                    Log.d("Servicio","Servicio detenido");
                }

                cargarDatosVehiculo(context); }

        });

        this.suspender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                suspender_viaje(context);
            }
        });

        this.alarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                alarma_viaje(context);
            }
        });

        this.boton_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.start();
                cargarDatosRemiseria(context, v);

            }
        });
        cargarDatos(context);

        return v;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver onBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context ctxt, Intent i) {
            Log.d("Servicio","Servicio recibido");
            if(!lb_viaje_terminado) {
                String datos = i.getStringExtra("coordenadas");//obtenemos las coordenadas envidas del servicioGeolocalizaci—n
                String[] tokens = datos.split(";");//separamos por token

                cargarTarifa(ctxt, tokens);
            }else{
                Log.d("Servicio","Servicio detenido");
            }

        }
    };


    private static String getTwoDecimals(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    public void cargarDatosRemiseria(final Context context, final View v) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_REMISERIA + "?remiseria=" + ls_remiseria;
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
                                procesarRespuesta_remiseria(response, context, v);
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

    private void procesarRespuesta_remiseria(JSONObject response, Context context, View v) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("remiseria");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    telefono_base = object.getString("TELEFONO_BASE");

                    setClickToChat(v,telefono_base);

                    break;

                case "2":

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setClickToChat(View v,String toNumber){
        String url = "https://api.whatsapp.com/send?phone=" + toNumber;
        try {
            PackageManager pm = v.getContext().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            v.getContext().startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    public void cargarTarifa(final Context context, final String[] tokens) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TARIFAS + "?id_remiseria=" + ls_remiseria;
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
                                if(!lb_viaje_terminado) {
                                    procesarRespuestaTarifa(response, context, tokens);
                                }
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

    private void procesarRespuestaTarifa(JSONObject response, Context context, String[] tokens) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("tarifa");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    String l_nocturno;

                    String getCurrentDateTime = sdf.format(c.getTime());

                    if (getCurrentDateTime.compareTo(getMyTime_desde) > 0)
                    { l_nocturno = "1"; } else
                    {
                        if (getCurrentDateTime.compareTo(getMyTime_hasta) < 0)
                        {
                            l_nocturno = "1";
                        }else{
                            int dia_semana;
                            dia_semana=c.get(Calendar.DAY_OF_WEEK);

                            if(dia_semana == Calendar.SUNDAY){
                                l_nocturno = "1";
                            }else{
                                if(ls_es_feriado.equals("si")){
                                    l_nocturno = "1";
                                }else{
                                    l_nocturno = "0";
                                }

                            }
                        }
                    }

                    if(l_nocturno.equals("0")){
                        texto_tarifa.setText(R.string.diurno);
                    }else{
                        texto_tarifa.setText(R.string.nocturno);
                    }

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("nocturno",l_nocturno);
                    editor.apply();
                    if(l_nocturno.equals("0")) {
                        importe_bajada = object.getString("importe_bajada");
                        importe_ficha = object.getString("importe_ficha");
                        importe_espera = object.getString("importe_espera");
                        texto_tarifa.setText(R.string.diurno);
                    }else{
                        importe_bajada = object.getString("importe_bajada_nocturno");
                        importe_ficha = object.getString("importe_ficha_nocturno");
                        importe_espera = object.getString("importe_espera_nocturno");
                        texto_tarifa.setText(R.string.nocturno);
                    }

                    Double valor_ficha = 0.00 ;
                    String ls_tiempo;

                    switch (tokens[2]) {
                        case "1"://ficha
                            ficha++;
                            valor_ficha = Double.parseDouble(importe_ficha);
                            precio_ficha = precio_ficha + valor_ficha ;
                            precio_ficha = getValor(getTwoDecimals(precio_ficha));
                            cuadras++;
                            kms.setText( String.valueOf(cuadras));
                            String ls_es_tolerancia;
                            ls_es_tolerancia = tokens[3];
                            if(ls_es_tolerancia.equals("NO")) {
                                tiempo_viaje.setText("00:00");
                            }else{
                                ls_tiempo = tokens[4];
                                tiempo_tolerancia = Long.parseLong(tokens[5]);
                                tiempo_viaje.setText( ls_tiempo);
                                tiempo_viaje.setTextColor(act.getResources().getColor(R.color.suspender));
                            }
                            break;
                        case "2"://espera
                            espera++;
                            valor_ficha = Double.parseDouble(importe_espera);
                            precio_espera = precio_espera + valor_ficha ;
                            precio_espera = getValor(getTwoDecimals(precio_espera));
                            ficha_espera.setText( String.valueOf(precio_espera));
                            tiempo_viaje.setText( "00:00");
                            break;

                        case "3"://reloj en tolerancia

                            ls_tiempo = tokens[3];
                            tiempo_tolerancia = Long.parseLong(tokens[4]);
                            tiempo_viaje.setText( ls_tiempo);
                            tiempo_viaje.setTextColor(act.getResources().getColor(R.color.suspender));
                            valor_ficha = 0.00;
                            break;
                        case "4"://reloj en espera

                            ls_tiempo = tokens[3];
                            tiempo_acumulado = Long.parseLong(tokens[4]);
                            tiempo_viaje.setText( ls_tiempo);
                            valor_ficha = 0.00;
                            tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                            break;
                        case "5"://termino tolerancia
                            tiempo_viaje.setText( "00:00");
                            if (l_tolerancia_tope == 0L){
                                valor_ficha = 0.00;
                            }else {
                                valor_ficha = Double.parseDouble(importe_espera);
                                precio_espera = precio_espera + valor_ficha;
                                precio_espera = getValor(getTwoDecimals(precio_espera));
                                ficha_espera.setText( String.valueOf(precio_espera));
                            }

                            tipo_espera = 1;
                            tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                            break;
                    }

                    if(valor_ficha > 0.00){
                        precio_total = precio_total + valor_ficha ;

                        precio_total = getValor(getTwoDecimals(precio_total));

                        importe.setText(String.valueOf(precio_total));

                        ls_precio = String.format(Locale.GERMANY,"%.2f",precio_total);
                    }

                    String latitud;
                    String longitud;

                    latitud = tokens[0];
                    longitud = tokens[1];
                    if(!lb_viaje_terminado) {
                        guardar_trayectoria(latitud, longitud, context);
                    }

                    break;

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void guardar_trayectoria(final String latitud, final String longitud, final Context context){

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
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(!lb_viaje_terminado) {
                                    procesarRespuestaActualizarUbicacion(response, latitud, longitud, context);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error trayectoria: " + error.getMessage());
                                actualizar_viaje(latitud, longitud, context);

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
    private void procesarRespuestaActualizarUbicacion(JSONObject response, String latitud, String longitud, final Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    if(lb_viaje_terminado) {


                        if (isMyServiceRunning(ServicioGeolocalizacion.class)) {
                            act.unregisterReceiver(onBroadcast);
                            act.stopService(new Intent(act, ServicioGeolocalizacion.class));

                            //act.stopService(new Intent(act, ServicioGeolocalizacion_metros.class));
                            Log.d("Servicio", "Servicio detenido 2");

                        }
                        cargarDatosVehiculo(context);
                    }else {
                        actualizar_viaje(latitud, longitud, context);
                    }

                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    actualizar_viaje(latitud, longitud, context);
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizar_viaje(final String latitud, final String longitud, final Context context){

        String ls_viaje = id_viaje.getText().toString();

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_viaje);
        map.put("tiempo", tiempo_viaje.getText().toString());
        map.put("fichas", String.valueOf(ficha));
        map.put("fichas_espera", String.valueOf(espera));
        map.put("importe_espera", String.valueOf(precio_espera));
        map.put("total", String.valueOf(precio_total));
        map.put("tiempo_tolerancia", String.valueOf(tiempo_tolerancia));
        map.put("tiempo_acumulado", String.valueOf(tiempo_acumulado));
        map.put("bajada", String.valueOf(importe_bajada));
        map.put("trayectoria", String.valueOf(id_trayecto));
        map.put("tipo_espera", String.valueOf(tipo_espera));
        map.put("importe_fichas", String.valueOf(precio_ficha));


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


        String newURL = Constantes.ACTUALIZAR_VIAJE + "?" + encodedParams;

        Log.d("act viaje",newURL);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarViaje(response, latitud, longitud, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error act viaje: " + error.getMessage());

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
    private void procesarRespuestaActualizarViaje(JSONObject response, String latitud, String longitud, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    String Text = "Lat = "+ latitud + "\n Long = " + longitud;
                    Log.d("ubicación_iniciado",Text);
                    guardar_ubicacion(latitud, longitud, context);
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
                    dato_salida.setText(object.getString("salida"));
                    destino.setText(object.getString("destino"));
                    id_vehiculo = object.getString("id_movil");
                    ldb_porcentaje_titular = Double.parseDouble(object.getString("porc_titular"));
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    String l_nocturno;
                    l_nocturno     = settings.getString("nocturno","");
                    if(l_nocturno.equals("0")) {
                        if(object.getString("bajada").equals("null") || object.getString("bajada").equals("0.000")) {
                            importe_bajada = object.getString("importe_bajada");
                        }else{
                            importe_bajada =object.getString("bajada");
                        }
                        importe_ficha = object.getString("importe_ficha");
                        importe_espera = object.getString("importe_espera");
                        texto_tarifa.setText(R.string.diurno);
                    }else{
                        if(object.getString("bajada").equals("null")|| object.getString("bajada").equals("0.000")) {
                            importe_bajada = object.getString("importe_bajada_nocturno");
                        }else{
                            importe_bajada =object.getString("bajada");
                        }
                        importe_ficha = object.getString("importe_ficha_nocturno");
                        importe_espera = object.getString("importe_espera_nocturno");
                        texto_tarifa.setText(R.string.nocturno);
                    }
                    movil = object.getString("movil");
                    ls_bajada = importe_bajada;
                    precio_bajada = Double.parseDouble(importe_bajada);
                    if(object.getString("bajada").equals("null") || object.getString("bajada").equals("0.000")) {
                        precio_total = Double.parseDouble(importe_bajada);
                        id_trayecto = 0;

                    }else{
                        precio_total = Double.parseDouble(object.getString("total"));
                        tiempo_viaje.setText(object.getString("tiempo"));
                        kms.setText(object.getString("fichas"));
                        ficha = Long.parseLong(object.getString("fichas"));
                        ficha_espera.setText(object.getString("importe_espera_viaje"));
                        espera = Long.parseLong(object.getString("fichas_espera"));
                        id_trayecto = Integer.parseInt(object.getString("trayectoria"));
                        tipo_espera = Integer.parseInt(object.getString("tipo_espera"));
                        precio_ficha = Double.parseDouble(object.getString("importe_fichas"));
                        precio_espera = Double.parseDouble(object.getString("importe_espera_viaje"));
                        if(tipo_espera == 0){
                            tiempo_viaje.setTextColor(act.getResources().getColor(R.color.suspender));
                        }else{
                            tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                        }

                    }
                    importe.setText(String.valueOf(precio_total));
                    ls_precio = String.format(Locale.GERMANY,"%.2f",precio_total);
                    cronometroActivo = true;

                    salida_coordenada = object.getString("salida_coordenadas");

                    if(!object.getString("tiempo_tolerancia").equals("null")) {

                        tiempo_tolerancia = Long.parseLong(object.getString("tiempo_tolerancia"));

                    }

                    if(!object.getString("tiempo_acumulado").equals("null")) {

                        tiempo_acumulado = Long.parseLong(object.getString("tiempo_acumulado"));

                    }

                    if(object.getString("tipo_espera").equals("0")){
                        lb_torerancia = true;
                    }else{
                        lb_torerancia = false;
                    }

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("boolean_tolerancia",lb_torerancia);
                    editor.putLong("tiempo_acumulado",tiempo_acumulado);
                    editor.putLong("tiempo_tolerancia",tiempo_tolerancia);
                    editor.putString("salida_coordenada",salida_coordenada);

                    editor.apply();

                    Log.d("inicio carga", "listo");

                    Iniciar_servicio_tiempo = new Iniciar_servicio_tiempo();
                    Iniciar_servicio_tiempo.execute();


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

    private class Iniciar_servicio_tiempo extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... params ) {

            act.startService(new Intent(act,ServicioGeolocalizacion.class));
            //Iniciar_servicio_metros = new Iniciar_servicio_metros();
            //Iniciar_servicio_metros.execute();
            return 0;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {


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

                    cargarViaje_solicitado(context);

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

    public void cargarViaje_solicitado(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_SOLICITADOS + "?conductor=" + ls_id_conductor;
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
                                procesarRespuesta_solicitados(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley viaje solicitado: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta_solicitados(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            Fragment fragment = null;
            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("viaje");
                    JSONObject object = mensaje1.getJSONObject(0);


                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    String ls_viaje;

                    ls_viaje = object.getString("id");

                    editor.putString("id_viaje",ls_viaje);
                    editor.apply();

                    editor.apply();
                    Intent intent2 = new Intent(context, MainViaje.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity(intent2);

                    break;

                case "2":

                    Intent intent3 = new Intent(context, MainActivity.class);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity(intent3);
                    act.finish();
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

                    obtenerRecibo(context);

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

    public void obtenerRecibo(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "8");
        map.put("remiseria", ls_remiseria);

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

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ID_PARAMETRO + "?" + encodedParams;
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
                                Log.d(TAG, "Error Volley parámetro: " + error.getMessage());

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

                        l_nro_recibo = object.getString("valor");

                    }
                    Integer l_recibo;

                    l_recibo = Integer.parseInt(l_nro_recibo) + 1;

                    l_recibo_parametro = String.valueOf(l_recibo);

                    l_nro_recibo = "00000000" + l_recibo_parametro;

                    l_nro_recibo = l_nro_recibo.substring(l_nro_recibo.length() - 8);

                    actualizar_recibo(context);

                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            context,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;


            }

            // run_espera();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void actualizar_recibo(final Context context){


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "8");
        map.put("remiseria", ls_remiseria);
        map.put("valor", l_recibo_parametro);

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


        String newURL = Constantes.UPDATE_PARAMETRO + "?" + encodedParams;
        Log.d(TAG,newURL);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarRecibo(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Turno: " + error.getMessage());

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
    private void procesarRespuestaActualizarRecibo(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    terminar_viaje( context);
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


    private void terminar_viaje(final Context context){

        String ls_viaje = id_viaje.getText().toString();

        distancia = String.valueOf(cuadras / 10);

        Double descuento, descuento_titular;
        Double total, subtotal;

        descuento = precio_total * (l_porcentaje / 100);//porcentaje remiseria
        subtotal = precio_total - descuento;

        descuento_titular = subtotal * (ldb_porcentaje_titular / 100);//porcentaje titular
        total = subtotal - descuento_titular;

        Double precio_km;

        if(!distancia.equals("0")) {
            precio_km = precio_total / Double.parseDouble(distancia);
        }else{
            precio_km = precio_total;
        }


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_viaje);
        map.put("latitud", l_latitud_destino);
        map.put("longitud", l_longitud_destino);
        map.put("distancia", distancia);
        map.put("precio", String.valueOf(precio_total));
        map.put("importe_espera", String.valueOf(precio_espera));
        map.put("descuento", String.valueOf(descuento));
        map.put("total", String.valueOf(total));
        map.put("bajada", String.valueOf(importe_bajada));
        map.put("tiempo_tolerancia", String.valueOf(tiempo_tolerancia));
        map.put("tiempo_acumulado", String.valueOf(tiempo_acumulado));
        map.put("tiempo", tiempo_viaje.getText().toString());
        map.put("fichas", String.valueOf(ficha));
        map.put("fichas_espera", String.valueOf(espera));
        map.put("importe_fichas", String.valueOf(precio_ficha));
        map.put("nro_recibo", l_nro_recibo);
        map.put("importe_titular", String.valueOf(descuento_titular));
        map.put("precio_km", String.valueOf(precio_km));




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
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizar(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error terminar: " + error.getMessage());

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
    private void procesarRespuestaActualizar(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    actualizar_turno(context);

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

    private void actualizar_turno(final Context context){


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
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarTurno(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Turno: " + error.getMessage());

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
    private void procesarRespuestaActualizarTurno(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    cargarDatosFinal( context);
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

    private void suspender_viaje(final Context context){

        String ls_viaje = id_viaje.getText().toString();

        String newURL = Constantes.SUSPENDER_VIAJE + "?id=" + ls_viaje;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarSuspender(response, context);
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
    private void procesarRespuestaActualizarSuspender(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Intent intent2 = new Intent(getContext(), MainViaje.class);
                    context.startActivity(intent2);
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

    private void alarma_viaje(final Context context){

        String ls_viaje = id_viaje.getText().toString();

        String newURL = Constantes.ALARMA_VIAJE + "?id=" + ls_viaje;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaActualizarAlarma(response, context);
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
    private void procesarRespuestaActualizarAlarma(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Intent intent2 = new Intent(context, MainViaje.class);
                    context.startActivity(intent2);
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


    private void guardar_ubicacion(String latitud, String longitud,final Context context){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("latitud", latitud);
        map.put("longitud", longitud);
        map.put("id", id_vehiculo);


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
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
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
    private void procesarRespuestaActualizarPosicion(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
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

    public double getValor(String texto){
        if(texto == null){
            return 0.00;
        }else {
            if (texto.contains(",")) {
                return Double.parseDouble(texto.replace(",", ".").trim());
            }
        }
        return Double.parseDouble(texto.trim());
    }


}
