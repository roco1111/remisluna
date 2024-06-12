package com.rosario.hp.remisluna.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.sector;
import com.rosario.hp.remisluna.Fragment.fragment_sectores;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.sectores_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sectorAdapter extends RecyclerView.Adapter<sectorAdapter.HolderSector>
        implements ItemClickListener9{

    private Context context;
    private ArrayList<sector> sectores;
    private Activity act;
    private String l_id_viaje;
    private String ls_empresa;
    private JsonObjectRequest myRequest;
    private static final String TAG = sectorAdapter.class.getSimpleName();
    private String cantidad_espera;
    private String cantidad_ficha;
    private String l_nocturno;
    private String importe_bajada;
    private String importe_ficha;
    private String importe_espera;
    private String monto_espera;
    private String monto_ficha;
    private String monto_total;
    private String l_tarifa_cc;
    private String l_sector;
    private String ls_remiseria;



    public sectorAdapter(Context context, ArrayList<sector> sectores, Activity act) {
        this.context = context;
        this.sectores = sectores;
        this.act = act;
    }

    @Override
    public HolderSector onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_sector,viewGroup,false);
        context = v.getContext();
        return new HolderSector(v,this);
    }

    public void onBindViewHolder(HolderSector holder, int position) {

        holder.nombre.setText(sectores.get(position).getNombre() ) ;


    }

    public static class HolderSector extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView nombre;

        public ItemClickListener9 listener;
        public HolderSector(View v, ItemClickListener9 listener) {
            super(v);
            nombre = v.findViewById(R.id.nombre);
            this.listener = listener;

            v.setOnClickListener(this);
        }

        public void onClick(View v) {

            listener.onItemClick(v, getAdapterPosition());
        }

    }

    @Override
    public int getItemCount() {
        return sectores.size();
    }
    @Override
    public void onItemClick(View view, int position) {
        MediaPlayer mediaPlayer = MediaPlayer.create(act, R.raw.everblue);
        mediaPlayer.start();
        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

        l_sector = sectores.get(position).getId();
        l_id_viaje = settings1.getString("id_viaje","");
        ls_empresa = settings1.getString("id_empresa", "");
        l_nocturno = settings1.getString("nocturno","");
        ls_remiseria     = settings1.getString("remiseria","");
        cargarViaje(context);


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

                    cargarTarifaqr(context);
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

    public void cargarTarifaqr(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_TARIFA_CC + "?id_empresa=" + ls_empresa ;
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

                                procesarRespuestaTarifaqr(response, context);

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

    private void procesarRespuestaTarifaqr(JSONObject response, Context context) {

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

                    viaje_automatico_qr(context);

                    break;
                case "2":
                    cargarTarifa(context);
                    break;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarTarifa(final Context context) {

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

                    viaje_automatico_qr(context);

                    break;
                case "2":

                    break;
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void viaje_automatico_qr(final Context context) {

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("id_viaje", l_id_viaje);
        map.put("id_sector", l_sector );
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
                    Toast.makeText(
                            context,
                            "Viaje Actualizado Correctamente",
                            Toast.LENGTH_LONG).show();
                            act.finish();
                            Intent intent2 = new Intent(context, MainActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent2);


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

interface ItemClickListener9 {
    void onItemClick(View view, int position);
}
