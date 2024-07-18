package com.rosario.hp.remisluna.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Impresion;
import com.rosario.hp.remisluna.MainViaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.PrinterCommands;
import com.rosario.hp.remisluna.include.Utils;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.viajes_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import static com.rosario.hp.remisluna.include.Utils.stringABytes;

public class fragment_datos_viaje extends Fragment{

    private JsonObjectRequest myRequest;
    private static final String TAG = fragment_datos_viaje.class.getSimpleName();
    private TextView fecha;
    private TextView nro_viaje;
    private TextView etiqueta_viaje;
    private TextView solicitante;
    private TextView documento;
    private TextView salida;
    private TextView destino;
    private TextView hora_salida;
    private TextView hora_destino;
    private TextView importe;
    private TextView datos_bajada;
    private TextView motivo;
    private String chofer;
    private String distancia;
    private String fecha_tarifa;
    private String movil;
    private String telefono_queja;
    private String telefono_remiseria;
    private String nombre_remiseria;
    private TextView total;
    private TextView espera;
    private LinearLayout suspension;
    private Button imprimir;
    private String importe_fichas;
    private String bajada;
    private String localidad_abreviada;
    private String precio_km;
    private String ls_id_conductor;
    private String chapa;
    private String patente;

    private String ls_id_viaje;
    private Impresion impresion;
    boolean mBound = false;
    private ArrayList<viaje> viaje = new ArrayList<>();
    private static OutputStream outputStream;
    byte FONT_TYPE;
    private String nro_recibo;

    private Activity act;
    private Context context;

