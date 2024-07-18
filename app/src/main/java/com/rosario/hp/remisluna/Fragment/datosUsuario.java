package com.rosario.hp.remisluna.Fragment;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.rosario.hp.remisluna.Entidades.conductor_titular;
import com.rosario.hp.remisluna.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.rosario.hp.remisluna.include.Constantes;
import com.rosario.hp.remisluna.include.NothingSelectedSpinnerAdapter;
import com.rosario.hp.remisluna.include.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class datosUsuario extends Fragment {
    private static final String TAG = datosUsuario.class.getSimpleName();
    String id_firebase= null;
    private FirebaseAuth mAuth = null;
    NothingSelectedSpinnerAdapter nothing;
    Activity act;
    private JsonObjectRequest myRequest;
    private TextView tvNombre = null;
    private TextView tvApellido = null;
    private TextView tvMail = null;
    private TextView tvClave = null;
    private TextView tvConfirmar = null;
    private Button btn_guardar = null;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    public static final String ARG_ARTICLES_NUMBER = "datos_usuario";
    private static int SELECT_PICTURE = 2;
    private ArrayList<conductor_titular> usuarios;
    String ls_id_conductor;
    private Button editar_foto;
    private CircleImageView imagen;
    GetImageToURL GetImageToURL;
    Uri imageUri;
    Context context;
    StorageReference storageRef;
    private FirebaseStorage storage;
    private Bitmap loadedImage;
    private Spinner sp_empresa;
    private String id_empresa;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("restore", true);
        outState.putInt("nAndroids", 2);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void  onResume(){
        super.onResume();

        setRetainInstance(true);
        context = getContext();
    }

    @Override
    public void  onPause(){
        super.onPause();

    }

    public datosUsuario() {
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity){
            act=(Activity) context;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        usuarios = new ArrayList<>();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        ls_id_conductor = settings.getString("id","");


    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflando layout del fragmento

        View v;
        v = inflater.inflate(R.layout.activity_configuracion_usuario, container, false);

        this.tvNombre = v.findViewById(R.id.editTextNombre);
        this.tvApellido = v.findViewById(R.id.editTextApellido);
        this.tvMail =  v.findViewById(R.id.editTextMail);
        this.tvClave = v.findViewById(R.id.editTextClave);
        this.tvConfirmar = v.findViewById(R.id.editTextConfirmar);
        this.btn_guardar = v.findViewById(R.id.buttonRegistro);
        this.editar_foto = v.findViewById(R.id.buttonFoto);
        imagen =  v.findViewById(R.id.imageViewfoto);

        this.btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validar()) {

                    if (compara_clave()) {

                        modificarUsuario();

                    } else {
                        Toast.makeText(
                                getActivity(),
                                "La clave ingresada no coincide con su confirmación",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        setHasOptionsMenu(true);

        editar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria(v);

            }
        });

        cargarAdaptador();

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void actualizar_foto(){
        storage = FirebaseStorage.getInstance();

        if (storageRef == null)
            storageRef = storage.getReference();

        String mChild = "conductores/" + ls_id_conductor  + ".jpg";
        Log.d(TAG,mChild);
        final StorageReference filepath = storageRef.child(mChild);
        try {


            clearGlideCache();

            Glide.with(getActivity().getApplicationContext())
                    .load(filepath)
                    .error(R.drawable.ic_account_circle)
                    .fallback(R.drawable.ic_account_circle)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .centerCrop ()
                    .into(imagen);
        }catch (Exception e){
            Log.d("error",e.getMessage());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {


            imageUri = data.getData();

            context = getContext();

            try {
                loadedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            } catch (Exception e) {
                actualizar_foto();

                //e.printStackTrace();
            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            bmOptions.inSampleSize = 1;
            bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmOptions.inJustDecodeBounds = false;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            loadedImage.compress(Bitmap.CompressFormat.JPEG, 10, baos);

            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), loadedImage, "Title", null);
            imageUri = Uri.parse(path);

            switch (requestCode) {
                case 2:

                    if (requestCode == SELECT_PICTURE) {


                        storage = FirebaseStorage.getInstance();

                        if (storageRef == null)
                            storageRef = storage.getReference();

                        String mChild = "conductores/" + ls_id_conductor + ".jpg";
                        final StorageReference filepath = storageRef.child(mChild);

                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, "Se ha actualizado la foto", Toast.LENGTH_SHORT).show();

                                GetImageToURL = new GetImageToURL();

                                GetImageToURL.execute(loadedImage);

                            }
                        });
                    }


                    break;
            }

        }
    }

    private class GetImageToURL extends AsyncTask<Bitmap, Bitmap, Bitmap> {

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected Bitmap doInBackground(Bitmap... params) {


            return params[0];
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            imagen.setImageBitmap(result);
            imagen.postInvalidate();

        }
    }



    private void  abrirGaleria(View v) {
        verifyStoragePermissions(getActivity());
        Intent intent = new Intent();
        intent.setType("image/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                SELECT_PICTURE);

    }

    private Void carga_datos(){


        tvNombre.setText(usuarios.get(0).getNombre());
        tvApellido.setText(usuarios.get(0).getApellido());
        tvMail.setText(usuarios.get(0).getMail());

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_ayuda:// CONFIRMAR
                if (!validar()) {

                    if (compara_clave()) {

                            modificarUsuario();

                    } else {
                        Toast.makeText(
                                getActivity(),
                                "La clave ingresada no coincide con su confirmación",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    public boolean validar(){
        if(camposVacios()){
            Toast.makeText(
                    getActivity(),
                    "Completa los campos",
                    Toast.LENGTH_LONG).show();
            return true;}
        if(ls_id_conductor.equals("")) {
            if (claves()) {
                return true;
            }
        }
        return false;

    }
    public boolean claves(){

        String clave = tvClave.getText().toString();
        String confirmacion = tvConfirmar.getText().toString();

        if(clave.isEmpty())
        {
            tvClave.requestFocus();
            Toast.makeText(
                    getActivity(),
                    "Debe ingresar su clave",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        if(confirmacion.isEmpty())
        {
            tvConfirmar.requestFocus();
            Toast.makeText(
                    getActivity(),
                    "Debe ingresar la confirmación de su clave",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    //funcion MD5

    public static void main(String[] args) {

        String s = "SecretKey20111013000";
        String  res = md5(s);
        System.out.println(res);

    }

    private static String md5(String s) { try {

        // Create MD5 Hash
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
        return "";

    }

    public void modificarUsuario() {
        final String nombre = tvNombre.getText().toString();
        final String apellido = tvApellido.getText().toString();
        final String mail = tvMail.getText().toString();
        final String clave = md5(tvClave.getText().toString());

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("nombre", nombre);
        map.put("apellido", apellido);
        map.put("mail", mail);
        map.put("clave", clave);
        map.put("id", ls_id_conductor);


        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);


        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "utf-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "utf-8"));
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "utf-8", uee);
        }

        encodedParams.setLength(Math.max(encodedParams.length() - 1, 0));

        String newURL = Constantes.UPDATE_CONDUCTOR + "?" + encodedParams;

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                myRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        //jobject,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor

                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }


                ) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }


                }

        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                5,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void procesarRespuesta(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de éxito

                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getActivity(),
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public boolean compara_clave(){
        String clave ;
        String confirma;

        clave = tvClave.getText().toString();
        confirma = tvConfirmar.getText().toString();

        if (clave.equals(confirma)) {
            return true;
        }else{
            tvClave.requestFocus();
            return  false;
        }

    }

    public boolean camposVacios() {

        String nombre = null;

        nombre = tvNombre.getText().toString();

        if(nombre.isEmpty())
        {
            tvNombre.setHintTextColor(Color.RED);
            tvNombre.requestFocus();
            return true;
        }else {
            tvNombre.setHintTextColor(Color.GRAY);
        }

        String apellido = null;

        apellido = tvApellido.getText().toString();

        if(apellido.isEmpty())
        {
            tvApellido.setHintTextColor(Color.RED);
            tvApellido.requestFocus();
            return true;
        }else {
            tvApellido.setHintTextColor(Color.GRAY);
        }

        String mail = tvMail.getText().toString();

        if(mail.isEmpty())
        {
            tvMail.setHintTextColor(Color.RED);
            tvMail.requestFocus();
            return true;
        }else {
            tvMail.setHintTextColor(Color.GRAY);
        }


        return false;
    }

    public void cargarAdaptador() {
        // Petición GET

        String newURL = Constantes.GET_CONDUCTOR_BY_ID + "?conductor=" + ls_id_conductor;
        Log.d(TAG, newURL);
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                newURL,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        procesarRespuestaCarga(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }

                        )
                );
    }
    private void procesarRespuestaCarga(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");

            switch (estado) {
                case "1": // EXITO

                    JSONArray mensaje = response.getJSONArray("conductor");

                    for(int i = 0; i < mensaje.length(); i++)
                    {JSONObject object = mensaje.getJSONObject(i);
                        conductor_titular con = new conductor_titular();

                        String id = object.getString("id");

                        con.setId(id);

                        String nombre = object.getString("nombre_solo");

                        con.setNombre(nombre);

                        String apellido = object.getString("apellido");

                        con.setApellido(apellido);

                        String mail = object.getString("mail");

                        con.setMail(mail);

                        String estadoUsuario = object.getString("estado");

                        con.setEstado(estadoUsuario);



                        usuarios.add(con);

                    }

                    carga_datos();
                    actualizar_foto();

                    break;
                case "2": // FALLIDO



                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    void clearGlideCache()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                Glide.get(getActivity().getApplicationContext()).clearDiskCache();
            }
        }.start();

        Glide.get(getActivity().getApplicationContext()).clearMemory();
    }
}


