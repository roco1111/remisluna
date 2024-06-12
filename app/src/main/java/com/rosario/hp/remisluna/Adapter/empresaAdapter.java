package com.rosario.hp.remisluna.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.empresa;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.MainActivity;
import com.rosario.hp.remisluna.Main_datos_viaje;
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

public class empresaAdapter extends RecyclerView.Adapter<empresaAdapter.HolderEmpresa>
        implements ItemClickListener8{

    private Context context;
    private ArrayList<empresa> empresas;
    private Activity act;
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
    private JsonObjectRequest myRequest;
    private String l_id_viaje;
    private String ls_empresa;

    public empresaAdapter(Context context, ArrayList<empresa> empresas, Activity act) {
        this.context = context;
        this.empresas = empresas;
        this.act = act;
    }

    @Override
    public HolderEmpresa onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_empresa,viewGroup,false);
        context = v.getContext();
        return new HolderEmpresa(v,this);
    }

    public void onBindViewHolder(HolderEmpresa holder, int position) {

        holder.nombre.setText(empresas.get(position).getNombre() ) ;

        holder.direccion.setText(empresas.get(position).getDireccion() );

        holder.localidad.setText(empresas.get(position).getLocalidad() );



        switch (empresas.get(position).getId_tipo_empresa()){
            case "1":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa1));
                break;
            case "2":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa2));
                break;
            case "3":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa3));
                break;
            case "4":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa4));
                break;
            case "5":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa5));
                break;
            case "6":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa6));
                break;
            case "7":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa7));
                break;
            case "8":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa8));
                break;
            case "9":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa9));
                break;
            case "10":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa10));
                break;
            case "11":
                holder.tipo_empresa.setImageDrawable(context.getResources().getDrawable(R.drawable.empresa11));
                break;
        }



    }

    public static class HolderEmpresa extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView nombre;
        public TextView direccion;
        public TextView localidad;
        public ImageView tipo_empresa;
        public ItemClickListener8 listener;
        public HolderEmpresa(View v, ItemClickListener8 listener) {
            super(v);
            nombre = v.findViewById(R.id.nombre);
            direccion = v.findViewById(R.id.direccion);
            localidad = v.findViewById(R.id.localidad);
            tipo_empresa = v.findViewById(R.id.tipo_empresa);
            this.listener = listener;

            v.setOnClickListener(this);
        }

        public void onClick(View v) {


            listener.onItemClick(v, getAdapterPosition());

        }

    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }
    @Override
    public void onItemClick(View view, int position) {
        MediaPlayer mediaPlayer = MediaPlayer.create(act, R.raw.everblue);
        mediaPlayer.start();
        String cantidad;
        cantidad = empresas.get(position).getSectores();
        if(cantidad.equals("1"))
        {
            l_sector = empresas.get(position).getId_sector();

            SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);
            l_id_viaje = settings1.getString("id_viaje","");
            ls_empresa = settings1.getString("id_empresa", "");
            l_nocturno = settings1.getString("nocturno","");
            ls_remiseria     = settings1.getString("remiseria","");
            cargarViaje(context);
        }else{
            SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = settings1.edit();

            editor.putString("id_empresa",empresas.get(position).getId());
            editor.apply();
            act.finish();
            Intent intent = new Intent(context, sectores_activity.class);
            context.startActivity(intent);
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


interface ItemClickListener8 {
    void onItemClick(View view, int position);
}
