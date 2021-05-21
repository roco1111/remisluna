package com.rosario.hp.remisluna;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.rosario.hp.remisluna.Entidades.ayuda;
import com.rosario.hp.remisluna.Fragment.fragment_turnos;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Objects;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class turnos_activity extends AppCompatActivity {

    private ArrayList<ayuda> ayudas;
    private Impresion impresion;
    boolean mBound = false;
    private static OutputStream outputStream;
    private JsonObjectRequest myRequest;
    private static final String TAG = turnos_activity.class.getSimpleName();

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getApplicationContext(), Impresion.class);
        getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getApplicationContext().unbindService(connection);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener instancia FirebaseAuth

        setContentView(R.layout.lista_main_inicial);

        Fragment fragment = null;
        fragment = new fragment_turnos();
        ayudas = new ArrayList<>();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();

        }

        Objects.requireNonNull(getSupportActionBar()).setTitle("Turnos");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        MenuItem myMenuItem = menu.findItem(R.id.menu_principal);
        getMenuInflater().inflate(R.menu.sub_menu_ayuda, myMenuItem.getSubMenu());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        Intent intent;
        switch (id) {

            case R.id.menu_ayuda:

                editor.putString("url", "https://remisluna.com.ar/remiseria/pagina_ayuda.php?id=3");
                editor.apply();
                intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                editor.commit();
                break;
            case R.id.menu_ayuda_app:

                editor.putString("url", "https://remisluna.com.ar/remiseria/paginas_ayuda.php");
                editor.apply();
                intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                editor.commit();
                break;
            case R.id.menu_app_impresa:
                if(mBound) {
                    cargarAyudas(getApplicationContext());

                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.menu_ayuda_impresa:
                if(mBound) {
                    cargarAyuda(getApplicationContext());

                }else{
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.no_impresora,
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarAyuda(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_ID_AYUDA + "?ayuda=3";
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
                                procesarRespuestaAyuda(response);
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

    private void procesarRespuestaAyuda(JSONObject response) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");
            switch (mensaje) {
                case "1":
                    JSONArray datos_ayuda = response.getJSONArray("ayuda");

                    ayudas.clear();

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = datos_ayuda.getJSONObject(i);
                        ayuda ay = new ayuda();


                        String titulo = object.getString("titulo");

                        ay.setTitulo(titulo);

                        String descripcion = object.getString("descripcion");

                        ay.setDescripcion(descripcion);


                        ayudas.add(ay);

                    }
                    ticket_ayuda_app(ayudas);

                    break;


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cargarAyudas(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_AYUDAS;
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
                                procesarRespuestaAyudas(response);
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

    private void procesarRespuestaAyudas(JSONObject response) {

        try {
            // Obtener atributo "mensaje"
            String mensaje = response.getString("estado");

            if (mensaje.equals("1")) {

                    JSONArray datos_ayuda = response.getJSONArray("ayuda");

                    ayudas.clear();

                    for(int i = 0; i < datos_ayuda.length(); i++)
                    {JSONObject object = datos_ayuda.getJSONObject(i);
                        ayuda ay = new ayuda();

                        String id = object.getString("ID");

                        ay.setId(id);

                        String titulo = object.getString("TITULO");

                        ay.setTitulo(titulo);

                        String descripcion = object.getString("DESCRIPCION");

                        ay.setDescripcion(descripcion);


                        ayudas.add(ay);

                    }
                    ticket_ayuda_app(ayudas);




            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void ticket_ayuda_app( ArrayList<ayuda> ayudas) {

        outputStream = impresion.getOutputStream();

        //print command
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //outputStream.write(printformat);

            //print title
            printUnicode();
            //print normal text
            printCustom(getResources().getString(R.string.empresa), 2, 1);
            printNewLine();
            printCustom(getResources().getString(R.string.menu_ayuda), 1, 1); // total 32 char in a single line

            printNewLine();
            String titulo;
            String descripcion;
            printCustom("",1,0);
            for (ayuda Ayuda : ayudas) {
                titulo = Ayuda.getTitulo();
                printText(stringABytes(titulo));
                printNewLine();

                descripcion = Ayuda.getDescripcion();
                printText(stringABytes(descripcion));
                printNewLine();
            }
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
}
