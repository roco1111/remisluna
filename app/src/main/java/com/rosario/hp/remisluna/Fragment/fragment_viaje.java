package com.rosario.hp.remisluna.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.parada;
import com.rosario.hp.remisluna.Entidades.turno;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.activity_preferencias;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.turnos_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class fragment_viaje extends Fragment {
    private JsonObjectRequest myRequest;
    private static final String TAG = login.class.getSimpleName();
    private String ls_id_conductor;
    private TextView solicitante;
    private TextView dato_salida;
    private TextView destino;
    private TextView texto_tarifa;
    private String id_viaje;

    private ImageButton boton_cero;
    private ImageButton boton_uno;
    private ImageButton boton_dos;
    private ImageButton boton_tres;
    private ImageButton boton_cuatro;
    private ImageButton boton_cinco;
    private ImageButton boton_seis;
    private ImageButton boton_siete;
    private ImageButton boton_ocho;
    private ImageButton boton_nueve;


    private String recaudacion;
    private String kms;
    private String fecha;
    private String hora_inicio;
    private String estado;
    private String ls_id_turno;
    private String fecha_ultimo;
    private String salida_ultimo;
    private String destino_ultimo;
    private String hora_salida_ultimo;
    private String hora_destino_ultimo;
    private String importe_ultimo;
    private String espera_ultimo;
    private String total_ultimo;
    private String chofer_ultimo ;
    private String distancia_ultimo ;
    private String fecha_tarifa_ultimo ;
    private String movil_ultimo ;
    private String fichas_ultimo;
    private String bajada_ultimo;

    private String id_movil;

    private ArrayList<viaje> viajes = new ArrayList<>();
    private ArrayList<turno> datos;

    private String latitud_salida;
    private String longitud_salida;
    private String latitud_destino;
    private String longitud_destino;
    private String distancia;
    private String salida_coordenada;
    private String destino_coordenada;

    private String l_hora_desde;
    private String l_hora_hasta;
    private String l_hoy;
    private String l_nocturno;
    private Calendar c;

    private Button inicio;
    private Button anular;
    private LocationManager mLocationManager;
    boolean mBound = false;
    private Impresion impresion;
    private static OutputStream outputStream;
    byte FONT_TYPE;
    private TextView impresora;
    private String ls_remiseria;
    private TextView gps;
    private String ls_es_feriado;
    private String chapa;
    private String patente;
    private Activity act;
    private Context context;
    private String l_porcentaje;
    ProgressDialog progress1;

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mBound) {
            act.unbindService(connection);

            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkIfLocationOpened()){
            gps.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            gps.setTextColor(getResources().getColor(R.color.alarma));
        }
        cargarImpresora(getContext());
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Impresion.LocalBinder binder = (Impresion.LocalBinder) service;
            impresion = binder.getService();
            if(impresion.getBluetoothAdapter() !=null) {
                if (impresion.getOutputStream() != null) {
                    impresora.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    mBound = true;
                } else {
                    impresora.setTextColor(context.getResources().getColor(R.color.alarma));
                    mBound = false;

                }
            }else{
                impresora.setTextColor(context.getResources().getColor(R.color.alarma));
                mBound = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            impresora.setTextColor(getResources().getColor(R.color.alarma));
            mBound = false;
        }
        @Override
        public void onBindingDied (ComponentName arg0) {
            impresora.setTextColor(getResources().getColor(R.color.alarma));
            mBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_viaje, container, false);
        act = getActivity();
        mLocationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

        solicitante = v.findViewById(R.id.dato_solicitante);
        dato_salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        inicio = v.findViewById(R.id.buttonInicio);
        anular = v.findViewById(R.id.buttonAnular);
        texto_tarifa = v.findViewById(R.id.tarifa);
        this.boton_cero = v.findViewById(R.id.imageButtonCero);
        this.boton_uno = v.findViewById(R.id.imageButtonUno);
        this.boton_dos = v.findViewById(R.id.imageButtonDos);
        this.boton_tres = v.findViewById(R.id.imageButtonTres);
        this.boton_cuatro = v.findViewById(R.id.imageButtonCuatro);
        this.boton_cinco = v.findViewById(R.id.imageButtonCinco);
        this.boton_seis = v.findViewById(R.id.imageButtonSeis);
        this.boton_siete = v.findViewById(R.id.imageButtonSiete);
        this.boton_ocho = v.findViewById(R.id.imageButtonOcho);
        this.boton_nueve = v.findViewById(R.id.imageButtonNueve);
        datos = new ArrayList<>();

        context = getContext();
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.everblue);

        this.impresora = v.findViewById(R.id.impresora);
        if(mBound) {
            impresora.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            impresora.setTextColor(getResources().getColor(R.color.alarma));
        }

        this.gps = v.findViewById(R.id.gps);
        if(checkIfLocationOpened()){
            gps.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            gps.setTextColor(getResources().getColor(R.color.alarma));
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_conductor     = settings.getString("id","");
        ls_id_turno     = settings.getString("id_turno_chofer","");
        ls_remiseria     = settings.getString("remiseria","");

        this.inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if(verificar_internet()) {
                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(
                                context,
                                R.string.no_gps,
                                Toast.LENGTH_LONG).show();

                    }else{

                        iniciar_viaje(context);

                    }
                }else{
                    Toast.makeText(
                            context,
                            R.string.no_internet,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        this.anular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                anular_viaje(context);

            }
        });

        this.gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                 habilitar_gps();

            }
        });



        this.boton_cero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if(mBound) {
                    impresion_cero();
                }else{
                    Toast.makeText(
                            context,
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        this.boton_uno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if (mBound) {
                    datos_turno(context);
                }else{
                    Toast.makeText(
                            context,
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        this.boton_dos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cerrar_turno(context);

            }
        });

        this.boton_tres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if(mBound) {
                    cargarDatosRecaudacion(context);
                }else{
                    Toast.makeText(
                            context,
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        this.boton_cuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if(mBound) {
                    datos_ultimos_viajes(context);
                }else{
                    Toast.makeText(
                            context,
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        this.boton_cinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("tipo_ventana","main");
                editor.apply();
                if(mBound) {
                    requireActivity().unbindService(connection);
                    impresora.setTextColor(getResources().getColor(R.color.alarma));
                    mBound = false;
                }
                if(!mBound) {

                    Intent intent2 = new Intent(context, MainActivity.class);
                    context.startActivity(intent2);
                    getActivity().finish();

                }
            }
        });


        this.boton_seis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                if(mBound) {
                    repetirTicket(context);
                }else{
                    Toast.makeText(
                            context,
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        this.boton_siete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent2 = new Intent(context, activity_preferencias.class);
                context.startActivity(intent2);
            }
        });

        this.boton_ocho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent2 = new Intent(context, turnos_activity.class);
                context.startActivity(intent2);
            }
        });

        this.boton_nueve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent2 = new Intent(context, MainViaje.class);
                context.startActivity(intent2);
            }
        });
        progress1 = ProgressDialog.show(context, "Recuperando Datos", "Por favor, espere..", true);
        feriado(context);
        return v;
    }

    private void habilitar_gps(){
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
    }

    public void feriado(final Context context){
        String newURL = Constantes.GET_FERIADO;
        Log.d(TAG,newURL);

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(context).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar respuesta Json

                                procesarRespuestaFeriado(response, context);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley feriado: " + error.getMessage());
                                progress1.dismiss();

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaFeriado(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            ls_es_feriado= response.getString("feriado");

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("feriado",ls_es_feriado);
            editor.apply();

            cargarParametro(context);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("Provider contains=> " + provider);
        if (provider.contains("gps") || provider.contains("network")){
            return true;
        }
        return false;
    }

    public void cargarParametro(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "10");
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
                                progress1.dismiss();

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

                        l_porcentaje = object.getString("valor");

                    }
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("porcentaje",l_porcentaje);
                    editor.apply();

                    cargarParametroTarifaDesde(context);

                    break;
                case "2":
                    progress1.dismiss();
                    break;


            }

            // run_espera();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarParametroTarifaDesde(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "11");
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
                                procesarRespuestaParametroDesde(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley tarifa desde: " + error.getMessage());
                                progress1.dismiss();

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaParametroDesde(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");

                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_hora_desde = object.getString("valor");

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("tarifa_desde",l_hora_desde);
                        editor.apply();

                        cargarParametroTarifaHasta(context);

                    }

                    break;
                case "2":
                    progress1.dismiss();
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarParametroTarifaHasta(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "12");
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
                                procesarRespuestaParametroHasta(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley tarifa hasta: " + error.getMessage());
                                progress1.dismiss();

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaParametroHasta(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");

                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_hora_hasta = object.getString("valor");

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("tarifa_hasta",l_hora_hasta);

                        c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        String getCurrentDateTime = sdf.format(c.getTime());
                        SimpleDateFormat shoy = new SimpleDateFormat("MM/dd/yyyy");
                        l_hoy = shoy.format(c.getTime());
                        String getMyTime = l_hoy + ' ' + l_hora_desde;

                        if (getCurrentDateTime.compareTo(getMyTime) > 0)
                        { l_nocturno = "1"; } else
                        {
                            getMyTime = l_hoy + ' ' + l_hora_hasta;
                            if (getCurrentDateTime.compareTo(getMyTime) < 0)
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

                        editor.putString("nocturno",l_nocturno);
                        editor.apply();


                        cargarDatos(context);

                    }

                    break;
                case "2":
                    progress1.dismiss();
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarDatosRecaudacion( final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TURNOS + "?conductor=" + ls_id_conductor;
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
                                procesarRespuestaRecaudacion(response, context);
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

    private void procesarRespuestaRecaudacion(JSONObject response, Context context) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");

            switch (estado) {
                case "1": // EXITO

                    JSONArray mensaje = response.getJSONArray("turno");

                    datos.clear();

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = mensaje.getJSONObject(i);
                        com.rosario.hp.remisluna.Entidades.turno tur = new turno();

                        String id = object.getString("ID");

                        tur.setId(id);

                        String fecha = object.getString("FECHA");

                        tur.setFecha(fecha);

                        String hora_inicio = object.getString("HORA_INICIO");

                        tur.setHora_inicio(hora_inicio);

                        String hora_fin = object.getString("HORA_FIN");

                        tur.setHora_fin(hora_fin);

                        String recaudacion = object.getString("RECAUDACION");

                        tur.setRecaudacion(recaudacion);

                        datos.add(tur);

                    }
                    if (mBound) {
                        ticket_recaudacion(datos);
                    }
                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    protected void ticket_recaudacion( ArrayList<turno> turnos) {
        outputStream = impresion.getOutputStream();
        if(outputStream == null){
            Toast.makeText(context, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
        }else {

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printCustom(getResources().getString(R.string.telefono), 1, 1);

                printNewLine();
                printText(stringABytes(getResources().getString(R.string.ticket_recaudacion))); // total 32 char in a single line

                printNewLine();

                String id;
                String fecha;
                String hora_inicio;
                String hora_fin;
                String importe;

                for (turno Turno : turnos) {
                    printNewLine();
                    id = Turno.getId();
                    fecha = Turno.getFecha();
                    hora_inicio = Turno.getHora_inicio();
                    hora_fin = Turno.getHora_fin();
                    printCustom("TURNO " + id, 1, 0);
                    printCustom("Fecha " + fecha, 1, 0);
                    printCustom("Hora Inicio " + hora_inicio, 1, 0);
                    if (!hora_fin.equals("null")) {
                        printCustom("Hora Fin " + hora_fin, 1, 0);
                    }
                    importe = Turno.getRecaudacion();
                    printText("TOTAL:  " + importe);
                    printNewLine();
                }

                printNewLine();
                printNewLine();
                //resetPrint(); //reset printer
                printUnicode();
                printNewLine();
                printNewLine();

                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Boolean verificar_internet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            return true;
        } else {
            // No hay conexión a Internet en este momento
            return false;
        }
    }

    private void cerrar_turno(final  Context context){

        String newURL = Constantes.FIN_TURNO + "?id=" + ls_id_turno;
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
                                procesarRespuestaCerrarTurno(response, context);
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
    private void procesarRespuestaCerrarTurno(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Toast.makeText(
                    context,
                    "Turno Finalizado",
                    Toast.LENGTH_LONG).show();
                    if(mBound) {
                        datos_turno(context);
                    }else{
                        iniciar_turno(context);
                    }
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

    protected void impresion_cero() {

        outputStream = impresion.getOutputStream();
        if(outputStream == null){
            Toast.makeText(context, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
        }else {

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();

                String fecha_hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                printCustom(fecha_hoy, 0, 1);
                printNewLine();
                printUnicode();
                printText(getResources().getString(R.string.menu_reportes)); // total 32 char in a single line

                printNewLine();
                printUnicode();
                printNewLine();

                printCustom(getResources().getString(R.string.reporte_ayuda), 0, 0);
                printNewLine();
                printText(getResources().getString(R.string.reporte_parcial));
                printNewLine();
                printText(getResources().getString(R.string.reporte_turno));
                printNewLine();
                printText(stringABytes(getResources().getString(R.string.reporte_ultimos)));
                printNewLine();
                printText(getResources().getString(R.string.reporte_resumen));
                printNewLine();
                printText(getResources().getString(R.string.reporte_impresora));
                printNewLine();
                printText(stringABytes(getResources().getString(R.string.reporte_ticket)));
                printNewLine();
                printText(getResources().getString(R.string.reporte_perfil));
                printNewLine();
                printText(getResources().getString(R.string.reporte_viajes));
                printNewLine();
                printText(getResources().getString(R.string.reporte_viaje));

                printNewLine();
                printNewLine();
                //resetPrint(); //reset printer
                printUnicode();
                printNewLine();
                printNewLine();

                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void cargarDatos(final Context context) {

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
                                procesarRespuesta(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley carga datos: " + error.getMessage());
                                progress1.dismiss();

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
                    id_viaje = object.getString("id");
                    solicitante.setText(object.getString("solicitante"));
                    dato_salida.setText(object.getString("salida"));
                    destino.setText(object.getString("destino"));
                    salida_coordenada = object.getString("salida_coordenadas");
                    destino_coordenada = object.getString("destino_coordenadas");
                    id_movil = object.getString("id_movil");
                    progress1.dismiss();
                    actualizar_coordenadas(context);
                    break;

                case "2":
                    progress1.dismiss();
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                                procesarRespuestaTurno(response, context);
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

    private void procesarRespuestaTurno(JSONObject response, Context context) {

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
                    estado = object.getString("estado");
                    datos_viajes_turno(context);
                case "2":
                    Toast.makeText(
                            context,
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
            viajes.clear();
            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viajes");



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

                    break;

            }
            if (mBound) {
                if(estado.equals("1")){
                    ticket_turno_parcial();
                }else{
                    ticket_turno(viajes);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void ticket_turno_parcial( ) {

        outputStream = impresion.getOutputStream();
        if(outputStream == null){
            Toast.makeText(context, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
        }else {

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printCustom(getResources().getString(R.string.parcial_turno), 1, 0); // total 32 char in a single line

                printNewLine();
                printText(fecha);
                printText(" - ");
                printText(hora_inicio);//fecha
                printNewLine();

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

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    protected void ticket_turno( ArrayList<viaje> viajes) {

        outputStream = impresion.getOutputStream();
        if(outputStream == null){
            Toast.makeText(context, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
        }else {

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printCustom(getResources().getString(R.string.telefono), 1, 1);

                printNewLine();
                printUnicode();
                printNewLine();
                printText(getResources().getString(R.string.ticket_turno)); // total 32 char in a single line

                printNewLine();
                printText(fecha);//fecha
                printText(" - ");
                printText(hora_inicio);//fecha
                printNewLine();

                String id;
                String importe;

                for (viaje Viaje : viajes) {
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
                iniciar_turno(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void iniciar_turno(final Context context){

        String newURL = Constantes.ALTA_TURNO + "?id_conductor=" + ls_id_conductor;
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
                                procesarRespuestaActualizarAlta(response, context);
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
    private void procesarRespuestaActualizarAlta(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    cargarTurno(context);
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

    public void cargarTurno(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TURNO + "?conductor=" + ls_id_conductor;
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
                                procesarRespuestaCargaTurno(response, context);
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

    private void procesarRespuestaCargaTurno(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            Fragment fragment = null;
            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("conductor");
                    JSONObject object = mensaje1.getJSONObject(0);

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    ls_id_turno = object.getString("id");

                    editor.putString("id_turno_chofer",ls_id_turno);
                    editor.apply();


                    break;

                case "2":
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void actualizar_coordenadas(Context context){

        Geocoder coder = new Geocoder(context);
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(salida_coordenada, 50);
            for(Address add : adresses){
                if (!adresses.isEmpty()) {

                    longitud_salida = String.valueOf(add.getLongitude());
                    latitud_salida = String.valueOf(add.getLatitude());
                    if(longitud_salida == null){
                        longitud_salida = "0";
                    }
                    if(latitud_salida == null){
                        latitud_salida = "0";
                    }
                }else{
                    longitud_salida = "0";
                    latitud_salida = "0";
                }
            } }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(destino_coordenada, 50);
            for(Address add : adresses){
                if (!adresses.isEmpty()) {

                    longitud_destino = String.valueOf(add.getLongitude());
                    latitud_destino = String.valueOf(add.getLatitude());
                    if(longitud_destino == null){
                        longitud_destino = "0";
                    }
                    if(latitud_destino == null){
                        latitud_destino = "0";
                    }
                }else{
                    longitud_destino = "0";
                    latitud_destino = "0";
                }
            } }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void iniciar_viaje(final Context context){

        Location location_salida = new Location("salida");
        location_salida.setLatitude(getValor(latitud_salida));  //latitud
        location_salida.setLongitude(getValor(longitud_salida)); //longitud
        Location location_destino = new Location("destino");
        location_destino.setLatitude(getValor(latitud_destino));  //latitud
        location_destino.setLongitude(getValor(longitud_destino)); //longitud
        double distance = location_salida.distanceTo(location_destino) / 100;
        distancia = String.valueOf(distance);


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", id_viaje);
        map.put("distancia", distancia);
        map.put("latitud_salida", latitud_salida);
        map.put("longitud_salida", longitud_salida);
        map.put("latitud_destino", latitud_destino);
        map.put("longitud_destino", longitud_destino);

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


        String newURL = Constantes.INICIAR_VIAJE + "?" + encodedParams;

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
                                procesarRespuestaActualizar(response, context);
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
    private void procesarRespuestaActualizar(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    cargarParametroTolerancia(context);

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

    public void cargarParametroTolerancia(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

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
        String newURL = Constantes.GET_TOLERANCIA + "?" + encodedParams;
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
                                procesarRespuestaParametroTolerancia(response, context);
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

    private void procesarRespuestaParametroTolerancia(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("remiseria");
                    Long l_tolerancia_tope = 0L;
                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_tolerancia_tope = Long.parseLong(object.getString("tiempo_tolerancia")) * 60000;

                    }

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    editor.putLong("tolerancia_tope",l_tolerancia_tope);

                    editor.apply();
                    cargarParametroEspera(context);

                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarParametroEspera(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "13");
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
                                procesarRespuestaParametroEspera(response, context);
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

    private void procesarRespuestaParametroEspera(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");
                    Long l_tiempo_espera = 0L;
                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_tiempo_espera = Long.parseLong(object.getString("valor")) * 1000;

                    }

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    editor.putLong("tiempo_espera",l_tiempo_espera);

                    editor.apply();
                    cargarParametroMetrosFicha(context);

                    break;


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarParametroMetrosFicha(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "14");
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
                                procesarRespuestaParametroMetrosFicha(response, context);
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

    private void procesarRespuestaParametroMetrosFicha(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");
                    Integer l_metros_ficha = 0;
                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_metros_ficha = Integer.parseInt(object.getString("valor")) ;

                    }

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    editor.putInt("metros_ficha",l_metros_ficha);

                    editor.apply();
                    if(mBound) {
                        act.unbindService(connection);

                        mBound = false;
                    }
                    Intent intent2 = new Intent(context, MainViaje.class);
                    context.startActivity(intent2);
                    act.finish();

                    break;


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void anular_viaje(final Context context){

        String newURL = Constantes.ANULAR_VIAJE + "?id=" + id_viaje;

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
                                procesarRespuestaActualizarAnular(response,context);
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
    private void procesarRespuestaActualizarAnular(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Intent intent2 = new Intent(context, MainActivity.class);
                    context.startActivity(intent2);
                    act.finish();
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

    public void datos_ultimos_viajes(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ULTIMOS_VIAJES + "?chofer=" + ls_id_conductor;
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
                                procesarRespuestaUltimosViajes(response, context);
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

    private void procesarRespuestaUltimosViajes(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            viajes.clear();
            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viajes");

                    for(int i = 0; i < mensaje1.length(); i++) {
                        JSONObject object = mensaje1.getJSONObject(i);

                        viaje via = new viaje();

                        via.setId(String.valueOf(i));

                        String hora = object.getString("hora_inicio");

                        via.setHora_inicio(hora);

                        String importe = object.getString("importe");

                        via.setImporte(importe);

                        String fecha = object.getString("fecha");

                        via.setFecha(fecha);

                        String destino = object.getString("destino");

                        via.setDestino(destino);

                        viajes.add(via);
                    }

                    break;

            }


            ticket_ultimos_viajes(viajes);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void ticket_ultimos_viajes( ArrayList<viaje> viajes) {

        outputStream = impresion.getOutputStream();
        if(outputStream == null){
            Toast.makeText(context, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
        }else {

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printCustom(getResources().getString(R.string.telefono), 1, 1);

                printNewLine();
                printUnicode();
                printNewLine();
                printText(getResources().getString(R.string.ticket_ultimos_viajes)); // total 32 char in a single line
                printNewLine();

                String id;
                String importe;
                String fecha;
                String hora;
                Double l_total = 0.00;

                for (viaje Viaje : viajes) {
                    id = Viaje.getId();
                    printCustom("VIAJE " + id, 1, 0);

                    fecha = Viaje.getFecha();
                    printText("Fecha:  " + fecha);

                    hora = Viaje.getHora_inicio();
                    printNewLine();
                    printText("Hora Inicio:  " + hora);

                    importe = Viaje.getImporte();
                    printNewLine();
                    printText("Importe:  " + importe);
                    l_total = l_total + Double.parseDouble(importe);
                    printNewLine();
                    printNewLine();
                }
                printNewLine();
                printText("TOTAL: ");
                printText(String.valueOf(getTwoDecimals(l_total)));
                printNewLine();
                printNewLine();
                printUnicode();
                //resetPrint(); //reset printer

                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getTwoDecimals(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    public void repetirTicket(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ULTIMO_VIAJE + "?conductor=" + ls_id_conductor;
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
                                procesarRespuestaRepetir(response, context);
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

    private void procesarRespuestaRepetir(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viaje");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    fecha_ultimo =object.getString("fecha");
                    salida_ultimo = object.getString("salida");
                    destino_ultimo = object.getString("destino");
                    hora_salida_ultimo = object.getString("hora_inicio");
                    hora_destino_ultimo =  object.getString("hora_fin");
                    String ls_importe;
                    ls_importe = object.getString("importe");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }
                    importe_ultimo = ls_importe;


                    ls_importe = object.getString("importe_espera");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }
                    espera_ultimo = ls_importe;

                    ls_importe = object.getString("importe_fichas");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }
                    fichas_ultimo = ls_importe;

                    ls_importe = object.getString("bajada");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }
                    bajada_ultimo = ls_importe;

                    ls_importe = object.getString("total");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }
                    total_ultimo = ls_importe;

                    chofer_ultimo = object.getString("chofer");
                    distancia_ultimo = object.getString("distancia");
                    fecha_tarifa_ultimo = object.getString("fecha_tarifa");
                    movil_ultimo = object.getString("movil");
                    chapa = object.getString("chapa");
                    patente = object.getString("patente");

                    repetirTicket();

                case "2":
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void repetirTicket() {

        outputStream = impresion.getOutputStream();

        if(outputStream == null){
            Toast.makeText(context, "No se pudo conectar el dispositivo. Verifique si la impresora esta encendida", Toast.LENGTH_SHORT).show();
        }else {

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] printformat = {0x1B, 0x21, 0x08};
                outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printCustom(getResources().getString(R.string.telefono), 1, 1);
                printNewLine();
                printText(getResources().getString(R.string.recibo)); // total 32 char in a single line
                printNewLine();
                printText(stringABytes(getResources().getString(R.string.servicio)));
                printNewLine();
                printText(fecha_ultimo);//fecha
                printNewLine();
                printCustom("Chofer: " + chofer_ultimo, 1, 0);
                printText(stringABytes(getResources().getString(R.string.nro_movil) + movil_ultimo));
                printCustom("Patente: " + patente, 1, 0);
                printText(stringABytes(getResources().getString(R.string.chapa) + chapa));
                printNewLine();
                printText("SALIDA  " + hora_salida_ultimo);
                printNewLine();
                printText("DESDE  " + salida_ultimo);
                printNewLine();
                printText("HASTA  " + destino_ultimo);
                printNewLine();
                printText("LLEGADA  " + hora_destino_ultimo);
                printNewLine();
                printText("RECORRIDO  " + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(distancia_ultimo)) + " Kms.");
                printNewLine();
                printNewLine();
                printText("TARIFA AL  " + fecha_tarifa_ultimo);
                printNewLine();
                printText("BAJADA  " + '$' + String.format(Locale.GERMAN, "%.2f", Double.parseDouble(bajada_ultimo)));
                printNewLine();
                printText("VIAJE  " + '$' + String.format(Locale.GERMAN, "%.2f", Double.parseDouble(fichas_ultimo)));
                printNewLine();
                printText("ESPERA  " + '$' + String.format(Locale.GERMAN, "%.2f", Double.parseDouble(espera_ultimo)));
                printNewLine();
                printNewLine();
                printCustom("TOTAL:  " + '$' + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(importe_ultimo)), 2, 0);
                printCustom("", 1, 1);
                printUnicode();
                printNewLine();
                printNewLine();
                outputStream.flush();

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

    public void cargarImpresora(final Context context) {

        String newURL = Constantes.GET_CONDUCTOR_BY_ID + "?conductor=" + ls_id_conductor;
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
                                procesarRespuestaImpresora(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley parametro: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaImpresora(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parada = response.getJSONArray("conductor");

                    for(int i = 0; i < datos_parada.length(); i++)
                    {
                        JSONObject object = datos_parada.getJSONObject(i);

                        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                        SharedPreferences.Editor editor = settings1.edit();

                        String l_impresora;

                        l_impresora = object.getString("impresora");

                        editor.putString("impresora",l_impresora);
                        editor.putString("tipo_ventana", "main");
                        editor.apply();

                        if(!l_impresora.equals("")) {


                            Intent intent = new Intent(context, Impresion.class);
                            context.startService(intent);
                            esperarYCerrar(1500, intent,context);

                        }else{

                            Intent intent = new Intent(context, Impresion.class);
                            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
                            context.startService(intent);

                        }

                    }
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void esperarYCerrar(int milisegundos, Intent intent, final Context context) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                bindApp(intent, context);
            }
        }, milisegundos);
    }
    public void bindApp(Intent intent, Context context) {
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if(impresion != null) {
            if (impresion.getBluetoothAdapter() != null) {
                if (impresion.getOutputStream() != null) {
                    impresora.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    mBound = true;
                } else {
                    impresora.setTextColor(context.getResources().getColor(R.color.alarma));
                    mBound = false;

                }
            } else {
                impresora.setTextColor(context.getResources().getColor(R.color.alarma));
                mBound = false;
            }
        }else{
            impresora.setTextColor(context.getResources().getColor(R.color.alarma));
            mBound = false;
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
