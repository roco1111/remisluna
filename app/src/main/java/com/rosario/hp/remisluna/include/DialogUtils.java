package com.rosario.hp.remisluna.include;

/**
 * Created by JuanPablo on 07/06/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
public class DialogUtils{

    public static ProgressDialog showProgressDialog(Activity activity, String message){
        ProgressDialog m_dialog = new ProgressDialog(activity);
        m_dialog.setMessage(message);
        m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_dialog.setCancelable(false);
        m_dialog.show();
        return m_dialog;
    }
}