package com.rosario.hp.remisluna.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rosario.hp.remisluna.Entidades.cta_cte;
import com.rosario.hp.remisluna.R;

import java.util.ArrayList;

public class cta_cteAdapter extends RecyclerView.Adapter<cta_cteAdapter.HolderCtaCte>
        implements ItemClickListener10 {

    private Context context;
    private ArrayList<cta_cte> cta_ctes;
    private Activity act;
    private static final String TAG = sectorAdapter.class.getSimpleName();


    public cta_cteAdapter(Context context, ArrayList<cta_cte> cta_ctes, Activity act) {
        this.context = context;
        this.cta_ctes = cta_ctes;
        this.act = act;
    }

    @Override
    public HolderCtaCte onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_cta_cte, viewGroup, false);
        context = v.getContext();
        return new HolderCtaCte(v, this);
    }


    @Override
    public int getItemCount() {
        return cta_ctes.size();
    }

    public void onBindViewHolder(HolderCtaCte holder, int position) {

        holder.debe.setText("$" + cta_ctes.get(position).getDebe());

        holder.haber.setText("$" + cta_ctes.get(position).getHaber());

        holder.descripcion.setText(cta_ctes.get(position).getDescripcion());

        holder.fecha.setText(cta_ctes.get(position).getFecha());

        holder.saldo.setText(cta_ctes.get(position).getSaldo());


        if(cta_ctes.get(position).getHaber().equals("0.00")){
            holder.haber.setVisibility(View.INVISIBLE);
            holder.debe.setVisibility(View.VISIBLE);
            holder.tipo_cta_cte.setImageDrawable(context.getResources().getDrawable(R.drawable.viaje_cta_cte));

        }else{
            holder.haber.setVisibility(View.VISIBLE);
            holder.debe.setVisibility(View.INVISIBLE);
            holder.tipo_cta_cte.setImageDrawable(context.getResources().getDrawable(R.drawable.prepago));
        }

        if(cta_ctes.get(position).getTipo_saldo().equals("1")){
            holder.id_cta_cte.setVisibility(View.GONE);
            holder.id_saldo_inicial.setVisibility(View.VISIBLE);
        }else{
            holder.id_cta_cte.setVisibility(View.VISIBLE);
            holder.id_saldo_inicial.setVisibility(View.GONE); 
        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }


    public static class HolderCtaCte extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView fecha;
        public TextView debe;
        public TextView haber;
        public TextView saldo;
        public TextView descripcion;
        public ImageView tipo_cta_cte;
        public RelativeLayout id_cta_cte;
        public RelativeLayout id_saldo_inicial;
        public ItemClickListener10 listener;

        public HolderCtaCte(View v, ItemClickListener10 listener) {
            super(v);
            fecha = v.findViewById(R.id.fecha);
            debe = v.findViewById(R.id.debe);
            haber = v.findViewById(R.id.haber);
            saldo = v.findViewById(R.id.saldo);
            descripcion = v.findViewById(R.id.descripcion);
            tipo_cta_cte = v.findViewById(R.id.tipo_cta_cte);
            id_cta_cte = v.findViewById(R.id.id_cta_cte);
            id_saldo_inicial = v.findViewById(R.id.id_saldo_inicial);
            this.listener = listener;

            v.setOnClickListener(this);
        }

        public void onClick(View v) {


            listener.onItemClick(v, getAdapterPosition());

        }

    }


}


interface ItemClickListener10 {
    void onItemClick(View view, int position);
}
