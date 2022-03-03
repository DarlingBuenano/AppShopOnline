package software.appshoponline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.util.Base64;
import java.util.HashMap;

import software.appshoponline.business.HomeBusiActivity;
import software.appshoponline.fragments.OpcionesDeRegistro;
import software.appshoponline.fragments.RegisterBusiness;
import software.appshoponline.fragments.RegisterClient;

public class RegisterContainer extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    Fragment frgOpcionesDeRegistro;
    Fragment frgRegisterBusiness;
    Fragment frgRegisterClient;
    FragmentTransaction transaction;
    Bitmap imgFotoPerfil;
    TextView txtNombreDeLaImagen;
    View btnRegistrar;
    String tag;
    String imgString;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_container);

        frgOpcionesDeRegistro = new OpcionesDeRegistro();
        frgRegisterBusiness = new RegisterBusiness();
        frgRegisterClient = new RegisterClient();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.actReg_frg_register_container, frgOpcionesDeRegistro);
        transaction.commit();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public void clickRegisterBusiness(View view){
        transaction = getSupportFragmentManager().beginTransaction();
        tag = "frgRegisterBusiness";
        if (frgRegisterBusiness.isAdded()){
            transaction.hide(frgOpcionesDeRegistro);
            transaction.show(frgRegisterBusiness);
        }
        else {
            transaction.hide(frgOpcionesDeRegistro);
            transaction.add(R.id.actReg_frg_register_container, frgRegisterBusiness, tag);
        }
        transaction.commit();
    }

    public void clickRegisterClient(View view){
        transaction = getSupportFragmentManager().beginTransaction();
        tag = "frgRegisterClient";
        if (frgRegisterClient.isAdded()){
            transaction.hide(frgOpcionesDeRegistro);
            transaction.show(frgRegisterClient);
        }
        else{
            transaction.hide(frgOpcionesDeRegistro);
            transaction.add(R.id.actReg_frg_register_container, frgRegisterClient, tag);
        }
        transaction.commit();
    }

    public void clicRegresarOpcDeRegistro(View view){
        transaction = getSupportFragmentManager().beginTransaction();
        if(tag.equals("frgRegisterBusiness")){
            transaction.hide(frgRegisterBusiness);
            transaction.show(frgOpcionesDeRegistro);
        }
        else{
            transaction.hide(frgRegisterClient);
            transaction.show(frgOpcionesDeRegistro);
        }
        transaction.commit();
    }

    public void cargarFotoBusiness(View view){
        txtNombreDeLaImagen = findViewById(R.id.frgRegBusiness_txtNombreDeLaImagen);
        cargarFoto();
    }

    public void cargarFotoClient(View view){
        txtNombreDeLaImagen = findViewById(R.id.frgRegClient_txtNombreDeLaImagen);
        cargarFoto();
    }

    private void cargarFoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            try {
                Toast.makeText(this, "Se cargó la imagen", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();

                int ultimaBarra = uri.getPath().lastIndexOf("/");
                txtNombreDeLaImagen.setText(uri.getPath().substring(ultimaBarra));

                imgFotoPerfil = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imgString = convertirBitmapAString(imgFotoPerfil);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertirBitmapAString(Bitmap bitmap){
        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayStream);
        byte[] imagenByte = arrayStream.toByteArray();
        String imageString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        return imageString;
    }

    public void clicRegistrarBusiness(View view){
        btnRegistrar = view;
        btnRegistrar.setEnabled(false);
        TextView txtNombres = findViewById(R.id.frgRegBusiness_txtNombres);
        TextView txtApellidos = findViewById(R.id.frgRegBusiness_txtApellidos);
        TextView txtCedula = findViewById(R.id.frgRegBusiness_txtCedula);
        TextView txtCiudad = findViewById(R.id.frgRegBusiness_txtCiudad);
        TextView txtDireccion = findViewById(R.id.frgRegBusiness_txtDireccion);
        TextView txtCelular = findViewById(R.id.frgRegBusiness_txtCelular);
        TextView txtCorreo = findViewById(R.id.frgRegBusiness_txtCorreo);
        TextView txtContrasena = findViewById(R.id.frgRegBusiness_txtContrasena);

        TextView txtNombreEmpresa = findViewById(R.id.frgRegBusiness_txtNombreEmpresa);
        TextView txtSlogan = findViewById(R.id.frgRegBusiness_txtSlogan);
        TextView txtCostoEnvio = findViewById(R.id.frgRegBusiness_txtCostoEnvio);
        TextView txtRuc = findViewById(R.id.frgRegBusiness_txtRUC);

        HashMap<String, String> parametrosPost = new HashMap<>();
        parametrosPost.put("nombres", txtNombres.getText().toString());
        parametrosPost.put("apellidos", txtApellidos.getText().toString());
        parametrosPost.put("cedula", txtCedula.getText().toString());
        parametrosPost.put("ciudad", txtCiudad.getText().toString());
        parametrosPost.put("direccion", txtDireccion.getText().toString());
        parametrosPost.put("celular", txtCelular.getText().toString());
        parametrosPost.put("correo", txtCorreo.getText().toString());
        parametrosPost.put("contrasena", txtContrasena.getText().toString());
        parametrosPost.put("nombre_empresa", txtNombreEmpresa.getText().toString());
        parametrosPost.put("slogan", txtSlogan.getText().toString());
        parametrosPost.put("costo_envio", txtCostoEnvio.getText().toString());
        parametrosPost.put("ruc", txtRuc.getText().toString());
        parametrosPost.put("imagen", imgString);

        String url = Dominio.URL_WebServie + Constantes.URL_Registrar_Empresa;

        JSONObject jsonObject = new JSONObject(parametrosPost);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    public void clicRegistrarCliente(View view){
        btnRegistrar = view;
        btnRegistrar.setEnabled(false);
        TextView txtNombres = findViewById(R.id.frgRegClient_txtNombres);
        TextView txtApellidos = findViewById(R.id.frgRegClient_txtApellidos);
        TextView txtCedula = findViewById(R.id.frgRegClient_txtCedula);
        TextView txtCiudad = findViewById(R.id.frgRegClient_txtCiudad);
        TextView txtDireccion = findViewById(R.id.frgRegClient_txtDireccion);
        TextView txtCelular = findViewById(R.id.frgRegClient_txtCelular);
        TextView txtCorreo = findViewById(R.id.frgRegClient_txtCorreo);
        TextView txtContrasena = findViewById(R.id.frgRegClient_txtContrasena);

        HashMap<String, String> parametrosPost = new HashMap<>();
        parametrosPost.put("nombres", txtNombres.getText().toString());
        parametrosPost.put("apellidos", txtApellidos.getText().toString());
        parametrosPost.put("cedula", txtCedula.getText().toString());
        parametrosPost.put("ciudad", txtCiudad.getText().toString());
        parametrosPost.put("direccion", txtDireccion.getText().toString());
        parametrosPost.put("celular", txtCelular.getText().toString());
        parametrosPost.put("correo", txtCorreo.getText().toString());
        parametrosPost.put("contrasena", txtContrasena.getText().toString());
        parametrosPost.put("imagen", imgString);

        String url = Dominio.URL_WebServie + Constantes.URL_Registrar_Cliente;

        JSONObject jsonObject = new JSONObject(parametrosPost);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.getBoolean("accion")){
                SharedPreferences pref;
                SharedPreferences.Editor editor;
                pref = getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
                editor = pref.edit();
                editor.putInt("usuario_id", response.getInt("usuario"));
                editor.apply();

                if (response.getString("rol_usuario").equals("Cliente")){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    editor.putInt("empresa_id", response.getInt("empresa"));
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), HomeBusiActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else{
                Toast.makeText(this, "No se pudo guardar los datos", Toast.LENGTH_SHORT).show();
                btnRegistrar.setEnabled(true);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error al subir los datos", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Ha ocurrido un error en el servidor", Toast.LENGTH_SHORT).show();
        System.out.println("Error: " + error.getMessage());
    }
}