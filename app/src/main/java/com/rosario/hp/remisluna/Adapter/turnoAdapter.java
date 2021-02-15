package com.rosario.hp.remisluna.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosario.hp.remisluna.MainTurno;
import com.rosario.hp.remisluna.R;
import androidx.recyclerview.widget.RecyclerView;

import com.rosario.hp.remisluna.Entidades.turno;

import java.util.ArrayList;

public class turnoAdapter extends RecyclerView.Adapter<turnoAdapter.HolderTurno>
        implements ItemClickListener6{

    private Context context;
    private ArrayList<turno> turnos;
    private Activity act;

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

        if(!hora_hasta.equals("null")){
            l_hora = l_hora + " - Fin: " + hora_hasta;
        }

        holder.hora.setText( l_hora);

        if(!turnos.get(position).getRecaudacion().equals("null")) {
            holder.importe.setText(turnos.get(position).getRecaudacion());
        }


    }

    public static class HolderTurno extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView fecha;
        public TextView hora;
        public TextView importe;
        public ItemClickListener6 listener;
        public HolderTurno(View v, ItemClickListener6 listener) {
            super(v);
            fecha = v.findViewById(R.id.fecha);
            hora = v.findViewById(R.id.hora);
            importe = v.findViewById(R.id.importe);
            this.listener = listener;

            v.setOnClickListener(this);
        }

        public void onClick(View v) {

            listener.onItemClick(v, getAdapterPosition());
            Intent intent = new Intent(v.getContext(), MainTurno.class);
            v.getContext().startActivity(intent);
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

        editor.commit();
    }
}
interface ItemClickListener6 {
    void onItemClick(View view, int position);
}