    private File file;
    private File dir;
    private String path;

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService

    }

    @Override
    public void onStop() {
        super.onStop();

        if(mBound) {
            act.unbindService(connection);

            mBound = false;
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
        View v = inflater.inflate(R.layout.activity_datos_viaje, container, false);
        act = getActivity();

        fecha = v.findViewById(R.id.dato_fecha);
        nro_viaje = v.findViewById(R.id.dato_viaje);
        etiqueta_viaje = v.findViewById(R.id.viaje);
        solicitante = v.findViewById(R.id.dato_solicitante);
        documento = v.findViewById(R.id.dato_documento);
        salida = v.findViewById(R.id.dato_salida);
        destino = v.findViewById(R.id.dato_destino);
        hora_salida = v.findViewById(R.id.hora_salida);
        hora_destino = v.findViewById(R.id.hora_destino);
        importe = v.findViewById(R.id.dato_importe);
        motivo = v.findViewById(R.id.dato_motivo);
        impresion = new Impresion();
        imprimir = v.findViewById(R.id.buttonTicket);
        suspension = v.findViewById(R.id.id_suspension);
        espera = v.findViewById(R.id.dato_espera);
        total = v.findViewById(R.id.dato_total);
        datos_bajada = v.findViewById(R.id.dato_bajada);

        context = getContext();


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_viaje     = settings.getString("id_viaje","");
        cargarDatos(context);

        this.imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBound) {
                    crear_pfd_repetir_ticket();
                } else {
                    printTicket();
                }
            }
        });


        return v;
    }

    public void cargarDatos(final Context context) {

        // Añadir parámetro a la URL del web service
        String newURL = Constantes.GET_VIAJE_BY_ID + "?id=" + ls_id_viaje;
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

                    fecha.setText(object.getString("fecha"));
                    nro_viaje.setText(object.getString("nro_recibo"));
                    solicitante.setText(object.getString("solicitante"));
                    documento.setText(object.getString("nro_documento"));
                    salida.setText(object.getString("salida"));
                    destino.setText(object.getString("destino"));
                    hora_salida.setText(object.getString("hora_inicio"));
                    localidad_abreviada = object.getString("abreviada");
                    if(object.getString("estado").equals("6") || object.getString("estado").equals("7") ){
                        hora_destino.setVisibility(View.INVISIBLE);
                    }else{
                        hora_destino.setVisibility(View.VISIBLE);
                    }
                    hora_destino.setText(object.getString("hora_fin"));
                    String ls_importe;
                    ls_importe = object.getString("importe");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }

                    total.setText(ls_importe);

                    ls_importe = object.getString("importe_fichas");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }

                    importe.setText(ls_importe);

                    ls_importe = object.getString("bajada");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }

                    datos_bajada.setText(ls_importe);


                    ls_importe = object.getString("importe_espera");
                    if(ls_importe.equals("null"))
                    {
                        ls_importe = "0,00";
                    }
                    espera.setText(ls_importe);


                    motivo.setText(object.getString("motivo"));
                    chofer = object.getString("chofer");
                    distancia = object.getString("distancia");
                    fecha_tarifa = object.getString("fecha_tarifa");
                    movil = object.getString("movil");
                    telefono_queja = object.getString("telefono_queja");
                    telefono_remiseria = object.getString("telefono");
                    nombre_remiseria = object.getString("remiseria");
                    bajada = object.getString("bajada");
                    importe_fichas = object.getString("importe_fichas");
                    nro_recibo = object.getString("nro_recibo");
                    precio_km = object.getString("precio_km");
                    ls_id_conductor = object.getString("id_conductor");
                    chapa = object.getString("chapa");
                    patente = object.getString("patente");

                    if(object.getString("estado").equals("4")){
                        suspension.setVisibility(View.VISIBLE);
                    }else{
                        suspension.setVisibility(View.INVISIBLE);
                    }

                    if( object.getString("estado").equals("7") ){
                        nro_viaje.setVisibility(View.GONE);
                        etiqueta_viaje.setText(R.string.viaje_anulado);
                        hora_salida.setVisibility(View.INVISIBLE);
                        imprimir.setVisibility(View.INVISIBLE);

                    }else{
                        hora_salida.setVisibility(View.VISIBLE);
                        etiqueta_viaje.setText(R.string.nro_recibo);
                        imprimir.setVisibility(View.VISIBLE);
                        nro_viaje.setVisibility(View.VISIBLE);
                    }

                    cargarImpresora(context);
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


            cell = new PdfPCell(new Phrase(getResources().getString(R.string.recibo),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("Nro Recibo: " + nro_recibo,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(getResources().getString(R.string.servicio) + ' ' + localidad_abreviada,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table1.addCell(cell);

            doc.add(table1);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table2 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase(fecha.getText().toString(),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase("Chofer: " + chofer,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase("Patente: " + patente,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(getResources().getString(R.string.chapa) + chapa,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table2.addCell(cell);
            doc.add(table2);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("SALIDA: " + hora_salida.getText(),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("DESDE: " + salida.getText().toString(),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("HASTA: " + destino.getText().toString(),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("LLEGADA: " + hora_destino.getText(),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("RECORRIDO: " + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(distancia)) + " Kms.",font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cell);

            doc.add(table);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table3 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("TARIFA AL " + fecha_tarifa,font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase("BAJADA: " + '$' + String.format(Locale.GERMAN, "%.2f", Double.parseDouble(bajada)),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase("VIAJE: " + '$' + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(importe_fichas)),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            cell = new PdfPCell(new Phrase("ESPERA: " + '$' + String.format(Locale.GERMANY, "%.2f", Double.parseDouble(espera.getText().toString())),font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table3.addCell(cell);

            doc.add(table3);

            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));


            PdfPTable table4 = new PdfPTable(1);

            cell = new PdfPCell(new Phrase("TOTAL: " + '$' + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(total.getText().toString())),titulo));
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


    protected void printTicket() {

        outputStream = impresion.getOutputStream();

        //print command
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            byte[] printformat = { 0x1B,0x21,0x08 };
            outputStream.write(printformat);

            //print title
            printUnicode();
            //print normal text
            printCustom (nombre_remiseria,2,1);
            printNewLine();
            printCustom ("Tel. Remis: " + telefono_remiseria,1,1);
            printNewLine();
            printCustom ("Tel. Queja: " + telefono_queja,1,1);
            printNewLine();
            printText(getResources().getString(R.string.recibo)); // total 32 char in a single line
            printNewLine();
            printText("Nro Recibo: " + nro_recibo ); // total 32 char in a single line
            printNewLine();
            printText(stringABytes(getResources().getString(R.string.servicio) + ' ' + localidad_abreviada));
            printNewLine();
            printText(fecha.getText().toString());//fecha
            printNewLine();
            printCustom ("Chofer: " + chofer,1,0);
            printCustom ("Nro Remis: " + movil,1,0);
            printText(stringABytes(getResources().getString(R.string.chapa) + chapa));
            printNewLine();
            printText("SALIDA  " + hora_salida.getText());
            printNewLine();
            printText("DESDE  " + salida.getText().toString());
            printNewLine();
            printText("HASTA  " + destino.getText().toString());
            printNewLine();
            printText("LLEGADA  " + hora_destino.getText());
            printNewLine();
            printText("RECORRIDO  " + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(distancia)) + " Kms.");
            printNewLine();
            printText("PRECIO/KM  " + precio_km);
            printNewLine();
            printNewLine();
            printText("TARIFA AL  " + fecha_tarifa);
            printNewLine();
            printText("BAJADA  " + '$' + String.format(Locale.GERMAN,"%.2f",Double.parseDouble(bajada)));
            printNewLine();
            printText("VIAJE  " + '$' + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(importe_fichas)));
            printNewLine();
            printText("ESPERA  "+ '$' + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(espera.getText().toString())));
            printNewLine();
            printNewLine();
            printCustom ("TOTAL:  " + '$' + String.format(Locale.GERMANY,"%.2f",Double.parseDouble(importe.getText().toString())),2,0);
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
