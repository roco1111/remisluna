package com.rosario.hp.remisluna.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.MainTurno;
import com.rosario.hp.remisluna.Main_datos_viaje;
import com.rosario.hp.remisluna.R;

import java.util.ArrayList;

public class viajeAdapter extends RecyclerView.Adapter<viajeAdapter.HolderTurno>
        implements ItemClickListener7{

    private Context context;
    private ArrayList<viaje> viajes;
    private Activity act;

    public viajeAdapter(Context context, ArrayList<viaje> viajes, Activity act) {
        this.context = context;
        this.viajes = viajes;
        this.act = act;
    }

    @Override
    public HolderTurno onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_viaje,viewGroup,false);
        context = v.getContext();
        return new HolderTurno(v,this);
    }

    public void onBindViewHolder(HolderTurno holder, int position) {

        holder.salida.setText(viajes.get(position).getSalida() ) ;

        holder.destino.setText(" A " + viajes.get(position).getDestino());

        holder.hora.setText( viajes.get(position).getFecha() + " " + viajes.get(position).getHora_inicio());

        holder.recibo.setText(viajes.get(position).getNro_recibo());

        switch (viajes.get(position).getEstado()){
            case "3":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.terminado));
                break;
            case "2":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.en_curso));
                break;
            case "4":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.suspendido));
                break;
            case "5":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.alarma));
                break;
            case "6":
                holder.indicador.setImageDrawable(context.getResources().getDrawable(R.drawable.asignado));
                break;
        }



        holder.importe.setText('$' + viajes.get(position).getImporte());

    }

    public static class HolderTurno extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView salida;
        public TextView destino;
        public TextView hora;
        public TextView importe;
        public TextView recibo;
        public ImageView indicador;
        public ItemClickListener7 listener;
        public HolderTurno(View v, ItemClickListener7 listener) {
            super(v);
            salida = v.findViewById(R.id.salida);
            destino = v.findViewById(R.id.destino);
            hora = v.findViewById(R.id.hora);
            importe = v.findViewById(R.id.importe);
            indicador = v.findViewById(R.id.indicador);
            this.listener = listener;

            v.setOnClickListener(this);
        }

        public void onClick(View v) {



            listener.onItemClick(v, getAdapterPosition());
            Intent intent = new Intent(v.getContext(), Main_datos_viaje.class);
            v.getContext().startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return viajes.size();
    }
    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = settings1.edit();


        editor.putString("id_viaje",viajes.get(position).getId());
        editor.apply();
    }
}

interface ItemClickListener7 {
    void onItemClick(View view, int position);
}
