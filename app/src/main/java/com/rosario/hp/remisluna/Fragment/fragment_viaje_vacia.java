package com.rosario.hp.remisluna.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.rosario.hp.remisluna.R;

public class fragment_viaje_vacia extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_viaje_vacio, container, false);
        return v;
    }
}
