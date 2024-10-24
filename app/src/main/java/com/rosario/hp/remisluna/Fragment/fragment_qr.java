package com.rosario.hp.remisluna.Fragment;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.R;

import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fragment_qr extends Fragment implements SurfaceHolder.Callback{

    private static final String TAG = datosUsuario.class.getSimpleName();
    private CameraSource cameraSource;
    private JsonObjectRequest myRequest;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    private Context context;
    Camera.Parameters params;
    private Activity act;
    private String ls_id_empresa;
    private String ls_id_sector;
    private String ls_id_solicitante;

    private Button boton_sin_qr;

    private String cantidad_ficha;
    private String cantidad_espera;
    private String importe_bajada;
    private String importe_ficha;
    private String importe_espera;
    private String l_nocturno;
    private String monto_espera;
    private String monto_ficha;
    private String monto_total;
    private String l_tarifa_cc;
    private String ls_remiseria;
    private static final int REQUEST_CODE_QR_SCAN = 101;

    Camera camera;
    private CameraManager mCameraManager;
    boolean isFlash = false;
    boolean isOn = false;
    private String mCameraId;

    private String l_id_viaje;

    private String link;


    @Override
    public void onPause(){
        super.onPause();
        //switchFlashLight(isOn);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_qr, container, false);
        cameraView = v.findViewById(R.id.camera_view);
        this.boton_sin_qr = v.findViewById(R.id.buttonsinqr);

        SurfaceHolder mHolder = cameraView.getHolder();
        mHolder.addCallback(this);
        act = getActivity();
        context = getContext();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        l_id_viaje = settings.getString("id_viaje","");
        l_nocturno = settings.getString("nocturno","");
        ls_remiseria = settings.getString("remiseria","");
        link = settings.getString("link","");

        if (camera == null) {

            camera = Camera.open();

            camera.lock();

            params = camera.getParameters();

            try {
                camera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                camera.release();
                camera = null;
            }

        }

        initQR();

        MediaPlayer mediaPlayer = MediaPlayer.create(act, R.raw.everblue);

        this.boton_sin_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.start();
                cargarViaje_sinqr(context);

            }
        });

        return v;

    }


    public void initQR() {

        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(act)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(act, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // listener de ciclo de vida de la camara

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // verificamos la version de ANdroid que sea al menos la M para mostrar
                                // el dialog de la solicitud de la camara
                                if (shouldShowRequestPermissionRationale(
                                        Manifest.permission.CAMERA)) ;
                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_CAMERA);

                            }
                        }
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.i("token", token);

                        MediaPlayer mediaPlayer = MediaPlayer.create(act, R.raw.everblue);
                        MediaPlayer mediaPlayer1 = MediaPlayer.create(act, R.raw.doorbell);

                        int li_codigo;
                        String ls_codigo;

                        try
                        {
                            li_codigo = Integer.parseInt(token.substring(0,10));
                            ls_codigo = String.valueOf(li_codigo);


                            mediaPlayer.start();
                            obtener_solicitante(ls_codigo, token);

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        synchronized (this) {
                                            wait(5000);
                                            // limpiamos el token
                                            tokenanterior = "";
                                        }
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        Log.e("Error", "Waiting didnt work!!");
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        catch(NumberFormatException e)
                        {


                            mediaPlayer1.start();

                            qr_invalido(context);

                        }

                    }
                }
            }
        });

    }

    public void qr_invalido(final Context context)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        alertDialogBuilder.setTitle("QR Callisto");

                        alertDialogBuilder
                                .setMessage("QR Inválido")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                    }
                                }).create().show();

                    }
                }
        );
    }

    public void obtener_solicitante(String ls_id, final String qr) {
        // Petición GET

        String newURL = Constantes.GET_SOLICITANTE + "?solicitante=" + ls_id;
        Log.d(TAG, newURL);
        VolleySingleton.
                getInstance(act).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                newURL,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        procesarRespuestaCarga(response, qr);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.toString());

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                        alertDialogBuilder.setTitle("QR Callisto");

                                        alertDialogBuilder
                                                .setMessage("Error al leer QR")
                                                .setCancelable(false)
                                                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {

                                                    }
                                                }).create().show();

                                    }
                                }

                        )
                );
    }
    private void procesarRespuestaCarga(JSONObject response, String qr_ini) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");

            switch (estado) {
                case "1": // EXITO

                    JSONArray mensaje = response.getJSONArray("solicitante");

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = mensaje.getJSONObject(i);

                        String qr = object.getString("qr");

                        if(qr.equals(qr_ini)){
                            ls_id_solicitante =  object.getString("id");
                            ls_id_empresa =  object.getString("id_empresa");
                            ls_id_sector =  object.getString("id_sector");
                            cargarViaje(getContext());
                        }else{

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                            alertDialogBuilder.setTitle("QR Callisto");

                            alertDialogBuilder
                                    .setMessage("El código QR está vencido o  el empleado no tiene ningún QR asignado")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {

                                        }
                                    }).create().show();
                        }


                    }

                    break;
                case "2": // FALLIDO
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("QR Callisto");

                    alertDialogBuilder
                            .setMessage("El QR no pertenece a ningún empleado")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                }
                            }).create().show();

                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }



    public void cargarViaje(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_BY_ID + "?id=" + l_id_viaje;
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

                    cantidad_espera = object.getString("fichas_espera");
                    cantidad_ficha = object.getString("fichas");

                    cargarTarifa(context);
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

    public void cargarTarifa(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TARIFA_CC + "?id_empresa=" + ls_id_empresa ;
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

                                procesarRespuestaTarifa(response, context);

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

    private void procesarRespuestaTarifa(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("tarifa");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    if(l_nocturno.equals("0")) {
                        importe_bajada = object.getString("importe_bajada");
                        importe_ficha = object.getString("importe_ficha");
                        importe_espera = object.getString("importe_espera");
                    }else{
                        importe_bajada = object.getString("importe_bajada_nocturno");
                        importe_ficha = object.getString("importe_ficha_nocturno");
                        importe_espera = object.getString("importe_espera_nocturno");
                    }

                    Double valor_ficha ;

                    valor_ficha = Double.parseDouble(importe_ficha);

                    Double cant_fichas;

                    cant_fichas = Double.parseDouble(cantidad_ficha);

                    Double ldb_monto_ficha = valor_ficha * cant_fichas;


                    Double valor_ficha_espera = 0.00 ;

                    valor_ficha_espera = Double.parseDouble(importe_espera);

                    Double cant_fichas_espera;

                    cant_fichas_espera = Double.parseDouble(cantidad_espera);

                    Double ldb_monto_ficha_espera = valor_ficha_espera * cant_fichas_espera;

                    Double ldb_bajada = Double.parseDouble(importe_bajada);

                    Double total = ldb_bajada + ldb_monto_ficha + ldb_monto_ficha_espera;

                    monto_espera = String.valueOf(ldb_monto_ficha_espera);
                    monto_ficha = String.valueOf(ldb_monto_ficha);
                    monto_total = String.valueOf(total);

                    l_tarifa_cc = object.getString("id");

                    viaje_automatico_qr(getContext(), ls_id_solicitante);

                    break;
                case "2":
                    cargarTarifa_remiseria(getContext(), ls_id_solicitante);
                    break;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void viaje_automatico_qr(final Context context, String ls_id) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_viaje", l_id_viaje);
        map.put("id_sector", ls_id_sector);
        map.put("solicitante", ls_id);
        map.put("importe_bajada_cc", importe_bajada);
        map.put("importe_ficha_cc", importe_ficha);
        map.put("importe_espera_cc", importe_espera);
        map.put("monto_espera_cc", monto_espera);
        map.put("monto_ficha_cc", monto_ficha);
        map.put("importe_cc", monto_total);
        map.put("importe_restante", monto_total);
        map.put("id_tarifa_cta_cte", l_tarifa_cc);
        map.put("tipo_tarifa", l_nocturno);


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

        String newURL = Constantes.VIAJE_QR + "?" + encodedParams;

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
                                procesarAgregarViajeAutQR(response, context);
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

    private void procesarAgregarViajeAutQR(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("QR Callisto");

                    alertDialogBuilder
                            .setMessage("El QR fue leido correctamente")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                    if (link.equals("0"))
                                    {
                                        Intent intent2 = new Intent(context, MainActivity.class);
                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent2);
                                    }else{
                                        salir();
                                    }
                                }
                            }).create().show();



                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error qr " + mensaje);
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void salir(){
        act.finish();
        finishAffinity(act);
        System.exit(0);
    }

    public void cargarTarifa_remiseria(final Context context, final String ls_id_solicitante) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TARIFAS + "?id_remiseria=" + ls_remiseria ;
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

                                procesarRespuestaTarifa_remiseria(response, context, ls_id_solicitante);

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

    private void procesarRespuestaTarifa_remiseria(JSONObject response, Context context, String ls_id_solicitante) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("tarifa");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    if(l_nocturno.equals("0")) {
                        importe_bajada = object.getString("importe_bajada");
                        importe_ficha = object.getString("importe_ficha");
                        importe_espera = object.getString("importe_espera");
                    }else{
                        importe_bajada = object.getString("importe_bajada_nocturno");
                        importe_ficha = object.getString("importe_ficha_nocturno");
                        importe_espera = object.getString("importe_espera_nocturno");
                    }

                    Double valor_ficha ;

                    valor_ficha = Double.parseDouble(importe_ficha);

                    Double cant_fichas;

                    cant_fichas = Double.parseDouble(cantidad_ficha);

                    Double ldb_monto_ficha = valor_ficha * cant_fichas;


                    Double valor_ficha_espera = 0.00 ;

                    valor_ficha_espera = Double.parseDouble(importe_espera);

                    Double cant_fichas_espera;

                    cant_fichas_espera = Double.parseDouble(cantidad_espera);

                    Double ldb_monto_ficha_espera = valor_ficha_espera * cant_fichas_espera;

                    Double ldb_bajada = Double.parseDouble(importe_bajada);

                    Double total = ldb_bajada + ldb_monto_ficha + ldb_monto_ficha_espera;

                    monto_espera = String.valueOf(ldb_monto_ficha_espera);
                    monto_ficha = String.valueOf(ldb_monto_ficha);
                    monto_total = String.valueOf(total);

                    l_tarifa_cc = object.getString("id");

                    viaje_automatico_qr(context, ls_id_solicitante);

                    break;
                case "2":

                    break;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            camera = Camera.open();
            params = camera.getParameters();

        }

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            camera.release();
            camera = null;
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height)   {

        List<Camera.Size> allSizes = params.getSupportedPictureSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }
//set max Picture Size
        params.setPreviewSize(size.width, size.height);

    }

    public void cargarViaje_sinqr(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_BY_ID + "?id=" + l_id_viaje;
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
                                procesarRespuesta_sinqr(response, context);
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

    private void procesarRespuesta_sinqr(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            switch (mensaje) {
                case "1":
                    // Obtener objeto "cliente"
                    JSONArray mensaje1 = response.getJSONArray("viaje");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    cantidad_espera = object.getString("fichas_espera");
                    cantidad_ficha = object.getString("fichas");

                    cargarTarifa_sin_qr(context);
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

    public void cargarTarifa_sin_qr(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TARIFAS + "?id_remiseria=" + ls_remiseria ;
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

                                procesarRespuestaTarifa_sin_qr(response, context);

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

    private void procesarRespuestaTarifa_sin_qr(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray mensaje1 = response.getJSONArray("tarifa");

                    JSONObject object = mensaje1.getJSONObject(0);
                    //Parsear objeto

                    if(l_nocturno.equals("0")) {
                        importe_bajada = object.getString("importe_bajada");
                        importe_ficha = object.getString("importe_ficha");
                        importe_espera = object.getString("importe_espera");
                    }else{
                        importe_bajada = object.getString("importe_bajada_nocturno");
                        importe_ficha = object.getString("importe_ficha_nocturno");
                        importe_espera = object.getString("importe_espera_nocturno");
                    }

                    Double valor_ficha ;

                    valor_ficha = Double.parseDouble(importe_ficha);

                    Double cant_fichas;

                    cant_fichas = Double.parseDouble(cantidad_ficha);

                    Double ldb_monto_ficha = valor_ficha * cant_fichas;


                    Double valor_ficha_espera = 0.00 ;

                    valor_ficha_espera = Double.parseDouble(importe_espera);

                    Double cant_fichas_espera;

                    cant_fichas_espera = Double.parseDouble(cantidad_espera);

                    Double ldb_monto_ficha_espera = valor_ficha_espera * cant_fichas_espera;

                    Double ldb_bajada = Double.parseDouble(importe_bajada);

                    Double total = ldb_bajada + ldb_monto_ficha + ldb_monto_ficha_espera;

                    monto_espera = String.valueOf(ldb_monto_ficha_espera);
                    monto_ficha = String.valueOf(ldb_monto_ficha);
                    monto_total = String.valueOf(total);

                    l_tarifa_cc = object.getString("id");

                    viaje_automatico_sin_qr(context);

                    break;
                case "2":

                    break;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void viaje_automatico_sin_qr(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_viaje", l_id_viaje);
        map.put("id_sector", "" );
        map.put("importe_bajada_cc", importe_bajada);
        map.put("importe_ficha_cc", importe_ficha);
        map.put("importe_espera_cc", importe_espera);
        map.put("monto_espera_cc", monto_espera);
        map.put("monto_ficha_cc", monto_ficha);
        map.put("importe_cc", monto_total);
        map.put("importe_restante", monto_total);
        map.put("id_tarifa_cta_cte", l_tarifa_cc);
        map.put("tipo_tarifa", l_nocturno);


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

        String newURL = Constantes.VIAJE_SIN_QR + "?" + encodedParams;

        Log.d("viaje",newURL);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarAgregarViajeAutSinQR(response, context);
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

    private void procesarAgregarViajeAutSinQR(JSONObject response, Context context) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Toast.makeText(
                            context,
                            "Viaje Actualizado Correctamente",
                            Toast.LENGTH_LONG).show();
                    act.finish();
                    if (link.equals("0"))
                    {
                        Intent intent2 = new Intent(context, MainActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);
                    }else{

                        finishAffinity(act);
                        System.exit(0);
                    }


                    break;
                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error qr " + mensaje);
                    // Enviar código de falla
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
