package com.rosario.hp.remisluna.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import com.google.firebase.database.core.utilities.Tree;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
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
import com.rosario.hp.remisluna.Entidades.parada;
import com.rosario.hp.remisluna.Entidades.turno;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainCtaCte;
import com.rosario.hp.remisluna.MainQR;
import com.rosario.hp.remisluna.MainViaje;

import com.rosario.hp.remisluna.R;

import com.rosario.hp.remisluna.SplashScreenActivity;
import com.rosario.hp.remisluna.activity_preferencias;
import com.rosario.hp.remisluna.empresas_activity;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.turnos_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.itextpdf.text.Document;
import com.rosario.hp.remisluna.viajes_activity;

import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class fragment_principal extends Fragment {

    private static final String TAG = fragment_principal.class.getSimpleName();
    private JsonObjectRequest myRequest;
    private ImageButton boton_uno;
    private ImageButton boton_dos;
    private ImageButton boton_tres;
    private ImageButton boton_cuatro;
    private ImageButton boton_ocho;
    private ImageButton boton_cta_cte;

    private ImageButton boton_whatsapp;
    private Button boton_impresora;
    private Button boton_turno;
    private Button boton_viaje;
    private Button buttonParadas;
    private Button repetirTicket;

    private String l_hora_desde;
    private String l_hora_hasta;
    private String l_hoy;
    private String l_nocturno;
    private Calendar c;

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

    private String ls_id_turno;
    private String ls_id_turno_ultimo;
    private String recaudacion;
    private String kms;
    private String fecha;
    private String hora_inicio;
    private String hora_fin;
    private String estado;
    private ArrayList<viaje> viajes = new ArrayList<>();
    private static OutputStream outputStream;
    byte FONT_TYPE;
    private TextView impresora;
    private TextView texto_tarifa;
    private TextView gps;
    private TextView red;
    private TextView txt_parada;
    private TextView turno;
    private TextView text_parcial_turno;
    private TextView text_final_turno;
    private TextView text_ultimos_finales;
    private TextView text_reportes;
    private TextView text_viaje_x_viaje;
    private TextView text_historial;
    private TextView text_cta_cte;

    private Impresion impresion;
    private ArrayList<turno> datos;
    boolean mBound = false;
    private String ls_id_conductor;
    private String ls_remiseria;
    private String ls_es_feriado;
    private String fichas_ultimo;
    private String bajada_ultimo;
    private ArrayList<parada> paradas;
    private String latitud_destino;
    private String longitud_destino;
    private String id_movil;
    private String telefono_base;
    private String telefono_queja;
    private String telefono_remiseria;
    private String localidad_abreviada;
    private String nombre_remiseria;
    private String viajes_automaticos;
    private File dir;
    private String path;
    private String chapa;
    private String patente;
    private String chofer_habilitado;
    private String movil_habilitado;
    private Context context;
    private Activity act;
    private String nro_recibo;
    private String l_nro_turno;
    private String l_turno_parametro;
    private String precio_km;
    private boolean lb_ultimo;
    private String l_turno_app;
    private File file;
    private boolean lb_bluetooth;
    ProgressDialog progress1;
    private String l_impresion;
    private String l_paradas;
    private String tipo_empresa;
    private String viajes_automaticos_chofer;
    private String tipo_rendicion;
    private String saldo_vehiculo;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mBound) {
            requireActivity().unbindService(connection);

            mBound = false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(gps_habilitado()){
            switch (tipo_empresa) {
                case "1":
                    gps.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    break;
                case "2":
                    gps.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    break;
                case "3":
                    gps.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    break;
            }
        }else{
            gps.setTextColor(act.getResources().getColor(R.color.alarma));
        }
        if(red_habilitada()){
            switch (tipo_empresa) {
                case "1":
                    red.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    break;
                case "2":
                    red.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    break;
                case "3":
                    red.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    break;
            }
        }else{
            red.setTextColor(act.getResources().getColor(R.color.alarma));
        }
        if(l_impresion.equals("1")) {
            if (lb_bluetooth) {
                cargarImpresora(context);
            }
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
            if(impresion.getBluetoothAdapter() !=null && impresion.getbluetoothSocket() != null) {
                if (impresion.getOutputStream() != null) {
                    switch (tipo_empresa) {
                        case "1":
                            impresora.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                            break;
                        case "2":
                            impresora.setTextColor(act.getResources().getColor(R.color.colorMoto));
                            break;
                        case "3":
                            impresora.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                            break;
                    }
                    mBound = true;
                } else {
                    impresora.setTextColor(act.getResources().getColor(R.color.alarma));
                    mBound = false;

                }
            }else{
                impresora.setTextColor(act.getResources().getColor(R.color.alarma));
                mBound = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            impresora.setTextColor(act.getResources().getColor(R.color.alarma));
            mBound = false;
            Log.d("impresora", "desconectada");
        }
        @Override
        public void onBindingDied (ComponentName arg0) {
            impresora.setTextColor(act.getResources().getColor(R.color.alarma));
            mBound = false;
            Log.d("impresora", "onBindingDied");
        }

    };


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_principal, container, false);
        context = getContext();
        act = getActivity();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        ls_id_conductor = settings.getString("id", "");
        ls_id_turno = settings.getString("id_turno_chofer", "");
        ls_remiseria = settings.getString("remiseria", "");
        viajes_automaticos = settings.getString("viajes_automaticos", "");
        movil_habilitado = settings.getString("estado_vehiculo", "");
        chofer_habilitado = settings.getString("estado_conductor", "");
        nombre_remiseria = settings.getString("nombre_remiseria", "");
        telefono_queja = settings.getString("telefono_queja", "");
        telefono_remiseria = settings.getString("telefono_remiseria", "");
        tipo_empresa = settings.getString("tipo_empresa", "");
        l_impresion = settings.getString("impresion", "");
        l_turno_app = settings.getString("turnos_app", "");
        l_paradas = settings.getString("paradas", "");
        viajes_automaticos_chofer = settings.getString("viajes_automaticos_chofer", "");
        l_hora_desde = settings.getString("tarifa_desde", "");
        l_hora_hasta = settings.getString("tarifa_hasta", "");
        saldo_vehiculo = settings.getString("saldo_vehiculo", "");
        tipo_rendicion = settings.getString("tipo_rendicion", "0");

        telefono_base = settings.getString("telefono_base", "");

        c = Calendar.getInstance();
        SimpleDateFormat shoy = new SimpleDateFormat("MM/dd/yyyy");
        l_hoy = shoy.format(c.getTime());

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        this.boton_uno = v.findViewById(R.id.imageButtonUno);
        this.boton_dos = v.findViewById(R.id.imageButtonDos);
        this.boton_tres = v.findViewById(R.id.imageButtonTres);
        this.boton_cuatro = v.findViewById(R.id.imageButtonCuatro);
        this.boton_ocho = v.findViewById(R.id.imageButtonOcho);
        this.impresora = v.findViewById(R.id.impresora);
        this.boton_whatsapp = v.findViewById(R.id.imageWa);

        this.boton_turno = v.findViewById(R.id.buttonTurno);
        this.boton_viaje = v.findViewById(R.id.buttonViaje);
        this.buttonParadas = v.findViewById(R.id.buttonParadas);
        this.repetirTicket = v.findViewById(R.id.buttonTicket);
        this.boton_cta_cte = v.findViewById(R.id.imagecta_cte);
        this.txt_parada = v.findViewById(R.id.parada);
        this.text_parcial_turno = v.findViewById(R.id.text_parcial_turno);
        this.text_final_turno = v.findViewById(R.id.text_final_turno);
        this.text_cta_cte = v.findViewById(R.id.text_cta_cte);
        text_ultimos_finales = v.findViewById(R.id.text_ultimos_finales);
        text_reportes = v.findViewById(R.id.text_reportes);
        text_viaje_x_viaje = v.findViewById(R.id.text_viaje_x_viaje);
        text_historial = v.findViewById(R.id.text_historial);
        this.gps = v.findViewById(R.id.gps);
        this.red = v.findViewById(R.id.red);
        this.turno = v.findViewById(R.id.turno);
        texto_tarifa = v.findViewById(R.id.tarifa);
        this.boton_impresora = v.findViewById(R.id.buttonImpresora);
        paradas = new ArrayList<>();

        if (gps_habilitado()) {
            switch (tipo_empresa) {
                case "1":
                    gps.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    break;
                case "2":
                    gps.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    break;
                case "3":
                    gps.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    break;
            }
        } else {
            gps.setTextColor(act.getResources().getColor(R.color.alarma));
        }

        if(tipo_rendicion.equals("2") || tipo_rendicion.equals("3")){
            boton_cta_cte.setVisibility(View.VISIBLE);
            text_cta_cte.setVisibility(View.VISIBLE);
        }else{
            boton_cta_cte.setVisibility(View.GONE);
            text_cta_cte.setVisibility(View.GONE);
        }

        if (verificar_internet()) {
            switch (tipo_empresa) {
                case "1":
                    red.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    break;
                case "2":
                    red.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    break;
                case "3":
                    red.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    break;
            }
        } else {
            red.setTextColor(act.getResources().getColor(R.color.alarma));
        }

        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        if (bt == null) { //Does not support Bluetooth
            lb_bluetooth = false;
        } else {
            //Magic starts. Let's check if it's enabled
            if (!bt.isEnabled()) {
                lb_bluetooth = false;
            } else {
                lb_bluetooth = true;
            }
        }

        datos = new ArrayList<>();
        impresion = new Impresion();
        this.boton_turno.setText(act.getResources().getString(R.string.nuevo_turno));

        if (movil_habilitado.equals("1") && chofer_habilitado.equals("1")) {
            this.boton_cuatro.setEnabled(true);
            this.boton_ocho.setEnabled(true);
            this.boton_cta_cte.setEnabled(true);
            this.boton_viaje.setEnabled(true);
            this.buttonParadas.setEnabled(true);

            switch (tipo_empresa) {
                case "1":
                    this.turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));

                    this.txt_parada.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    this.texto_tarifa.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    this.red.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    text_reportes.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    text_historial.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro));
                    this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho));
                    this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_turno.setBackgroundTintList( ColorStateList.valueOf(ResourcesCompat.getColor(act.getResources(),
                                R.color.colorPrimary, act.getTheme())));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_viaje.setBackgroundTintList( ColorStateList.valueOf(ResourcesCompat.getColor(act.getResources(),
                                R.color.colorPrimary, act.getTheme())));
                    }

                    break;
                case "2":
                    this.turno.setTextColor(act.getResources().getColor(R.color.colorMoto));

                    this.txt_parada.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    this.texto_tarifa.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    this.red.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    text_reportes.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    text_historial.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro_moto));
                    this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho_moto));
                    this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte_moto));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_turno.setBackgroundTintList( ColorStateList.valueOf(ResourcesCompat.getColor(act.getResources(),
                                R.color.colorMoto, act.getTheme())));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_viaje.setBackgroundTintList( ColorStateList.valueOf(ResourcesCompat.getColor(act.getResources(),
                                R.color.colorMoto, act.getTheme())));
                    }
                    break;
                case "3":
                    this.turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));

                    this.txt_parada.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    this.texto_tarifa.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    this.red.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    text_reportes.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    text_historial.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro_taxi));
                    this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte_taxi));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_turno.setBackgroundTintList( ColorStateList.valueOf(ResourcesCompat.getColor(act.getResources(),
                                R.color.colorTaxi, act.getTheme())));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        boton_viaje.setBackgroundTintList( ColorStateList.valueOf(ResourcesCompat.getColor(act.getResources(),
                                R.color.colorTaxi, act.getTheme())));
                    }
                    break;
            }


        } else {
            this.boton_cuatro.setEnabled(false);
            this.boton_ocho.setEnabled(false);
            this.boton_cta_cte.setEnabled(false);
            this.boton_viaje.setEnabled(false);
            this.buttonParadas.setEnabled(false);
            this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.cuatro_gris));
            this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.ocho_gris));
            this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.cta_cte_gris));
            text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorGris));
            text_historial.setTextColor(act.getResources().getColor(R.color.colorGris));
            text_reportes.setTextColor(act.getResources().getColor(R.color.colorGris));
            text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorGris));

        }

        if (l_turno_app.equals("1")) {

            if (ls_id_turno.equals("0")) {

                this.boton_viaje.setEnabled(false);

            } else {
                this.boton_viaje.setEnabled(true);
            }
        } else {
            this.turno.setVisibility(View.INVISIBLE);
            this.boton_viaje.setEnabled(true);

        }

        if (l_impresion.equals("0")) {

            boton_impresora.setVisibility(View.GONE);
            this.impresora.setVisibility(View.GONE);
        } else {
            boton_impresora.setVisibility(View.VISIBLE);
            this.impresora.setVisibility(View.VISIBLE);
        }

        if (l_paradas.equals("0")) {
            txt_parada.setVisibility(View.GONE);
            buttonParadas.setVisibility(View.GONE);

        } else {
            txt_parada.setVisibility(View.VISIBLE);
            buttonParadas.setVisibility(View.VISIBLE);
        }
        if (l_turno_app.equals("0")) {


            repetirTicket.setBackground(act.getResources().getDrawable(R.drawable.boton));
            boton_dos.setEnabled(false);
            boton_dos.setBackground(act.getResources().getDrawable(R.drawable.dos_gris));
            boton_tres.setEnabled(false);
            boton_tres.setBackground(act.getResources().getDrawable(R.drawable.tres_gris));
            boton_uno.setEnabled(false);
            boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
            text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
            text_final_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
            text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorGris));

        } else {

            repetirTicket.setBackground(act.getResources().getDrawable(R.drawable.boton_chico));
            boton_dos.setEnabled(true);
            boton_uno.setEnabled(true);
            switch (tipo_empresa) {
                case "1":
                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos));
                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno));
                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres));
                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                    break;
                case "2":
                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_moto));
                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_moto));
                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_moto));
                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorMoto));
                    break;
                case "3":
                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_taxi));
                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_taxi));
                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_taxi));
                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                    break;
            }
        }



        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.everblue);

        this.boton_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();
                    String l_mensaje_rendicion;
                    if(tipo_rendicion.equals("2")){
                        if(saldo_vehiculo.equals("0.00")){
                            l_mensaje_rendicion = "No tiene Saldo para iniciar el viaje";
                        }else{
                            l_mensaje_rendicion = "";
                        }
                    }else{
                        l_mensaje_rendicion = "";
                    }
                    if(l_mensaje_rendicion.equals("")) {
                        viaje_automatico(context);
                        boton_viaje.setEnabled(false);
                    }else{
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        alertDialogBuilder.setTitle("Callisto");

                        alertDialogBuilder
                                .setMessage(l_mensaje_rendicion)
                                .setCancelable(false)
                                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                    }
                                }).create().show();
                    }
                }else{
                    sin_internet();
                }
            }
        });


        this.boton_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();
                    setClickToChat(v,telefono_base);
                }else{
                    sin_internet();
                }

            }
        });

        this.boton_cta_cte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();
                    Intent intent2 = new Intent(context, MainCtaCte.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }else{
                    sin_internet();
                }

            }
        });

        this.gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                habilitar_gps();

            }
        });



        this.boton_uno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();

                    datos_turno(context);
                }else{
                    sin_internet();
                }
            }
        });

        this.boton_dos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();

                    lb_ultimo = true;
                    ultimo_turno(context);
                }else{
                    sin_internet();
                }
            }
        });

        this.boton_turno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();
                    if(ls_id_turno.equals("0")) {
                        verificar_movil_turno(context);
                    }else{
                        cerrar_turno(context);
                    }
                }else{
                    sin_internet();
                }
            }
        });

        this.boton_tres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();

                    cargarDatos(context);
                }else{
                    sin_internet();
                }
            }
        });

        this.boton_cuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();

                    datos_ultimos_viajes(context);
                }else{
                    sin_internet();
                }
            }
        });

        this.boton_impresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("tipo_ventana","main");
                editor.apply();
                if(mBound) {
                    act.unbindService(connection);
                    impresora.setTextColor(act.getResources().getColor(R.color.alarma));
                    mBound = false;
                }
                if(!mBound) {
                    if(lb_bluetooth) {
                        cargarImpresora(context);
                    }
                }
            }
        });


        this.repetirTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();

                    boton_repetirTicket(context);
                }else{
                    sin_internet();
                }
            }
        });

        this.buttonParadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();
                    cargarIdVehiculo(context);
                }else{
                    sin_internet();
                }
            }
        });

        this.boton_ocho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificar_internet()) {
                    mediaPlayer.start();
                    Intent intent2;
                    if(l_turno_app.equals("0")){
                        intent2 = new Intent(context, viajes_activity.class);
                    }else {
                        intent2 = new Intent(context, turnos_activity.class);
                    }
                    context.startActivity(intent2);
                }else{
                    sin_internet();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("swipe","swipe");

                swipeRefreshLayout.setRefreshing(true);

                if(verificar_internet()) {
                    switch (tipo_empresa) {
                        case "1":
                            red.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                            break;
                        case "2":
                            red.setTextColor(act.getResources().getColor(R.color.colorMoto));
                            break;
                        case "3":
                            red.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                            break;
                    }
                    feriado(context);
                } else{
                    swipeRefreshLayout.setRefreshing(false);
                    red.setTextColor(act.getResources().getColor(R.color.alarma));
                }


            }
        });

        if(verificar_internet()) {
            feriado(context);
        }


        return v;
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

    public void sin_internet()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);

                        alertDialogBuilder.setTitle("Parece que no hay internet");

                        alertDialogBuilder
                                .setMessage("Compruebe su conexión a internet para seguir utilizando la app")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        red.setTextColor(act.getResources().getColor(R.color.alarma));
                                    }
                                })

                                .create().show();
                    }
                }
        );
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

    public void feriado( final Context context){
        String newURL = Constantes.GET_FERIADO;
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

                                procesarRespuestaFeriado(response, context);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley feriado: " + error.getMessage());

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

            procesarParametroTurno();
            if (!l_paradas.equals("0")) {
                cargarParadas(context);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean gps_habilitado(){
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            return true;
        }
        return false;
    }

    private boolean red_habilitada(){
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            return true;
        }
        return false;
    }


