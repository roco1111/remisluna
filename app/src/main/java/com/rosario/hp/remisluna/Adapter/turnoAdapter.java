package com.rosario.hp.remisluna.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rosario.hp.remisluna.MainTurno;
import com.rosario.hp.remisluna.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.rosario.hp.remisluna.Entidades.turno;
import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.VolleySingleton;
import com.rosario.hp.remisluna.viajes_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class turnoAdapter extends RecyclerView.Adapter<turnoAdapter.HolderTurno>
        implements ItemClickListener6{

    private Context context;
    private ArrayList<turno> turnos;
    private Activity act;
    private JsonObjectRequest myRequest;
    private String kms;
    private String fecha;
    private String hora_inicio;
    private String hora_fin;
    private String recaudacion;
    private String l_nro_turno;
    private String estado;
    private File dir;
    private String path;
    private File file;
    private ArrayList<viaje> viajes = new ArrayList<>();


    public turnoAdapter(Context context, ArrayList<turno> turnos, Activity act) {
        this.context = context;
        this.turnos = turnos;
        this.act = act;
    }

    @Override
    public HolderTurno onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_turno,viewGroup,false);
        context = v.getContext();
        return new HolderTurno(v,this);
    }

    public void onBindViewHolder(HolderTurno holder, int position) {

        holder.fecha.setText(turnos.get(position).getFecha());

        String hora_desde = turnos.get(position).getHora_inicio();
        String hora_hasta = turnos.get(position).getHora_fin();
        String l_hora = "Inicio: " + hora_desde;
        String l_nro = "Turno Nro: " + turnos.get(position).getNro_turno();

        if(!hora_hasta.equals("null")){
            l_hora = l_hora + " - Fin: " + hora_hasta;
        }

        holder.hora.setText( l_hora);
        holder.nro.setText(l_nro);

        if(!turnos.get(position).getRecaudacion().equals("null")) {
            holder.importe.setText(turnos.get(position).getRecaudacion());
        }

        holder.impresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imprimir_turno(v.getContext(),position);
            }
        });

        holder.nro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), viajes_activity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), viajes_activity.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), viajes_activity.class);
                v.getContext().startActivity(intent);
            }
        });

        switch (turnos.get(position).getEstado()){
            case "2":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.terminado));
                break;
            case "1":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.en_curso));
                break;
        }

        holder.indicador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), viajes_activity.class);
                v.getContext().startActivity(intent);
            }
        });


    }

    public static class HolderTurno extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView fecha;
        public TextView hora;
        public TextView importe;
        public ImageView indicador;
        public ImageView impresora;
        public TextView nro;
        public ItemClickListener6 listener;
        public HolderTurno(View v, ItemClickListener6 listener) {
            super(v);
            fecha = v.findViewById(R.id.fecha);
            hora = v.findViewById(R.id.hora);
            importe = v.findViewById(R.id.importe);
            indicador = v.findViewById(R.id.indicador);
            nro = v.findViewById(R.id.nro);
            impresora = v.findViewById(R.id.impresora);
            this.listener = listener;

            v.setOnClickListener(this);
        }

        public void onClick(View v) {

            listener.onItemClick(v, getAdapterPosition());
            //Intent intent = new Intent(v.getContext(), MainTurno.class);

        }

    }

    @Override
    public int getItemCount() {
        return turnos.size();
    }
    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = settings1.edit();


        editor.putString("id_turno",turnos.get(position).getId());
        editor.apply();

    }

    private void imprimir_turno(final Context context, int position) {

       fecha = turnos.get(position).getFecha();
        hora_inicio = turnos.get(position).getHora_inicio();
        hora_fin = turnos.get(position).getHora_fin();
        kms = turnos.get(position).getDistancia();
        recaudacion = turnos.get(position).getRecaudacion();
        estado = turnos.get(position).getEstado();
        l_nro_turno = turnos.get(position).getNro_turno();
        String l_id_turno = turnos.get(position).getId();
        datos_viajes_turno(context, l_id_turno);
    }

    public void datos_viajes_turno(final Context context, String ls_id_turno ) {

        String newURL = Constantes.GET_VIAJES_TURNO + "?turno=" + ls_id_turno;
        Log.d("adapter turno",newURL);

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
                                Log.d("adapter turno", "Error Volley turno: " + error.getMessage());

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
            crear_pfd_turno(viajes);


        } catch (JSONException e) {
            e.printStackTrace();
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
            act.startActivity(target);
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
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String nombre_remiseria = settings.getString("nombre_remiseria","");
            doc.left(10f);
            //doc.top(15f);
            file = new File(dir, "fin_turno-"+ l_nro_turno +".pdf");
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

                cell = new PdfPCell(new Phrase("TOTAL:  " + importe,font));
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

            cell = new PdfPCell(new Phrase(recaudacion,font));
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
            Log.d("turno adapter", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
interface ItemClickListener6 {
    void onItemClick(View view, int position);
}
