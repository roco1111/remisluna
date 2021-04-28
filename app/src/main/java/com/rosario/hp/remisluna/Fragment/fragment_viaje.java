package com.rosario.hp.remisluna.Fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.ServicioGeolocalizacion;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.SocketIO;

public class fragment_viaje extends Fragment {
    private JsonObjectRequest myRequest;
    private static final String TAG = login.class.getSimpleName();
    private String ls_id_conductor;
    private TextView id_viaje;
    private TextView solicitante;
    private TextView documento;
    private TextView dato_salida;
    private TextView destino;

    private String latitud_salida;
    private String longitud_salida;
    private String latitud_destino;
    private String longitud_destino;
    private String distancia;
    private String salida_coordenada;
    private String destino_coordenada;
    private Button inicio;
    private Button anular;
    private RelativeLayout datos_viaje;
    private RelativeLayout sin_elementos;
    private LocationManager mLocationManager;
    boolean mBound = false;
    private Impresion impresion;

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
        View v = inflater.inflate(R.layout.activity_viaje, container, false);


        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        id_viaje = v.findViewById(R.id.dato_viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        documento = v.findViewById(R.id.dato_documento);
        dato_salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        inicio = v.findViewById(R.id.buttonInicio);
        anular = v.findViewById(R.id.buttonAnular);

        datos_viaje = v.findViewById(R.id.datos_viaje);
        sin_elementos = v.findViewById(R.id.sin_elementos);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_conductor     = settings.getString("id","");

        this.inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showDialogGPS("GPS apagado", "Deseas activarlo?");
                }else{
                    iniciar_viaje();
                }

            }
        });

        this.anular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                anular_viaje();

            }
        });

        cargarDatos(getContext());

        return v;
    }

    public void showDialogGPS(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(settingsIntent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
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
                    salida_coordenada = object.getString("salida_coordenadas");
                    destino_coordenada = object.getString("destino_coordenadas");
                    sin_elementos.setVisibility(View.GONE);
                    datos_viaje.setVisibility(View.VISIBLE);
                    actualizar_coordenadas();

                    break;

                case "2":
                    sin_elementos.setVisibility(View.VISIBLE);
                    datos_viaje.setVisibility(View.GONE);

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void actualizar_coordenadas(){

        Geocoder coder = new Geocoder(getContext());
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(salida_coordenada, 50);
            for(Address add : adresses){
                if (!adresses.isEmpty()) {

                    longitud_salida = String.valueOf(add.getLongitude());
                    latitud_salida = String.valueOf(add.getLatitude());
                } } }
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
                } } }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }


    private void iniciar_viaje(){

        Location location_salida = new Location("salida");
        location_salida.setLatitude(Double.parseDouble(latitud_salida));  //latitud
        location_salida.setLongitude(Double.parseDouble(longitud_salida)); //longitud
        Location location_destino = new Location("destino");
        location_destino.setLatitude(Double.parseDouble(latitud_destino));  //latitud
        location_destino.setLongitude(Double.parseDouble(longitud_destino)); //longitud
        double distance = location_salida.distanceTo(location_destino) / 100;
        distancia = String.valueOf(distance);

        String ls_viaje = id_viaje.getText().toString();

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id", ls_viaje);
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
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

                    getActivity().startService(new Intent(getActivity(),ServicioGeolocalizacion.class));
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

    private void anular_viaje(){

        String ls_viaje = id_viaje.getText().toString();

        String newURL = Constantes.ANULAR_VIAJE + "?id=" + ls_viaje;

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
                                procesarRespuestaActualizarAnular(response);
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
    private void procesarRespuestaActualizarAnular(JSONObject response) {

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
}
