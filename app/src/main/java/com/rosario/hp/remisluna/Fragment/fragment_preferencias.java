package com.rosario.hp.remisluna.Fragment;

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
    private FirebaseAuth mAuth;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.preferencias, container, false);
        perfil = v.findViewById(R.id.nav_perfil);
        politicas = v.findViewById(R.id.nav_politicas);
        terminos = v.findViewById(R.id.nav_terminos);
        acerca = v.findViewById(R.id.nav_acerca);
        salir = v.findViewById(R.id.nav_salir);


        this.perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), insertUsuario.class);

                getActivity().startActivity(intent2);
            }
        });

        politicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("url", "https://remisluna.com.ar/politicas/privacidad.php");
                editor.apply();

                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Objects.requireNonNull(getActivity()).startActivity(intent);
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
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Salir")
                .setMessage("Desea salir de la aplicación?")
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("posicion", "0");
                        editor.commit();
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();

                        getActivity().finish();
                        Intent intent4 = new Intent(getContext(), Main_Login.class);
                        startActivity(intent4);

                    }
                })
                .show();

    }

}