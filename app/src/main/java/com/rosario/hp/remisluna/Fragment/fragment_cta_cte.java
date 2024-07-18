package com.rosario.hp.remisluna.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.rosario.hp.remisluna.Adapter.cta_cteAdapter;
import com.rosario.hp.remisluna.Adapter.empresaAdapter;
import com.rosario.hp.remisluna.Entidades.cta_cte;
import com.rosario.hp.remisluna.Entidades.empresa;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class fragment_cta_cte extends Fragment {

    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_cta_cte.class.getSimpleName();
    private RecyclerView lista;
    private TextView texto;
    private TextView saldo;
    private ImageView imagen;
    private RecyclerView.LayoutManager lManager;
    private String ls_movil;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<cta_cte> datos;
    private Activity act;
    private cta_cteAdapter Cta_CteAdapter;
    private RelativeLayout fragment_main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_saldo, container, false);

        datos = new ArrayList<>();

        act = getActivity();

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        lista =  v.findViewById(R.id.reciclador);
        lista.setHasFixedSize(true);


        texto =  v.findViewById(R.id.TwEmpty);
        imagen = v.findViewById(R.id.ImEmpty);

        fragment_main = v.findViewById(R.id.fragment_main);

        fragment_main.setBackgroundColor(getResources().getColor(R.color.black));

        imagen.setVisibility(v.INVISIBLE);
        texto.setVisibility(v.INVISIBLE);

        saldo = v.findViewById(R.id.saldo);

        lManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(lManager);
        lista.setBackgroundColor(getResources().getColor(R.color.black));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_movil = settings.getString("id_movil","");;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("swipe","swipe");

                swipeRefreshLayout.setRefreshing(true);
                cargarDatos();
            }
        });
        cargarDatos();


        return v;
    }

    public void cargarDatos() {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_CTA_CTE + "?movil=" + ls_movil;
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
                                imagen.setVisibility(getView().VISIBLE);
                                texto.setVisibility(getView().VISIBLE);

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

                    JSONArray mensaje = response.getJSONArray("movil");

                    datos.clear();

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = mensaje.getJSONObject(i);

                        if(i == 0){

                            saldo.setText("$" + object.getString("saldo"));

                        }

                        cta_cte cc = new cta_cte();

                        String id = object.getString("id");

                        cc.setId(id);

                        String descripcion = object.getString("descripcion");

                        cc.setDescripcion(descripcion);

                        String fecha = object.getString("fecha");

                        cc.setFecha(fecha);

                        String debe = object.getString("debe");

                        cc.setDebe(debe);

                        String haber = object.getString("haber");

                        cc.setHaber(haber);

                        cc.setTipo_saldo("2");

                        String saldo = object.getString("saldo");

                        cc.setSaldo(saldo);


                        datos.add(cc);

                    }

                    Cta_CteAdapter = new cta_cteAdapter(getContext(),datos, act);
                    // Setear adaptador a la lista
                    lista.setAdapter(Cta_CteAdapter);

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