private void procesarParametroTurno( ) {

    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(
            new Runnable() {
                @Override
                public void run() {
                    if (l_turno_app.equals("0")) {

                        boton_turno.setEnabled(false);

                        boton_dos.setEnabled(false);
                        boton_tres.setEnabled(false);
                        boton_dos.setBackground(act.getResources().getDrawable(R.drawable.dos_gris));
                        boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
                        boton_tres.setBackground(act.getResources().getDrawable(R.drawable.tres_gris));
                        text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_final_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorGris));

                    } else {
                        boton_turno.setEnabled(true);

                        boton_dos.setEnabled(true);
                        switch (tipo_empresa) {
                            case "1":
                                boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos));
                                boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno));
                                boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres));
                                text_final_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "2":
                                boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_moto));
                                boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_moto));
                                boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_moto));
                                text_final_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                break;
                            case "3":
                                boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_taxi));
                                boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_taxi));
                                boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_taxi));
                                text_final_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                break;
                        }

                    }
                    c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    String getCurrentDateTime = sdf.format(c.getTime());
                    SimpleDateFormat shoy = new SimpleDateFormat("MM/dd/yyyy");
                    l_hoy = shoy.format(c.getTime());
                    String getMyTime = l_hoy + ' ' + l_hora_desde;

                    if (getCurrentDateTime.compareTo(getMyTime) > 0) {
                        l_nocturno = "1";
                    } else {
                        getMyTime = l_hoy + ' ' + l_hora_hasta;
                        if (getCurrentDateTime.compareTo(getMyTime) < 0) {
                            l_nocturno = "1";
                        } else {
                            int dia_semana;
                            dia_semana = c.get(Calendar.DAY_OF_WEEK);

                            if (dia_semana == Calendar.SUNDAY) {
                                l_nocturno = "1";
                            } else {
                                if (ls_es_feriado.equals("si")) {
                                    l_nocturno = "1";
                                } else {
                                    l_nocturno = "0";
                                }

                            }
                        }
                    }

                    if (l_nocturno.equals("0")) {
                        texto_tarifa.setText(R.string.diurno);
                    } else {
                        texto_tarifa.setText(R.string.nocturno);
                    }

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("nocturno", l_nocturno);
                    editor.apply();
                    datos_turno_inicial(context);

                }
            }
    );

}


    public void datos_turno(final Context context) {

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
                    hora_fin = object.getString("hora_fin");
                    if(!object.getString("distancia").equals("null")){
                        kms =object.getString("distancia");}
                    if(!object.getString("recaudacion").equals("null")){
                        recaudacion = object.getString("recaudacion");}
                    estado = object.getString("estado");
                    l_nro_turno = object.getString("nro_turno");
                    String habilitada = object.getString("habilitada");

                    if(habilitada.equals("1") && movil_habilitado.equals("1") && chofer_habilitado.equals("1") )
                    {

                        this.boton_ocho.setEnabled(true);
                        this.boton_cta_cte.setEnabled(true);
                        this.boton_cuatro.setEnabled(true);
                        this.boton_viaje.setEnabled(true);
                        this.buttonParadas.setEnabled(true);
                        switch (tipo_empresa) {
                            case "1":
                                this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho));
                                this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro));
                                this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte));
                                this.text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                this.text_historial.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                this.text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "2":
                                this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho_moto));
                                this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte_moto));
                                this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro_moto));
                                this.text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                this.text_historial.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                this.text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                break;
                            case "3":
                                this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho_taxi));
                                this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte_taxi));
                                this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro_taxi));
                                this.text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                this.text_historial.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                this.text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                break;
                        }

                        if(l_turno_app.equals("0")){

                            boton_turno.setEnabled(false);
                            //boton_turno.setBackgroundColor(act.getResources().getColor(R.color.colorGris));
                            boton_dos.setEnabled(false);
                            boton_dos.setBackground(act.getResources().getDrawable(R.drawable.dos_gris));
                            text_final_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                            boton_uno.setEnabled(false);
                            text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                            boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
                            boton_tres.setEnabled(false);
                            boton_tres.setBackground(act.getResources().getDrawable(R.drawable.tres_gris));
                            text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorGris));
                            boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.cta_cte_gris));
                            text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorGris));
                        }else{
                            boton_turno.setEnabled(false);
                            boton_dos.setEnabled(true);
                            boton_uno.setEnabled(true);
                            boton_tres.setEnabled(true);
                            switch (tipo_empresa) {
                                case "1":
                                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos));
                                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno));
                                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres));
                                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                    break;
                                case "2":
                                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_moto));
                                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_moto));
                                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_moto));
                                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                    break;
                                case "3":
                                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_taxi));
                                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_taxi));
                                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_taxi));
                                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                    break;
                            }

                        }

                        datos_viajes_turno(context);

                    }else{
                        this.boton_uno.setEnabled(false);
                        this.boton_dos.setEnabled(false);
                        this.boton_tres.setEnabled(false);
                        this.boton_cuatro.setEnabled(false);
                        this.boton_ocho.setEnabled(false);
                        this.boton_cta_cte.setEnabled(false);
                        this.boton_viaje.setEnabled(false);
                        this.buttonParadas.setEnabled(false);
                        this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
                        this.boton_dos.setBackground(act.getResources().getDrawable(R.drawable.dos_gris));
                        this.boton_tres.setBackground(act.getResources().getDrawable(R.drawable.tres_gris));
                        this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.cuatro_gris));
                        this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.ocho_gris));
                        this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.cta_cte_gris));
                        text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_final_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                        this.text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_historial.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorGris));


                    }


                case "2":
                    Log.d(TAG, "Error turno " );
                    Toast.makeText(
                            context,
                            "No hay turno iniciado",
                            Toast.LENGTH_LONG).show();

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void datos_viajes_turno(final Context context) {

        String l_turno;
        if(lb_ultimo){
            l_turno = ls_id_turno_ultimo;
        }else{
            l_turno = ls_id_turno;
        }

        String newURL = Constantes.GET_VIAJES_TURNO + "?turno=" + l_turno;
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

                        String nro_recibo = object.getString("nro_recibo");

                        via.setNro_recibo(nro_recibo);

                        viajes.add(via);
                    }

                    break;

            }

            if(lb_ultimo){
                if(mBound) {
                    ticket_turno(viajes);
                }else{
                    crear_pfd_turno(viajes);
                }
                lb_ultimo = false;
            }else {
                if (mBound) {
                    if (estado.equals("1")) {
                        ticket_turno_parcial();
                    } else {
                        ticket_turno(viajes);

                    }
                }else{
                    if (estado.equals("1")) {
                        crear_pfd_ticket_turno_parcial();
                    } else {
                        crear_pfd_turno(viajes);

                    }
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

                Locale locale2 = new Locale ("es", "ES");
                NumberFormat objNF2 = NumberFormat.getInstance (locale2);
                objNF2.setMinimumFractionDigits(2);
                objNF2.setMaximumFractionDigits(2);

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(nombre_remiseria, 2, 1);
                printNewLine();
                printCustom(act.getResources().getString(R.string.parcial_turno), 1, 0); // total 32 char in a single line

                printNewLine();
                printText("Nro: " + l_nro_turno);//nro
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
                printText(objNF2.format(Double.valueOf(recaudacion)));
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

    private void crear_pfd_ticket_turno_parcial()
    {
        directorio();
        pfd_ticket_turno_parcial( );
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

    private Boolean pfd_ticket_turno_parcial()
    {
        boolean success = false;
        PdfPCell cell;


        //saldo=saldo.replace("\n","");
        //create document file
        Document doc = new Document(PageSize.A5, 14f, 10f, 10f, 10f);
        try {
            Locale locale2 = new Locale ("es", "ES");
            NumberFormat objNF2 = NumberFormat.getInstance (locale2);
            objNF2.setMinimumFractionDigits(2);
            objNF2.setMaximumFractionDigits(2);
            doc.left(10f);
            //doc.top(15f);
            file = new File(dir, "ticket_turno_parcial.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);

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

            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.parcial_turno),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla_enc.addCell(cell);


            doc.add(tabla_enc);

            LineSeparator lineSeparator = new LineSeparator();

            lineSeparator.setLineColor(new BaseColor(255, 255, 255, 68));

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));

            PdfPTable tabla = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("Nro: " + l_nro_turno,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla.addCell(cell);

            cell = new PdfPCell(new Phrase(fecha + " - " + hora_inicio,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla.addCell(cell);

            doc.add(tabla);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));

            PdfPTable tabla1 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("K.TOTAL:  ",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla1.addCell(cell);

            cell = new PdfPCell(new Phrase(kms,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla1.addCell(cell);

            doc.add(tabla1);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));

            PdfPTable tabla2 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("RECAUDACION: ",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla2.addCell(cell);

            cell = new PdfPCell(new Phrase(objNF2.format(Double.valueOf(recaudacion)),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla2.addCell(cell);

            doc.add(tabla2);


        } catch (DocumentException | IOException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } finally {
            doc.close();

            success = true;
        }

        return success;

    }


    private void cerrar_turno(final Context context){

        String newURL = Constantes.FIN_TURNO + "?id=" + ls_id_conductor;
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
                        Map<String, String> headers = new TreeMap<>();
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
                    turno.setText(act.getResources().getString(R.string.turno_finalizado) );
                    this.boton_turno.setText(act.getResources().getString(R.string.nuevo_turno));
                    ls_id_turno = "0";

                    this.boton_viaje.setEnabled(false);

                    this.boton_uno.setEnabled(false);
                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                    this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));


                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    editor.putString("id_turno_chofer",ls_id_turno);
                    editor.apply();
                    if(mBound) {
                        datos_turno(context);
                    }
                    break;
                case "2":
                    Log.d(TAG, "Error cerrar turno " );
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

    private void ultimo_turno(final Context context){

        String newURL = Constantes.GET_ULTIMO_TURNO + "?conductor=" + ls_id_conductor;
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
                                procesarRespuestaUltimoTurno(response, context);
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
                        Map<String, String> headers = new TreeMap<>();
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
    private void procesarRespuestaUltimoTurno(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje

            switch (estado) {
                case "1":

                    JSONArray mensaje1 = response.getJSONArray("turno");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    fecha = object.getString("fecha");
                    hora_inicio = object.getString("hora_inicio");
                    hora_fin = object.getString("hora_fin");
                    if(!object.getString("distancia").equals("null")){
                        kms =object.getString("distancia");}
                    if(!object.getString("recaudacion").equals("null")){
                        recaudacion = object.getString("recaudacion");}
                    estado = object.getString("estado");
                    l_nro_turno = object.getString("nro_turno");

                    ls_id_turno_ultimo = object.getString("id_turno");

                    datos_viajes_turno(context);

                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            "Error en fin de turno",
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verificar_movil_turno(final Context context){

        TreeMap<String, String> map = new TreeMap<>();// Mapeo previo

        map.put("conductor", ls_id_conductor);
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
        String newURL = Constantes.VERIFICAR_MOVIL_TURNO + "?" + encodedParams;

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
                                procesarRespuestamovil_turno(response, context);
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
                        Map<String, String> headers = new TreeMap<>();
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
    private void procesarRespuestamovil_turno(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje

            switch (estado) {
                case "1":

                    JSONArray datos_parametro = response.getJSONArray("conductor");
                    String l_cantidad = "0";
                    for(int i = 0; i < datos_parametro.length(); i++)
                    {JSONObject object = datos_parametro.getJSONObject(i);

                        l_cantidad = object.getString("cantidad");

                    }
                    if(l_cantidad.equals("0")) {
                        obtenerNroTurno(context);
                    }else{
                        Toast.makeText(
                                context,
                                "Hay otro chofer con turno iniciado en su móvil",
                                Toast.LENGTH_LONG).show();
                    }

                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            "Error en fin de turno",
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void obtenerNroTurno(final Context context) {

        TreeMap<String, String> map = new TreeMap<>();// Mapeo previo

        map.put("parametro", "15");
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

                        l_nro_turno = object.getString("valor");

                    }
                    Long l_turno = 0L;

                    l_turno = Long.parseLong(l_nro_turno) + 1;

                    l_turno_parametro = String.valueOf(l_turno);
                    l_nro_turno = l_turno_parametro;

                    actualizar_nro_turno(context);

                    break;
                case "2":
                    Log.d(TAG, "Error nro turno " );
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

    private void actualizar_nro_turno(final Context context){


        TreeMap<String, String> map = new TreeMap<>();// Mapeo previo

        map.put("parametro", "15");
        map.put("remiseria", ls_remiseria);
        map.put("valor", l_turno_parametro);

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
                                procesarRespuesta_actualizar_nro_turno(response, context);
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
                        Map<String, String> headers = new TreeMap<>();
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
    private void procesarRespuesta_actualizar_nro_turno(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    iniciar_turno( context);
                    break;
                case "2":
                    // Mostrar mensaje
                    Log.d(TAG, "actualizar nro " );
                    Toast.makeText(
                            context,
                            "nro turno",
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                                procesarRespuestaActualizar(response, context);
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
                        Map<String, String> headers = new TreeMap<>();
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
                    cargarTurno(context);
                    break;
                case "2":
                    Log.d(TAG, "actualizar nro " );
                    Toast.makeText(
                            context,
                            "carga turno",
                            Toast.LENGTH_LONG).show();
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

                    String l_fecha = object.getString("fecha");
                    String l_hora_inicio = object.getString("hora_inicio");

                    turno.setText("TURNO INICIADO - " + l_fecha + " - " + l_hora_inicio);
                    this.boton_turno.setText(act.getResources().getString(R.string.cerrar_turno));
                    this.boton_turno.setText(act.getResources().getString(R.string.cerrar_turno));
                    this.boton_viaje.setEnabled(true);


                    this.boton_uno.setEnabled(true);

                    switch (tipo_empresa) {
                        case "1":
                            this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno));
                            text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                            break;
                        case "2":
                            this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_moto));
                            text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                            break;
                        case "3":
                            this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_taxi));
                            text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                            break;
                    }

                    actualizar_turno(context);

                    break;

                case "2":
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void actualizar_turno(final Context context){


        TreeMap<String, String> map = new TreeMap<>();// Mapeo previo

        map.put("id", ls_id_turno);
        map.put("nro_turno", l_turno_parametro);

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


        String newURL = Constantes.UPDATE_NRO_TURNO + "?" + encodedParams;
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
                        Map<String, String> headers = new TreeMap<>();
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
                    Log.d(TAG, "1 actualizar turno " );
                    Toast.makeText(
                            context,
                            "Turno Iniciado",
                            Toast.LENGTH_LONG).show();
                    break;
                case "2":

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarDatos(final Context context) {

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
                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley turnos: " + error.getMessage());

                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta(JSONObject response) {
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
                    }else{
                        crear_pfd_ticket_recaudacion(datos);
                    }
                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public void cargarParadas(final Context context) {

        String newURL = Constantes.GET_PARADAS_REMISERIA + "?remiseria=" + ls_remiseria;
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
                                procesarRespuestaParadas(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley parametro: " + error.getMessage());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaParadas(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            paradas.clear();

            switch (mensaje) {
                case "1":
                    JSONArray datos_parada = response.getJSONArray("parada");

                    for(int i = 0; i < datos_parada.length(); i++)
                    {
                        JSONObject object = datos_parada.getJSONObject(i);

                        parada par = new parada();

                        String id = object.getString("id");

                        par.setId(id);

                        String descripcion = object.getString("descripcion");

                        par.setDescripcion(descripcion);

                        String latitud = object.getString("latitud");

                        par.setLatitud(latitud);

                        String longitud = object.getString("longitud");

                        par.setLongitud(longitud);

                        paradas.add(par);


                    }
                    break;

            }

            cargarParada(context);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarParada(final Context context) {

        String newURL = Constantes.GET_PARADA + "?conductor=" + ls_id_conductor;
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
                                procesarRespuestaParada(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley parametro: " + error.getMessage());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuestaParada(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parada = response.getJSONArray("parada");

                    for(int i = 0; i < datos_parada.length(); i++)
                    {
                        JSONObject object = datos_parada.getJSONObject(i);

                        txt_parada.setText("Parada Nro: " + object.getString("id"));
                    }

                    break;


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void datos_turno_inicial(final Context context) {

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
                                procesarRespuesta_inicial(response, context);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley turno: " + error.getMessage());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                )
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void procesarRespuesta_inicial(JSONObject response, Context v) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("conductor");
                    JSONObject object = mensaje1.getJSONObject(0);

                    SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor editor = settings1.edit();

                    String habilitada = object.getString("habilitada");
                    saldo_vehiculo = object.getString("saldo_movil");

                    if(habilitada.equals("1") && movil_habilitado.equals("1") && chofer_habilitado.equals("1") )
                    {
                        this.boton_ocho.setEnabled(true);
                        this.boton_cuatro.setEnabled(true);
                        if(viajes_automaticos.equals("1") && viajes_automaticos_chofer.equals("1")) {
                            this.boton_viaje.setVisibility(View.VISIBLE);
                        }
                        this.buttonParadas.setEnabled(true);
                        this.boton_uno.setEnabled(true);
                        this.boton_cta_cte.setEnabled(true);
                        this.repetirTicket.setVisibility(View.VISIBLE);
                        this.boton_turno.setVisibility(View.VISIBLE);

                        switch (tipo_empresa) {
                            case "1":
                                this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho));
                                this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte));
                                this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro));
                                this.text_historial.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                this.text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "2":
                                this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho_moto));
                                this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte_moto));
                                this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro_moto));
                                this.text_historial.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                this.text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                break;
                            case "3":
                                this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.selector_ocho_taxi));
                                this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.selector_cta_cte_taxi));
                                this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.selector_cuatro_taxi));
                                this.text_historial.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                this.text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                break;
                        }

                        if(l_turno_app.equals("0")){

                            boton_turno.setEnabled(false);
                            boton_dos.setEnabled(false);
                            text_final_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                            boton_dos.setBackground(act.getResources().getDrawable(R.drawable.dos_gris));

                            boton_uno.setEnabled(false);
                            boton_tres.setEnabled(false);
                            text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                            boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
                            text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorGris));

                        }else{
                            boton_turno.setEnabled(true);
                            boton_dos.setEnabled(true);
                            boton_uno.setEnabled(true);
                            boton_tres.setEnabled(true);
                            switch (tipo_empresa) {
                                case "1":
                                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos));
                                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno));
                                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres));
                                    break;
                                case "2":
                                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_moto));
                                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_moto));
                                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorMoto));
                                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_moto));
                                    break;
                                case "3":
                                    boton_dos.setBackground(act.getResources().getDrawable(R.drawable.selector_dos_taxi));
                                    text_final_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                    boton_uno.setBackground(act.getResources().getDrawable(R.drawable.selector_uno_taxi));
                                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                    text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                                    boton_tres.setBackground(act.getResources().getDrawable(R.drawable.selector_tres_taxi));
                                    break;
                            }

                        }


                        ls_id_turno = object.getString("id");

                        editor.putString("id_turno_chofer",ls_id_turno);
                        editor.apply();

                        String l_fecha = object.getString("fecha");
                        String l_hora_inicio = object.getString("hora_inicio");
                        String l_nro_turno = object.getString("nro_turno");

                        turno.setText("TURNO INICIADO - " + l_fecha + " - " + l_hora_inicio);
                        this.boton_turno.setText(act.getResources().getString(R.string.cerrar_turno));
                        this.boton_viaje.setEnabled(true);


                        if(l_impresion.equals("1")) {
                            if (lb_bluetooth) {
                                cargarImpresora(context);
                            }
                        }
                    }else {
                        this.boton_uno.setEnabled(false);
                        this.boton_dos.setEnabled(false);
                        this.boton_tres.setEnabled(false);
                        this.boton_cuatro.setEnabled(false);
                        this.boton_ocho.setEnabled(false);
                        this.boton_cta_cte.setEnabled(false);
                        this.boton_viaje.setVisibility(View.GONE);
                        this.buttonParadas.setEnabled(false);
                        this.boton_turno.setEnabled(false);
                        this.repetirTicket.setVisibility(View.GONE);
                        text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_final_turno.setTextColor(act.getResources().getColor(R.color.colorGris));

                        this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
                        this.boton_dos.setBackground(act.getResources().getDrawable(R.drawable.dos_gris));
                        this.boton_tres.setBackground(act.getResources().getDrawable(R.drawable.tres_gris));
                        this.boton_cuatro.setBackground(act.getResources().getDrawable(R.drawable.cuatro_gris));
                        this.boton_ocho.setBackground(act.getResources().getDrawable(R.drawable.ocho_gris));
                        this.boton_cta_cte.setBackground(act.getResources().getDrawable(R.drawable.cta_cte_gris));
                        this.text_ultimos_finales.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_viaje_x_viaje.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_historial.setTextColor(act.getResources().getColor(R.color.colorGris));
                        text_cta_cte.setTextColor(act.getResources().getColor(R.color.colorGris));


                    }
                    break;
                case "2":

                    Toast.makeText(
                            context,
                            "No hay turno iniciado",
                            Toast.LENGTH_LONG).show();
                    turno.setText(act.getResources().getString(R.string.turno_finalizado) );
                    this.boton_turno.setText(act.getResources().getString(R.string.nuevo_turno));
                    this.boton_viaje.setEnabled(false);

                    this.boton_uno.setEnabled(false);
                    text_parcial_turno.setTextColor(act.getResources().getColor(R.color.colorGris));
                    this.boton_uno.setBackground(act.getResources().getDrawable(R.drawable.uno_gris));
                    break;

            }
            swipeRefreshLayout.setRefreshing(false);


        } catch (JSONException e) {
            e.printStackTrace();
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
                                esperarYCerrar(1500, intent, context);

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

    /**
     * Finaliza la aplicación
     */
    public void bindApp(Intent intent, Context context) {
        Log.d("impresora", "bind");
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if(impresion.getBluetoothAdapter() !=null) {
            if (impresion.getOutputStream() != null) {
                switch (tipo_empresa) {
                    case "1":
                        impresora.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                        break;
                    case "2":
                        impresora.setTextColor(act.getResources().getColor(R.color.colorMoto));
                        break;
                    case "3":
                        impresora.setTextColor(act.getResources().getColor(R.color.colorTaxi));
                        break;
                }
                mBound = true;
            } else {
                impresora.setTextColor(act.getResources().getColor(R.color.alarma));
                mBound = false;

            }
        }else{
            impresora.setTextColor(act.getResources().getColor(R.color.alarma));
            mBound = false;
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

                Locale locale2 = new Locale ("es", "ES");
                NumberFormat objNF2 = NumberFormat.getInstance (locale2);
                objNF2.setMinimumFractionDigits(2);
                objNF2.setMaximumFractionDigits(2);

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(nombre_remiseria, 2, 1);
                printNewLine();
                printCustom("Tel. Empresa: " + telefono_remiseria, 1, 1);

                printNewLine();
                printText(stringABytes(act.getResources().getString(R.string.ticket_recaudacion))); // total 32 char in a single line

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
                    printText("TOTAL:  " + objNF2.format(Double.valueOf(importe)));
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

    private void crear_pfd_ticket_recaudacion(ArrayList<turno> datos)
    {
        directorio();
        pfd_ticket_recaudacion( datos );
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

    private Boolean pfd_ticket_recaudacion(ArrayList<turno> turnos)
    {
        boolean success = false;
        PdfPCell cell;


        //saldo=saldo.replace("\n","");
        //create document file
        Document doc = new Document(PageSize.A5, 14f, 10f, 10f, 10f);
        try {
            doc.left(10f);
            //doc.top(15f);
            file = new File(dir, "ticket_recaudacion.pdf");
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

            cell = new PdfPCell(new Phrase("Tel. Empresa: " + telefono_remiseria,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla_enc.addCell(cell);

            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.ticket_recaudacion),font));
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

            String id;
            String fecha;
            String hora_inicio;
            String hora_fin;
            String importe;
            for (turno Turno : turnos) {

                id = Turno.getId();
                fecha = Turno.getFecha();
                hora_inicio = Turno.getHora_inicio();
                hora_fin = Turno.getHora_fin();

                cell = new PdfPCell(new Phrase("TURNO " + id,font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);
                cell = new PdfPCell(new Phrase("Fecha " + fecha,font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                cell = new PdfPCell(new Phrase("Hora Inicio " + hora_inicio,font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                if (!hora_fin.equals("null")) {
                    cell = new PdfPCell(new Phrase("Hora Fin " + hora_fin,font));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    table1.addCell(cell);
                }
                importe = Turno.getRecaudacion();
                cell = new PdfPCell(new Phrase("TOTAL:  " + objNF2.format(Double.valueOf(importe)),font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);


                cell = new PdfPCell(new Phrase("-------------------------",font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                cell = new PdfPCell(new Phrase("",font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

            }
            doc.add(table1);

        } catch (DocumentException | IOException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } finally {
            doc.close();

            success = true;
        }

        return success;

    }

    protected void ticket_turno( ArrayList<viaje> viajes) {

        outputStream = impresion.getOutputStream();
        Locale locale2 = new Locale ("es", "ES");
        NumberFormat objNF2 = NumberFormat.getInstance (locale2);
        objNF2.setMinimumFractionDigits(2);
        objNF2.setMaximumFractionDigits(2);

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
                printCustom(act.getResources().getString(R.string.empresa), 2, 1);
                printNewLine();
                printPhoto(R.drawable.remisluna_logo_impresion);
                printCustom(act.getResources().getString(R.string.telefono), 1, 1);

                printNewLine();
                printUnicode();
                printNewLine();
                printText(act.getResources().getString(R.string.ticket_turno)); // total 32 char in a single line

                printNewLine();
                printText("Nro: " + l_nro_turno);//nro
                printNewLine();
                printText("Fecha: " + fecha);//fecha
                printNewLine();
                printText("Inicio: " + hora_inicio);
                printText(" - ");
                printText("Fin: " + hora_fin);
                printNewLine();
                printNewLine();
                printUnicode();
                printNewLine();

                String id;
                String importe;

                for (viaje Viaje : viajes) {
                    id = Viaje.getId();
                    printCustom("VIAJE " + id + " - " + Viaje.getNro_recibo(),  1, 0);

                    importe = Viaje.getImporte();
                    printText("TOTAL:  " + objNF2.format(Double.valueOf(importe)));
                    printNewLine();
                }
                printNewLine();
                printText("K.TOTAL:  ");
                printText(kms);
                printNewLine();
                printNewLine();
                printText("FIN TURNO: ");
                printText(objNF2.format(Double.valueOf(recaudacion)));
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

    private void crear_pfd_turno(ArrayList<viaje> viajes)
    {
        directorio();
        pdf_turno(viajes);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(FileProvider.getUriForFile(context, act.getPackageName() + ".my.package.name.provider", file), "application/pdf");
        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(target);
        } catch (ActivityNotFoundException e) {
            Intent intent = Intent.createChooser(target, "Open File");
            act.startActivity(intent);
        }
    }

    private Boolean pdf_turno(ArrayList<viaje> viajes)
    {
        boolean success = false;
        PdfPCell cell;


        //saldo=saldo.replace("\n","");
        //create document file
        Document doc = new Document(PageSize.A5, 14f, 10f, 10f, 10f);
        try {
            doc.left(10f);
            //doc.top(15f);
            file = new File(dir, "fin_turno-"+ l_nro_turno +".pdf");
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

            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.ticket_turno),font));
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


            cell = new PdfPCell(new Phrase("Nro: " + l_nro_turno,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Fecha: " + fecha,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Inicio: " + hora_inicio,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase("Fin: " + hora_fin,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            doc.add(table1);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table2 = new PdfPTable(1);

            String id;
            String importe;

            for (viaje Viaje : viajes) {
                id = Viaje.getId();

                cell = new PdfPCell(new Phrase("VIAJE " + id + " - " + Viaje.getNro_recibo(),font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table2.addCell(cell);

                importe = Viaje.getImporte();

                cell = new PdfPCell(new Phrase("TOTAL:  " + objNF2.format(Double.valueOf(importe)),font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table2.addCell(cell);
            }

            doc.add(table2);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("K.TOTAL:  ",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(kms,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("FIN TURNO: ",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(objNF2.format(Double.valueOf(recaudacion)),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);


            doc.add(table);


        } catch (DocumentException | IOException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } finally {
            doc.close();

            success = true;
        }

        return success;

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

                        String nro_recibo = object.getString("nro_recibo");

                        via.setNro_recibo(nro_recibo);

                        viajes.add(via);
                    }

                    break;

            }

            if(mBound) {
                ticket_ultimos_viajes(viajes);
            }else{
                crear_pfd_ultimos_viajes(viajes);
            }


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
                Locale locale2 = new Locale ("es", "ES");
                NumberFormat objNF2 = NumberFormat.getInstance (locale2);
                objNF2.setMinimumFractionDigits(2);
                objNF2.setMaximumFractionDigits(2);

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                printCustom(nombre_remiseria, 2, 1);
                printNewLine();
                printCustom("Tel. Empresa: " + telefono_remiseria, 1, 1);

                printNewLine();
                printUnicode();
                printNewLine();
                printText(act.getResources().getString(R.string.ticket_ultimos_viajes)); // total 32 char in a single line
                printNewLine();

                String id;
                String importe;
                String fecha;
                String hora;
                Double l_total = 0.00;

                for (viaje Viaje : viajes) {
                    id = Viaje.getNro_recibo();
                    printCustom("VIAJE " + id, 1, 0);

                    fecha = Viaje.getFecha();
                    printText("Fecha:  " + fecha);

                    hora = Viaje.getHora_inicio();
                    printNewLine();
                    printText("Hora Inicio:  " + hora);

                    importe = Viaje.getImporte();
                    printNewLine();
                    printText("Importe:  " + objNF2.format(Double.valueOf(importe)));
                    l_total = l_total + Double.parseDouble(importe);
                    printNewLine();
                    printNewLine();
                }
                printNewLine();
                printText("TOTAL: ");
                printText(objNF2.format(Double.valueOf(l_total)));
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

    private void crear_pfd_ultimos_viajes(ArrayList<viaje> viajes)
    {
        directorio();
        pfd_ultimos_viajes( viajes );
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

    private Boolean pfd_ultimos_viajes(ArrayList<viaje> viajes)
    {
        boolean success = false;
        PdfPCell cell;


        //saldo=saldo.replace("\n","");
        //create document file
        Document doc = new Document(PageSize.A5, 14f, 10f, 10f, 10f);
        try {
            doc.left(10f);
            //doc.top(15f);
            file = new File(dir, "ultimos_viajes.pdf");
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

            cell = new PdfPCell(new Phrase("Tel. Empresa: " + telefono_remiseria,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tabla_enc.addCell(cell);

            cell = new PdfPCell(new Phrase(act.getResources().getString(R.string.ticket_ultimos_viajes),font));
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

            String id;
            String importe;
            String l_fecha;
            String hora;
            Double l_total = 0.00;
            for (viaje Viaje : viajes) {
                id = Viaje.getNro_recibo();

                cell = new PdfPCell(new Phrase("VIAJE " + id,font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                l_fecha = Viaje.getFecha();

                cell = new PdfPCell(new Phrase("Fecha " + l_fecha,font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                hora = Viaje.getHora_inicio();

                cell = new PdfPCell(new Phrase("Hora Inicio " + hora,font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                importe = Viaje.getImporte();

                cell = new PdfPCell(new Phrase("Importe:  " + objNF2.format(Double.valueOf(importe)),font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);


                cell = new PdfPCell(new Phrase("-------------------------",font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);

                cell = new PdfPCell(new Phrase("",font));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                table1.addCell(cell);
                l_total = l_total + Double.parseDouble(importe);

            }
            doc.add(table1);

            lineSeparator = new LineSeparator();

            lineSeparator.setLineColor(new BaseColor(255, 255, 255, 68));

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table2 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("TOTAL: ",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(objNF2.format(Double.valueOf(l_total)),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            doc.add(table2);

        } catch (DocumentException | IOException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } finally {
            doc.close();

            success = true;
        }

        return success;

    }

    private static String getTwoDecimals(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    public void boton_repetirTicket(final Context context) {

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
                    localidad_abreviada = object.getString("abreviada");
                    telefono_queja = object.getString("telefono_queja");
                    telefono_remiseria = object.getString("telefono");
                    nombre_remiseria = object.getString("remiseria");
                    chapa = object.getString("chapa");
                    patente = object.getString("patente");
                    nro_recibo = object.getString("nro_recibo");
                    precio_km = object.getString("precio_km");

                    if(mBound) {
                        repetirTicket();
                    }else{
                        crear_pfd_repetir_ticket();
                    }
                    break;
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
                printText("SALIDA:  " + hora_salida_ultimo);
                printNewLine();
                printText("DESDE:  " + salida_ultimo);
                printNewLine();
                printText("HASTA:  " + destino_ultimo);
                printNewLine();
                printText("LLEGADA:  " + hora_destino_ultimo);
                printNewLine();
                printText("RECORRIDO:  " + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(distancia_ultimo)) + " Kms.");
                printNewLine();
                printNewLine();
                printText("TARIFA AL  " + fecha_tarifa_ultimo);
                printNewLine();
                printText("BAJADA:  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(bajada_ultimo))));
                printNewLine();
                printText("VIAJE:  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(fichas_ultimo))));
                printNewLine();
                printText("ESPERA:  " + '$' + objNF2.format(Double.valueOf(Double.parseDouble(espera_ultimo))));
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

            cell = new PdfPCell(new Phrase("Tel. Empresa: " + telefono_remiseria,font));
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

    public void cargarIdVehiculo(final Context context) {

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
                                procesarRespuesta_ID(response, context);
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

    private void procesarRespuesta_ID(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("vehiculo");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    id_movil = object.getString("id");
                    actualizar_coordenadas_paradas( context);
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


    private void actualizar_coordenadas_paradas(Context context){

        Geocoder coder = new Geocoder(context);


        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName("Oroño 2352", 50);
            for(Address add : adresses){
                if (!adresses.isEmpty()) {

                    longitud_destino = String.valueOf(add.getLongitude());
                    latitud_destino = String.valueOf(add.getLatitude());
                } } }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        traer_parada (context);
    }

    public void traer_parada (final Context context) {

        double distancia = 100000.00;
        String parada = "";

        for(int i = 0; i < paradas.size(); i++) {

            double l_latitud = Double.parseDouble(latitud_destino);
            double l_longitud = Double.parseDouble(longitud_destino);

            Location locationA = new Location("punto vehiculo");

            locationA.setLatitude(l_latitud);
            locationA.setLongitude(l_longitud);

            Location locationB = new Location("punto parada");

            String latitud_parada = paradas.get(i).getLatitud();
            String longitud_parada = paradas.get(i).getLongitud();

            l_latitud = Double.parseDouble(latitud_parada);
            l_longitud = Double.parseDouble(longitud_parada);

            locationB.setLatitude(l_latitud);
            locationB.setLongitude(l_longitud);

            float distance = locationA.distanceTo(locationB) ;

            if(distance < distancia){

                distancia = distance;
                parada = paradas.get(i).getId();
            }

        }

        guardar_parada(parada, context);

    }

    private void guardar_parada(String parada, final Context context){

        TreeMap<String, String> map = new TreeMap<>();// Mapeo previo

        map.put("parada", parada);
        map.put("id", id_movil);


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
                        Map<String, String> headers = new TreeMap<>();
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
                    feriado(context);
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
                        Map<String, String> headers = new TreeMap<>();
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
                    if (l_paradas.equals("0")) {
                        comenzar_viaje();
                    }else{
                        cargarIdVehiculoParada(context);
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
                                Log.d(TAG, "Error Volley id parada: " + error.getMessage());

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

                    id_movil = object.getString("id");
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

        TreeMap<String, String> map = new TreeMap<>();// Mapeo previo
        String l_parada = "0";

        map.put("parada", l_parada);
        map.put("id", id_movil);


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
                        Map<String, String> headers = new TreeMap<>();
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
                    comenzar_viaje() ;
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

    private void comenzar_viaje(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("automatico", "1");
        editor.putString("link", "0");
        editor.putString("estado_viaje","asignado");
        editor.apply();
        Intent intent2 = new Intent(context, MainViaje.class);
        context.startActivity(intent2);
        ((MainActivity)context).locationEnd();
        act.finish();
    }


}
