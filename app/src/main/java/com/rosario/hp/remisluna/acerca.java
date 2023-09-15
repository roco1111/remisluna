package com.rosario.hp.remisluna;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class acerca extends Activity {

    private TextView textversion;
    private TextView textcopyright;
    private Calendar c;
    private String l_hoy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acerca);
        this.textversion = findViewById(R.id.textversion);
        this.textcopyright = findViewById(R.id.textcopyright);
        String l_version;
        l_version = "Versión: " + getVersionName(getApplicationContext());
        textversion.setText(l_version);
        c = Calendar.getInstance();
        SimpleDateFormat shoy = new SimpleDateFormat("yyyy");
        l_hoy = shoy.format(c.getTime());
        textcopyright.setText( "Copyright \u00a9 " + l_hoy + " Callisto - Todos los derechos Reservados.");
    }
    private String getVersionName(Context ctx){
        try {
            String l_version;
            l_version = ctx.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            return l_version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


}