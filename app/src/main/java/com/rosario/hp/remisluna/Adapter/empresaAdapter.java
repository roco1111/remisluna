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

import com.rosario.hp.remisluna.Entidades.empresa;
import com.rosario.hp.remisluna.Entidades.viaje;
import com.rosario.hp.remisluna.Main_datos_viaje;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.sectores_activity;

import java.util.ArrayList;

public class empresaAdapter extends RecyclerView.Adapter<empresaAdapter.HolderEmpresa>
        implements ItemClickListener8{

    private Context context;
    private ArrayList<empresa> empresas;
    private Activity act;

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
            Intent intent = new Intent(v.getContext(), sectores_activity.class);
            v.getContext().startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }
    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences settings1 = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = settings1.edit();

        editor.putString("id_empresa",empresas.get(position).getId());
        editor.apply();
    }
}

interface ItemClickListener8 {
    void onItemClick(View view, int position);
}
