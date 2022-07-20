package com.rosario.hp.remisluna.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.rosario.hp.remisluna.R;
import com.rosario.hp.remisluna.WebActivity;
import com.rosario.hp.remisluna.Main_Login;
import com.rosario.hp.remisluna.insertUsuario;

import java.util.Objects;

public class fragment_preferencias extends Fragment {


    private RelativeLayout perfil;
    private RelativeLayout politicas;
    private RelativeLayout terminos;
    private RelativeLayout acerca;
    private RelativeLayout salir;
    private RelativeLayout ayuda;
    private FirebaseAuth mAuth;
    private Context context;
    private Activity act;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.preferencias, container, false);
        perfil = v.findViewById(R.id.nav_perfil);
        politicas = v.findViewById(R.id.nav_politicas);
        terminos = v.findViewById(R.id.nav_terminos);
        acerca = v.findViewById(R.id.nav_acerca);
        salir = v.findViewById(R.id.nav_salir);
        ayuda = v.findViewById(R.id.nav_ayuda);
        context = getContext();
        act = getActivity();

        this.perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), insertUsuario.class);

                act.startActivity(intent2);
            }
        });

        this.ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                Intent intent;


                editor.putString("url", "https://callisto.com.ar/remiseria/paginas_ayuda.php");
                editor.apply();
                intent = new Intent(context, WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                editor.apply();
            }
        });

        politicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("url", "https://remisluna.com.ar/politicas/privacidad.php");
                editor.apply();

                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.startActivity(intent);
                editor.commit();
            }
        });

        this.salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrar_sesion();
            }
        });




        return v;

    }

    private void cerrar_sesion() {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Salir")
                .setMessage("Desea salir de la aplicación?")
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("posicion", "0");
                        editor.commit();
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();

                        getActivity().finish();
                        Intent intent4 = new Intent(context, Main_Login.class);
                        startActivity(intent4);

                    }
                })
                .show();

    }

}