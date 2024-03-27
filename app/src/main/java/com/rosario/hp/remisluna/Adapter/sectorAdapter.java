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
import androidx.recyclerview.widget.RecyclerView;
import com.rosario.hp.remisluna.Entidades.sector;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.sectores_activity;

import java.util.ArrayList;

public class sectorAdapter extends RecyclerView.Adapter<sectorAdapter.HolderSector>
        implements ItemClickListener9{

    private Context context;
    private ArrayList<sector> sectores;
    private Activity act;

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
            Intent intent = new Intent(v.getContext(), sectores_activity.class);
            v.getContext().startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return sectores.size();
    }
    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = settings1.edit();

        editor.putString("id_sector",sectores.get(position).getId());
        editor.apply();
    }
}

interface ItemClickListener9 {
    void onItemClick(View view, int position);
}
