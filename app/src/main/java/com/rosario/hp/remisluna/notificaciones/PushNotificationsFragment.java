package com.rosario.hp.remisluna.notificaciones;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rosario.hp.remisluna.R;

import java.util.ArrayList;

/**
 * Muestra lista de notificaciones
 */
public class PushNotificationsFragment extends Fragment implements PushNotificationContract.View {

    public static final String ACTION_NOTIFY_NEW_PROMO = "NOTIFY_NEW_PROMO";
    public static final String ARG_ARTICLES_NUMBER = "notificaciones";
    private static final String TAG = PushNotificationsFragment.class.getSimpleName();
    private BroadcastReceiver mNotificationsReceiver;
    private RecyclerView mRecyclerView;
    private TextView texto;
    private ImageView imagen;
    private PushNotificationsAdapter mNotificatiosAdapter;

    private PushNotificationsPresenter mPresenter;


    public PushNotificationsFragment() {
    }

    public static PushNotificationsFragment newInstance() {
        PushNotificationsFragment fragment = new PushNotificationsFragment();
        // Setup de Argumentos
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            // Gets de argumentos
        }
        mNotificationsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String titulo = intent.getStringExtra("titulo");
                String texto = intent.getStringExtra("texto");
                String id_pedido = intent.getStringExtra("id_pedido");
                String fecha = intent.getStringExtra("fecha");
                mPresenter.savePushMessage(titulo, texto, id_pedido, fecha);
            }
        };
    }
    public static PushNotificationsFragment createInstance() {
        PushNotificationsFragment PushNotificationsFragment = new PushNotificationsFragment();
        Bundle bundle = new Bundle();
        PushNotificationsFragment.setArguments(bundle);
        return PushNotificationsFragment;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("restore", true);
        outState.putInt("nAndroids", 2);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_basico, container, false);

        mNotificatiosAdapter = new PushNotificationsAdapter();
        mRecyclerView = root.findViewById(R.id.reciclador);
        texto =  root.findViewById(R.id.TwEmpty);
        imagen =  root.findViewById(R.id.ImEmpty);

        imagen.setVisibility(root.INVISIBLE);
        texto.setVisibility(root.INVISIBLE);
        mRecyclerView.setAdapter(mNotificatiosAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        LocalBroadcastManager.getInstance(getActivity())
               .registerReceiver(mNotificationsReceiver, new IntentFilter(ACTION_NOTIFY_NEW_PROMO));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mNotificationsReceiver);
    }

    @Override
    public void showNotifications(ArrayList<PushNotification> notifications) {
        mNotificatiosAdapter.replaceData(notifications);
    }

    @Override
    public void showEmptyState(boolean empty) {
        mRecyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        imagen.setVisibility(getView().VISIBLE);
        texto.setVisibility(getView().VISIBLE);
    }

    @Override
    public void popPushNotification(PushNotification pushMessage) {
        mNotificatiosAdapter.addItem(pushMessage);
    }

    @Override
    public void setPresenter(PushNotificationContract.Presenter presenter) {
        if (presenter != null) {
            mPresenter = (PushNotificationsPresenter) presenter;
        } else {
            throw new RuntimeException("El presenter de notificaciones no puede ser null");
        }
    }

}