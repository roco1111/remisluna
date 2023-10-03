package com.rosario.hp.remisluna.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Adapter.turnoAdapter;
import com.rosario.hp.remisluna.Adapter.viajeAdapter;
import com.rosario.hp.remisluna.Entidades.turno;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragment_viajes extends Fragment {

    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_viajes.class.getSimpleName();
    private RecyclerView lista;
    private TextView texto;
    private ImageView imagen;
    private RecyclerView.LayoutManager lManager;
    private String ls_id_turno;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<viaje> datos;
    private Activity act;
    private viajeAdapter ViajeAdapter;
	private String ls_id_chofer;
	private String l_turno_app;
    private Context context;
    private String ls_remiseria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_basico, container, false);

        datos = new ArrayList<>();

        act = getActivity();
        context = getContext();

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        lista =  v.findViewById(R.id.reciclador);
        lista.setHasFixedSize(true);


        texto =  v.findViewById(R.id.TwEmpty);
        imagen = v.findViewById(R.id.ImEmpty);

        imagen.setVisibility(v.INVISIBLE);
        texto.setVisibility(v.INVISIBLE);

        lManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(lManager);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_turno     = settings.getString("id_turno","");
		ls_id_chofer     = settings.getString("id","");
        ls_remiseria     = settings.getString("remiseria","");


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("swipe","swipe");

                swipeRefreshLayout.setRefreshing(true);

            }
        });
        cargarParametroTurno(getContext());


        return v;
    }
	
	public void cargarParametroTurno(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("parametro", "17");
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
                                procesarRespuestaParametroTurno(response, context);
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

    private void procesarRespuestaParametroTurno(JSONObject response, Context context) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            switch (mensaje) {
                case "1":
                    JSONArray datos_parametro = response.getJSONArray("parametro");


                    for (int i = 0; i < datos_parametro.length(); i++) {
                        JSONObject object = datos_parametro.getJSONObject(i);

                        l_turno_app = object.getString("valor");

                    }

                    cargarDatos(context);

                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");

                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarDatos(Context context) {

        // Añadir parámetro a la URL del web service
		
		String newURL;
		
		if(l_turno_app.equals("0")){
				
				newURL = Constantes.GET_VIAJES_CHOFER + "?chofer=" + ls_id_chofer;
                      
				}else{
					
				newURL = Constantes.GET_VIAJES_TURNO + "?turno=" + ls_id_turno;    

				}
					
        
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

                    JSONArray mensaje = response.getJSONArray("viajes");

                    datos.clear();

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = mensaje.getJSONObject(i);
                        viaje via = new viaje();

                        String id = object.getString("id");

                        via.setId(id);

                        String fecha = object.getString("fecha");

                        via.setFecha(fecha);

                        String hora_inicio = object.getString("hora_inicio");

                        if(hora_inicio.equals("null")){
                            hora_inicio = "No Iniciado";
                        }

                        via.setHora_inicio(hora_inicio);

                        String salida = object.getString("salida");

                        via.setSalida(salida);

                        String destino = object.getString("destino");

                        via.setDestino(destino);

                        String importe = object.getString("importe");

                        if(importe.equals("null")){
                            importe = "0,00";
                        }

                        via.setImporte(importe);

                        String estado_viaje = object.getString("estado");

                        via.setEstado(estado_viaje);

                        String nro_recibo = object.getString("nro_recibo");

                        if(nro_recibo.equals("null")){
                            nro_recibo = "Sin Nro";
                        }

                        via.setNro_recibo(nro_recibo);

                        datos.add(via);

                    }

                    ViajeAdapter = new viajeAdapter(getContext(),datos, act);
                    // Setear adaptador a la lista
                    lista.setAdapter(ViajeAdapter);

                    break;
                case "2": // FALLIDO

                    imagen.setVisibility(getView().VISIBLE);
                    texto.setVisibility(getView().VISIBLE);


                    break;
            }
            swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
}
