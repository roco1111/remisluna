package com.rosario.hp.remisluna.Fragment;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.misc.AsyncTask;
import com.android.volley.request.JsonObjectRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.ServicioGeolocalizacion;
import com.rosario.hp.remisluna.WebActivity;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class fragment_viaje_iniciado extends Fragment {
    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_viaje_iniciado.class.getSimpleName();
    private static OutputStream outputStream;
    private String ls_id_conductor;
    private TextView id_viaje;
    private TextView solicitante;
    private TextView texto_solicitante;
    private TextView dato_salida;
    private TextView destino;
    private TextView kms;
    private TextView importe;
    private TextView texto_tarifa;
    private TextView tiempo_viaje;
    private TextView ficha_espera;
    private TextView salida;
    private TextView texto_destino;
    private TextView monto_ficha;
    private TextView gps;
    private TextView titulo_ficha;
    private TextView titulo_espera;
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
    private Button buttonmenu;
    private Button alarma;
    private Button boton_viaje;
    private Button repetirTicket;
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
    private String tipo_empresa;

    private Button boton_whatsapp;
    private Button boton_mapa;
    private String telefono_base;
    private Boolean lb_servicio = false;
    private Button inicio;
    private LocationManager mLocationManager;
    private String latitud_salida;
    private String longitud_salida;
    private String latitud_destino;
    private String longitud_destino;
    private String destino_coordenada;
    private String l_id_viaje;
    private RelativeLayout id_botones_en_curso;
    private RelativeLayout id_botones_asignado;
    private RelativeLayout id_botones_terminar;
    private LinearLayout id_datos_viaje;
    private String l_estado_viaje;

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
    private String chapa;
    private String patente;
    private Impresion impresion;
    private String l_impresion;
    private String nro_recibo;
    private String precio_km;
    boolean mBound = false;
    private boolean lb_bluetooth;
    private File file;
    private File dir;
    private String nombre_remiseria;
    private String telefono_queja;
    private String localidad_abreviada;
    private String telefono_remiseria;
    private String path;
    private String viajes_automaticos;
    private String automatico;
    private String viajes_automaticos_chofer;

    @Override
    public void onPause() {
        if (lb_servicio){
            if (isMyServiceRunning(ServicioGeolocalizacion.class)) {
                act.unregisterReceiver(onBroadcast);
            }
        }
        if(mBound) {
            act.unbindService(connection);

            mBound = false;
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        if(lb_servicio) {
            act.registerReceiver(onBroadcast, new IntentFilter("key"));
        }
        if(l_impresion.equals("1")) {
            if (lb_bluetooth) {
                cargarImpresora(getContext());
            }
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_viaje_iniciado, container, false);

        act = getActivity();

        context = getContext();
        mLocationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        if (bt == null)
        { //Does not support Bluetooth
            lb_bluetooth = false;
        }else{
            //Magic starts. Let's check if it's enabled
            if (!bt.isEnabled())
            { lb_bluetooth = false; }
            else{
                lb_bluetooth = true;
            }
        }

        //((MainActivity) getActivity()).locationEnd();

        id_botones_en_curso = v.findViewById(R.id.id_botones_en_curso);
        id_botones_terminar = v.findViewById(R.id.id_botones_terminar);
        id_botones_asignado = v.findViewById(R.id.id_botones_asignado);
        id_datos_viaje = v.findViewById(R.id.id_datos_viaje);

        id_viaje = v.findViewById(R.id.dato_viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        texto_solicitante = v.findViewById(R.id.solicitante);
        dato_salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        texto_destino = v.findViewById(R.id.destino);
        sin_ticket = v.findViewById(R.id.buttonSinTicket);
        boton_whatsapp = v.findViewById(R.id.imageWa);
        inicio = v.findViewById(R.id.buttonInicio);
        buttonmenu = v.findViewById(R.id.buttonmenu);
        boton_viaje = v.findViewById(R.id.buttonviaje);
        boton_mapa = v.findViewById(R.id.mapa);
        this.repetirTicket = v.findViewById(R.id.buttonTicket);

        alarma = v.findViewById(R.id.buttonAlarma);
        kms = v.findViewById(R.id.kms);
        importe = v.findViewById(R.id.precio);
        texto_tarifa = v.findViewById(R.id.tarifa);
        tiempo_viaje = v.findViewById(R.id.tiempo);
        ficha_espera = v.findViewById(R.id.ficha_espera);
        salida = v.findViewById(R.id.salida);
        gps = v.findViewById(R.id.gps);
        monto_ficha = v.findViewById(R.id.monto_ficha);
        titulo_espera = v.findViewById(R.id.titulo_espera);
        titulo_ficha = v.findViewById(R.id.titulo_ficha);


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

        this.inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();

                cargarRemiseria(context);

            }
        });

        this.boton_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("url", "https://callisto.com.ar/remiseria/mapa_movil.php?viaje=" + l_id_viaje );
                editor.apply();
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);

            }
        });

        this.alarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                lb_ticket = false;
                lb_viaje_terminado = true;
                if(isMyServiceRunning(ServicioGeolocalizacion.class)) {
                    act.unregisterReceiver(onBroadcast);
                    act.stopService(new Intent(act, ServicioGeolocalizacion.class));
                    //act.stopService(new Intent(act, ServicioGeolocalizacion_metros.class));
                    Log.d("Servicio","Servicio detenido");
                }
                alarma_viaje(context);
            }
        });

        this.buttonmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                l_estado_viaje = "asignado";
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("estado_viaje","asignado");
                editor.apply();
                Intent intent2 = new Intent(context, MainActivity.class);
                context.startActivity(intent2);
                act.finish();
            }
        });

        this.boton_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.start();
                viaje_automatico(context);

            }
        });

        this.boton_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.start();
                cargarDatosRemiseria(context, v);

            }
        });

        this.repetirTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                repetirTicket(context);
            }
        });
        reiniciar();

        return v;
    }

    private void reiniciar(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        ls_id_conductor     = settings.getString("id","");
        id_turno            = settings.getString("id_turno_chofer","");
        ls_remiseria     = settings.getString("remiseria","");
        l_porcentaje = getValor(settings.getString("porcentaje","0"));
        l_hora_desde = settings.getString("tarifa_desde","");
        l_hora_hasta= settings.getString("tarifa_hasta","");
        ls_es_feriado = settings.getString("feriado","");
        l_tolerancia_tope = settings.getLong("tolerancia_tope",0L);
        tipo_empresa = settings.getString("tipo_empresa","");
        l_estado_viaje = settings.getString("estado_viaje","");
        l_id_viaje = settings.getString("id_viaje","");
        l_impresion = settings.getString("impresion","");
        viajes_automaticos = settings.getString("viajes_automaticos", "");
        automatico = settings.getString("automatico", "");
        viajes_automaticos_chofer = settings.getString("viajes_automaticos_chofer", "");

        sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        lb_viaje_terminado = false;

        switch (l_estado_viaje){
            case "asignado":
                id_botones_asignado.setVisibility(View.VISIBLE);
                id_botones_terminar.setVisibility(View.GONE);
                id_botones_en_curso.setVisibility(View.GONE);
                break;
            case "en curso":
                id_botones_asignado.setVisibility(View.GONE);
                id_botones_terminar.setVisibility(View.GONE);
                id_botones_en_curso.setVisibility(View.VISIBLE);
                act.registerReceiver(onBroadcast, new IntentFilter("key"));
                break;
            case "terminado":
                id_botones_asignado.setVisibility(View.GONE);
                id_botones_terminar.setVisibility(View.VISIBLE);
                id_botones_en_curso.setVisibility(View.GONE);
                break;
        }

        switch (tipo_empresa) {
            case "1":
                id_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                importe.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                ficha_espera.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                kms.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                salida.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                dato_salida.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                texto_destino.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                destino.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                solicitante.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                texto_solicitante.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                texto_tarifa.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                gps.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                monto_ficha.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                titulo_ficha.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                titulo_espera.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                break;
            case "2":
                id_viaje.setTextColor(act.getResources().getColor(R.color.colorMoto));
                importe.setTextColor(act.getResources().getColor(R.color.colorMoto));
                tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorMoto));
                ficha_espera.setTextColor(act.getResources().getColor(R.color.colorMoto));
                kms.setTextColor(act.getResources().getColor(R.color.colorMoto));
                salida.setTextColor(act.getResources().getColor(R.color.colorMoto));
                dato_salida.setTextColor(act.getResources().getColor(R.color.colorMoto));
                texto_destino.setTextColor(act.getResources().getColor(R.color.colorMoto));
                destino.setTextColor(act.getResources().getColor(R.color.colorMoto));
                solicitante.setTextColor(act.getResources().getColor(R.color.colorMoto));
                texto_solicitante.setTextColor(act.getResources().getColor(R.color.colorMoto));
                texto_tarifa.setTextColor(act.getResources().getColor(R.color.colorMoto));
                gps.setTextColor(act.getResources().getColor(R.color.colorMoto));
                monto_ficha.setTextColor(act.getResources().getColor(R.color.colorMoto));
                titulo_ficha.setTextColor(act.getResources().getColor(R.color.colorMoto));
                titulo_espera.setTextColor(act.getResources().getColor(R.color.colorMoto));
                break;
            case "3":
                id_viaje.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                importe.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                ficha_espera.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                kms.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                salida.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                dato_salida.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                texto_destino.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                destino.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                solicitante.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                texto_solicitante.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                texto_tarifa.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                gps.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                monto_ficha.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                titulo_ficha.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                titulo_espera.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                break;
        }

        String l_hoy;

        c = Calendar.getInstance();
        SimpleDateFormat shoy = new SimpleDateFormat("MM/dd/yyyy");
        l_hoy = shoy.format(c.getTime());
        getMyTime_desde = l_hoy + ' ' + l_hora_desde;
        getMyTime_hasta = l_hoy + ' ' + l_hora_hasta;

        if(l_estado_viaje.equals("en curso")) {
            cargarDatos(context);
        }else if(l_estado_viaje.equals("asignado")){
            cargarTarifaInicial(context);
        }

        if (automatico.equals("0")) {
            this.boton_mapa.setVisibility(View.VISIBLE);
            id_datos_viaje.setVisibility(View.VISIBLE);
            this.boton_viaje.setVisibility(View.GONE);
        }else{
            this.boton_mapa.setVisibility(View.GONE);
            id_datos_viaje.setVisibility(View.GONE);
            this.boton_viaje.setVisibility(View.VISIBLE);
        }
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

    public void cargarTarifaInicial(final Context context) {

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
                                    procesarRespuestaTarifaInicial(response, context);
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

    private void procesarRespuestaTarifaInicial(JSONObject response, Context context) {

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


                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("nocturno",l_nocturno);

                    if(l_nocturno.equals("0")) {
                        importe_bajada = object.getString("importe_bajada");

                        texto_tarifa.setText(R.string.diurno);
                    }else{
                        importe_bajada = object.getString("importe_bajada_nocturno");

                        texto_tarifa.setText(R.string.nocturno);
                    }
                    editor.putString("importe_bajada",object.getString("importe_bajada"));
                    editor.putString("importe_bajada_nocturno",object.getString("importe_bajada_nocturno"));
                    editor.putString("importe_ficha",object.getString("importe_ficha"));
                    editor.putString("importe_ficha_nocturno",object.getString("importe_ficha_nocturno"));
                    editor.putString("importe_espera",object.getString("importe_espera"));
                    editor.putString("importe_espera_nocturno",object.getString("importe_espera_nocturno"));

                    importe.setText(importe_bajada);


                    salida_coordenada = settings.getString("salida_coordenadas","");
                    destino_coordenada = settings.getString("destino_coordenadas","");
                    id_vehiculo = settings.getString("id_movil","");

                    id_viaje.setText(settings.getString("id_viaje",""));
                    solicitante.setText(settings.getString("solicitante",""));
                    dato_salida.setText(settings.getString("salida",""));
                    destino.setText(settings.getString("destino",""));
                    editor.putString("bajada","null");

                    editor.apply();

                    break;

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            monto_ficha.setText(String.valueOf(precio_ficha));
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

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        id_viaje.setText(settings.getString("id_viaje",""));
        solicitante.setText(settings.getString("solicitante",""));
        dato_salida.setText(settings.getString("salida",""));
        destino.setText(settings.getString("destino",""));
        id_vehiculo = settings.getString("id_movil","");
        ldb_porcentaje_titular = Double.parseDouble(settings.getString("porc_titular",""));

        String l_nocturno;
        l_nocturno     = settings.getString("nocturno","");
        if(l_nocturno.equals("0")) {
            if(settings.getString("bajada","").equals("null") || settings.getString("bajada","").equals("0.000")) {
                importe_bajada = settings.getString("importe_bajada","");
            }else{
                importe_bajada =settings.getString("bajada","");
            }
            importe_ficha = settings.getString("importe_ficha","");
            importe_espera = settings.getString("importe_espera","");
            texto_tarifa.setText(R.string.diurno);
        }else{
            if(settings.getString("bajada","").equals("null")|| settings.getString("bajada","").equals("0.000")) {
                importe_bajada = settings.getString("importe_bajada_nocturno","");
            }else{
                importe_bajada =settings.getString("bajada","");
            }
            importe_ficha = settings.getString("importe_ficha_nocturno","");
            importe_espera = settings.getString("importe_espera_nocturno","");
            texto_tarifa.setText(R.string.nocturno);
        }
        movil = settings.getString("movil","");
        ls_bajada = importe_bajada;
        precio_bajada = Double.parseDouble(importe_bajada);
        if(settings.getString("bajada","").equals("null") || settings.getString("bajada","").equals("0.000")) {
            precio_total = Double.parseDouble(importe_bajada);
            id_trayecto = 0;

        }else{
            precio_total = Double.parseDouble(settings.getString("total",""));
            tiempo_viaje.setText(settings.getString("tiempo",""));
            kms.setText(settings.getString("fichas",""));
            ficha = Long.parseLong(settings.getString("fichas",""));
            ficha_espera.setText(settings.getString("importe_espera_viaje",""));
            espera = Long.parseLong(settings.getString("fichas_espera",""));
            id_trayecto = Integer.parseInt(settings.getString("trayectoria",""));
            tipo_espera = Integer.parseInt(settings.getString("tipo_espera",""));
            precio_ficha = Double.parseDouble(settings.getString("importe_fichas",""));
            precio_espera = Double.parseDouble(settings.getString("importe_espera_viaje",""));
            if(tipo_espera == 0){
                tiempo_viaje.setTextColor(act.getResources().getColor(R.color.suspender));
            }else{
                tiempo_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
            }

        }
        importe.setText(String.valueOf(precio_total));
        ls_precio = String.format(Locale.GERMANY,"%.2f",precio_total);
        cronometroActivo = true;

        salida_coordenada = settings.getString("salida_coordenadas","");

        if(settings.getLong("tiempo_tolerancia",0L) != 0L) {

            tiempo_tolerancia = settings.getLong("tiempo_tolerancia",0L);

        }

        if(settings.getLong("tiempo_acumulado",0L) != 0L) {

            tiempo_acumulado = settings.getLong("tiempo_acumulado",0L);

        }

        if(settings.getString("tipo_espera","").equals("0")){
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


    }

    private class Iniciar_servicio_tiempo extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... params ) {

            //act.startService(new Intent(act,ServicioGeolocalizacion.class));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(act,ServicioGeolocalizacion.class).putExtra("stops",true));
            }else{
                context.startService(new Intent(act,ServicioGeolocalizacion.class));
            }
            //act.startService(new Intent(act,ServicioGeolocalizacionFused.class));
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

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong("tiempo_acumulado",0L);
                    editor.putLong("tiempo_tolerancia",0L);
                    editor.putString("total","0");
                    editor.putString("importe_fichas","0.00");
                    editor.putString("importe_espera_viaje","0.00");
                    editor.putBoolean("boolean_tolerancia",true);
                    editor.apply();

                    tiempo_acumulado = 0L;
                    tiempo_tolerancia = 0L;

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
            SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = settings1.edit();
            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("viaje");
                    JSONObject object = mensaje1.getJSONObject(0);


                    String ls_viaje;

                    ls_viaje = object.getString("id");

                    editor.putString("id_viaje",ls_viaje);
                    l_estado_viaje = "asignado";
                    editor.putString("estado_viaje","asignado");

                    break;

                case "2":
                    l_estado_viaje = "terminado";
                    editor.putString("estado_viaje","terminado");

                    break;

            }
            editor.apply();
            reiniciar();
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
                    terminar_viaje(context);
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
                                procesarRespuestaTerminar_viaje(response, context);
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
    private void procesarRespuestaTerminar_viaje(JSONObject response, Context context) {

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
                    cargarDatosFinal(context);
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
                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    l_estado_viaje = "terminado";
                    editor.putString("estado_viaje","terminado");

                    editor.putLong("tiempo_acumulado",0L);
                    editor.putLong("tiempo_tolerancia",0L);
                    editor.putBoolean("boolean_tolerancia",true);
                    editor.putString("importe_fichas","0.00");
                    editor.putString("importe_espera_viaje","0.00");

                    tiempo_viaje.setText("00:00");
                    tiempo_viaje.setText(getResources().getString(R.string.cero_tiempo));
                    ficha_espera.setText(getResources().getString(R.string.cero_pesos));
                    monto_ficha.setText(getResources().getString(R.string.cero_pesos));
                    kms.setText("0");

                    editor.apply();

                    reiniciar();
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

    public void cargarRemiseria(final Context context) {

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
                                procesarRespuesta_remiserias(response, context);
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

    private void procesarRespuesta_remiserias(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("remiseria");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    String habilitada = object.getString("HABILITADA");

                    if(habilitada.equals("1"))
                    {
                        cargarDatos_solicitados(context);
                    }else{
                        Toast.makeText(
                                context,
                                R.string.inhabilitada,
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                case "2":

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarDatos_solicitados(final Context context) {

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
                                procesarRespuesta_Datos_solicitados(response, context);
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

    private void procesarRespuesta_Datos_solicitados(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            Fragment fragment = null;
            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("viaje");
                    JSONObject object = mensaje1.getJSONObject(0);

                    id_vehiculo = object.getString("id_movil");

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    String ls_viaje, ls_remiseria, ls_telefono_queja, ls_telefono;


                    ls_viaje = object.getString("id");

                    editor.putString("id_viaje",ls_viaje);

                    ls_remiseria = object.getString("remiseria");

                    editor.putString("nombre_remiseria",ls_remiseria);

                    ls_telefono_queja = object.getString("telefono_queja");

                    editor.putString("telefono_queja",ls_telefono_queja);

                    ls_telefono = object.getString("telefono");

                    editor.putString("telefono_remiseria",ls_telefono);
                    editor.putString("estado_viaje","asignado");
                    editor.putString("salida_coordenadas",object.getString("salida_coordenadas"));
                    editor.putString("destino_coordenadas",object.getString("destino_coordenadas"));
                    editor.putString("id_movil",object.getString("id_movil"));
                    l_estado_viaje = "en curso";


                    editor.putString("estado_viaje","en curso");

                    editor.apply();
                    iniciar_viaje();

                    break;

                case "2":
                    Toast.makeText(
                            context,
                            "No hay viajes asignados",
                            Toast.LENGTH_LONG).show();

                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void iniciar_viaje(){
        if (verificar_internet()) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(
                        context,
                        R.string.no_gps,
                        Toast.LENGTH_LONG).show();

            } else {
                actualizar_coordenadas(context);


            }
        } else {
            Toast.makeText(
                    context,
                    R.string.no_internet,
                    Toast.LENGTH_LONG).show();
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

        iniciar_viaje(context);

    }

    private void iniciar_viaje(final Context context){

        latitud_salida = String.valueOf(getValor(latitud_salida));
        longitud_salida = String.valueOf(getValor(longitud_salida));
        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = settings1.edit();
        editor.putString("latitud_salida",latitud_salida);
        editor.putString("longitud_salida",longitud_salida);
        editor.apply();

        latitud_destino = String.valueOf(getValor(latitud_destino));
        longitud_destino = String.valueOf(getValor(longitud_destino));
        Location location_salida = new Location("salida");
        location_salida.setLatitude(Double.parseDouble(latitud_salida));  //latitud
        location_salida.setLongitude(Double.parseDouble(longitud_salida)); //longitud
        Location location_destino = new Location("destino");
        location_destino.setLatitude(Double.parseDouble(latitud_destino));  //latitud
        location_destino.setLongitude(Double.parseDouble(longitud_destino)); //longitud

        double distance = location_salida.distanceTo(location_destino) / 100;
        try {
            distancia = String.valueOf(distance);
        } catch (Exception e) {
            distancia = "0";
            e.printStackTrace();
        }

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", l_id_viaje);
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

                    reiniciar();

                    break;


            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    private void viaje_automatico(final Context context) {
        String newURL = Constantes.VIAJE_AUTOMATICO + "?conductor=" + ls_id_conductor;

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
                                procesarAgregarViajeAut(response, context);
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

    private void procesarAgregarViajeAut(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":

                    cargarIdVehiculoParada(context);

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

    public void cargarIdVehiculoParada(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ID_VEHICULO + "?id=" + ls_id_conductor;
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
                                procesarRespuesta_ID_Parada(response, context);
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

    private void procesarRespuesta_ID_Parada(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("vehiculo");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    id_vehiculo = object.getString("id");
                    borrar_parada(context);
                    break;

                case "2":

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void borrar_parada(final Context context){

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        String l_parada = "0";

        map.put("parada", l_parada);
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

        String newURL = Constantes.UPDATE_PARADAS + "?" + encodedParams;
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
                                procesarRespuestaBorrarParada(response, context);
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
    private void procesarRespuestaBorrarParada(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    l_estado_viaje = "asignado";


                    editor.putString("estado_viaje","asignado");
                    editor.putString("automatico", "1");
                    editor.putLong("tiempo_acumulado",0L);
                    editor.putLong("tiempo_tolerancia",0L);
                    editor.putBoolean("boolean_tolerancia",true);
                    editor.putString("importe_fichas","0.00");
                    editor.putString("importe_espera_viaje","0.00");
                    tiempo_viaje.setText(getResources().getString(R.string.cero_tiempo));
                    ficha_espera.setText(getResources().getString(R.string.cero_pesos));
                    monto_ficha.setText(getResources().getString(R.string.cero_pesos));
                    kms.setText("0");

                    editor.apply();

                    reiniciar();


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
                    nro_recibo = object.getString("nro_recibo");
                    precio_km = object.getString("precio_km");
                    nombre_remiseria = object.getString("remiseria");
                    telefono_queja = object.getString("telefono_queja");
                    localidad_abreviada = object.getString("abreviada");
                    telefono_remiseria = object.getString("telefono");

                    if(mBound) {
                        repetirTicket();
                    }else{
                        crear_pfd_repetir_ticket();
                    }

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

                Locale locale2 = new Locale ("es", "ES");
                NumberFormat objNF2 = NumberFormat.getInstance (locale2);
                objNF2.setMinimumFractionDigits(2);
                objNF2.setMaximumFractionDigits(2);

                byte[] printformat = {0x1B, 0x21, 0x08};
                outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(nombre_remiseria, 2, 1);
                printNewLine();
                printCustom("Tel. Remis: " + telefono_remiseria, 1, 1);
                printNewLine();
                printCustom("Tel. Queja: " + telefono_queja, 1, 1);
                printNewLine();
                printText(act.getResources().getString(R.string.recibo)); // total 32 char in a single line
                printNewLine();
                printText("Nro Recibo: " + nro_recibo ); // total 32 char in a single line
                printNewLine();
                printText(stringABytes(act.getResources().getString(R.string.servicio) + ' ' + localidad_abreviada));
                printNewLine();
                printText(fecha_ultimo);//fecha
                printNewLine();
                printCustom("Chofer: " + chofer_ultimo, 1, 0);
                printText(stringABytes(act.getResources().getString(R.string.nro_movil) + movil_ultimo));
                printCustom("Patente: " + patente, 1, 0);
                printText(stringABytes(act.getResources().getString(R.string.chapa) + chapa));
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
                printText("BAJADA  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(bajada_ultimo))));
                printNewLine();
                printText("VIAJE  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(fichas_ultimo))));
                printNewLine();
                printText("ESPERA  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(espera_ultimo))));
                printNewLine();
                printNewLine();
                printCustom("TOTAL:  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(importe_ultimo))), 2, 0);
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

    private void crear_pfd_repetir_ticket()
    {
        directorio();
        pdf_repetir_ticket( );
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(FileProvider.getUriForFile(context, act.getPackageName() + ".my.package.name.provider", file), "application/pdf");
        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(target);
        } catch (ActivityNotFoundException e) {
            Intent intent = Intent.createChooser(target, "Open File");
            startActivity(intent);
        }
    }

    private Boolean pdf_repetir_ticket()
    {
        boolean success = false;
        PdfPCell cell;


        //saldo=saldo.replace("\n","");
        //create document file
        Document doc = new Document(PageSize.A5, 14f, 10f, 10f, 10f);
        try {
            doc.left(10f);
            //doc.top(15f);
            file = new File(dir, "ticket-"+ nro_recibo +".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);

            Locale locale2 = new Locale ("es", "ES");
            NumberFormat objNF2 = NumberFormat.getInstance (locale2);
            objNF2.setMinimumFractionDigits(2);
            objNF2.setMaximumFractionDigits(2);

            doc.open();

            BaseFont bf = BaseFont.createFont(
                    BaseFont.HELVETICA,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);
            Font font = new Font(bf, 15);

            Font titulo = new Font(bf, 20);

            float[] columnWidth;

            columnWidth = new float[]{100};

            PdfPTable tabla_enc = new PdfPTable(1);

            cell = new PdfPCell(new Phrase(nombre_remiseria,titulo));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla_enc.addCell(cell);

            cell = new PdfPCell(new Phrase("Tel. Remisería: " + telefono_remiseria,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla_enc.addCell(cell);

            cell = new PdfPCell(new Phrase("Tel. Queja: " + telefono_queja,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla_enc.addCell(cell);

            doc.add(tabla_enc);

            LineSeparator lineSeparator = new LineSeparator();

            lineSeparator.setLineColor(new BaseColor(255, 255, 255, 68));

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table1 = new PdfPTable(1);


            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.recibo),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Nro Recibo: " + nro_recibo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.servicio) + ' ' + localidad_abreviada,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            doc.add(table1);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table2 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase(fecha_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase("Chofer: " + chofer_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase("Patente: " + patente,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.chapa) + chapa,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);
            doc.add(table2);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("SALIDA: " + hora_salida_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("DESDE: " + salida_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("HASTA: " + destino_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("LLEGADA: " + hora_destino_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("RECORRIDO: " + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(distancia_ultimo)) + " Kms.",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            doc.add(table);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table3 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("TARIFA AL " + fecha_tarifa_ultimo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase("BAJADA: " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(bajada_ultimo))),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase("VIAJE: " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(fichas_ultimo))),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase("ESPERA: " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(espera_ultimo))),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            doc.add(table3);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table4 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("TOTAL: " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(importe_ultimo))),titulo));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table4.addCell(cell);

            doc.add(table4);

        } catch (DocumentException | IOException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } finally {
            doc.close();

            success = true;
        }

        return success;

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
            Bitmap bmp = BitmapFactory.decodeResource(act.getResources(),
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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Impresion.LocalBinder binder = (Impresion.LocalBinder) service;
            impresion = binder.getService();
            if (impresion.getBluetoothAdapter() != null && impresion.getbluetoothSocket() != null) {
                if (impresion.getOutputStream() != null) {

                    mBound = true;
                } else {
                    mBound = false;

                }
            } else {
                mBound = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
        @Override
        public void onBindingDied (ComponentName arg0) {
            mBound = false;
        }
    };

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
        Log.d("impresora", "bind");
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if (impresion != null) {
            if (impresion.getBluetoothAdapter() != null) {
                if (impresion.getOutputStream() != null) {
                    mBound = true;
                } else {
                    mBound = false;

                }
            } else {
                mBound = false;
            }
        }else{
            mBound = false;
        }
    }

    private void directorio(){
        checkFilePermissions();
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/pdf";
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = ContextCompat.checkSelfPermission(context,"Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += ContextCompat.checkSelfPermission(context,"Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {

                ActivityCompat.requestPermissions(act,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}
