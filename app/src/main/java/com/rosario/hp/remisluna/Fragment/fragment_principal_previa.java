package com.rosario.hp.remisluna.Fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.rosario.hp.remisluna.Adapter.turnoAdapter;
import com.rosario.hp.remisluna.Entidades.turno;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.ListaBluetoohtActivity;
import com.rosario.hp.remisluna.MainActivity;
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

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class fragment_principal_previa extends Fragment {

    private ImageButton turno;
    private ImageButton impresora;
    private ImageButton historial;
    private ImageButton ic_recaudacion;
    private String ls_id_conductor;
    private static final String TAG = fragment_principal_previa.class.getSimpleName();
    private TextView txtLabel;
    private ArrayList<turno> datos;
    private Activity act;
    private JsonObjectRequest myRequest;
    private Impresion impresion;
    boolean mBound = false;
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
        View v = inflater.inflate(R.layout.activity_principal_previa, container, false);

        turno = v.findViewById(R.id.imageButtonTurno);
        this.impresora = v.findViewById(R.id.imageButtonImpresora);
        this.txtLabel = v.findViewById(R.id.referencia);
        this.turno = v.findViewById(R.id.imageButtonTurno);
        this.historial = v.findViewById(R.id.imageButtonHistorial);
        this.ic_recaudacion = v.findViewById(R.id.imageButtonRecaudacion);
        datos = new ArrayList<>();
        act = getActivity();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_conductor     = settings.getString("id","");

        this.turno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar_turno();
            }
        });

        this.impresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("tipo_ventana","main");
                editor.commit();
                getActivity().startService(new Intent(getActivity(), Impresion.class));
            }
        });
        this.historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getContext(), turnos_activity.class);
                getContext().startActivity(intent2);
            }
        });

        this.ic_recaudacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("tipo_ventana","main");
                    editor.commit();
                    getActivity().startService(new Intent(getActivity(), Impresion.class));
                } else {
                    cargarDatos();
                }
            }
        });

        return v;
    }


    private void iniciar_turno(){

        String newURL = Constantes.ALTA_TURNO + "?id_conductor=" + ls_id_conductor;
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
                                procesarRespuestaActualizar(response);
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
    private void procesarRespuestaActualizar(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Intent intent2 = new Intent(getContext(), MainActivity.class);
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

    public void cargarDatos() {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TURNOS + "?conductor=" + ls_id_conductor;
        Log.d(TAG,newURL);

        // Realizar petición GET_BY_ID
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
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

                    ticket_recaudacion( datos);

                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    protected void ticket_recaudacion( ArrayList<turno> turnos) {
        outputStream = impresion.getOutputStream();

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
                printCustom("Hora Fin " + hora_fin, 1, 0);
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
