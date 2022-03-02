package software.appshoponline.business.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.LoginActivity;
import software.appshoponline.R;
import software.appshoponline.business.activitys.EditUserAccount;

public class AccountBusiFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    View root;
    int empresa_id;
    int usuario_id;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    TextView txtNombresUsuario;
    TextView txtApellidosUsuario;

    String urlImagen;
    ImageView imgFotoPerfil;

    TextView txtRuc;
    TextView txtNombreEmpresa;
    TextView txtSlogan;
    TextView txtCostoEnvio;

    Button btnCambiarFotoPerfil;
    Button btnConfiguracionUsuario;
    Button btnConfiguracionEmpresa;
    Button btnCerrarSesion;

    AlertDialog.Builder builder;
    private ActivityResultLauncher<Intent> mStartForResult;

    SharedPreferences pref;

    public String nombreImagen;
    public Bitmap imgFotoBitmap;
    public String imgFotoString;
    public Boolean seCambioLaImagen = false;

    public AccountBusiFragment() {
        // Required empty public constructor
    }

    public static AccountBusiFragment newInstance(String param1, String param2) {
        AccountBusiFragment fragment = new AccountBusiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pref = getContext().getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        empresa_id = pref.getInt("empresa_id", 1);
        usuario_id = pref.getInt("usuario_id", 1);
        this.requestQueue = Volley.newRequestQueue(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.registrarActivityResult();
        root = inflater.inflate(R.layout.fragment_account_busi, container, false);

        this.imgFotoPerfil = root.findViewById(R.id.frgaccount_imgPerfil);

        this.txtNombreEmpresa = root.findViewById(R.id.frgaccount_txtNombreEmpresa);
        this.txtSlogan = root.findViewById(R.id.frgaccount_txtSlogan);
        this.txtNombresUsuario = root.findViewById(R.id.frgaccount_txtNombresUsuario);
        this.txtApellidosUsuario = root.findViewById(R.id.frgaccount_txtApellidosUsuario);

        this.btnCambiarFotoPerfil = root.findViewById(R.id.frgaccount_btnCambiarFotoPerfil);
        this.btnConfiguracionUsuario = root.findViewById(R.id.frgaccount_btnConfiguracionUsuario);
        this.btnConfiguracionEmpresa = root.findViewById(R.id.frgaccount_btnConfiguracionEmpresa);
        this.btnCerrarSesion = root.findViewById(R.id.frgaccount_btnCerrarSesion);
        this.btnCambiarFotoPerfil.setOnClickListener(clicCambiarFotoPerfil);
        this.btnConfiguracionUsuario.setOnClickListener(clicConfiguracionUsuario);
        this.btnConfiguracionEmpresa.setOnClickListener(clicConfiguracionEmpresa);
        this.btnCerrarSesion.setOnClickListener(clicCerrarSesion);

        this.mostrarDatosPrevios();

        return root;
    }

    private View.OnClickListener clicCambiarFotoPerfil = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cargarFotoDesdeGaleria();
        }
    };

    private View.OnClickListener clicConfiguracionUsuario = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), EditUserAccount.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener clicConfiguracionEmpresa = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View dialog_layout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_business, null);

            txtRuc = dialog_layout.findViewById(R.id.dialogEmpresa_txtRUC);
            txtNombreEmpresa = dialog_layout.findViewById(R.id.dialogEmpresa_txtNombreEmpresa);
            txtSlogan = dialog_layout.findViewById(R.id.dialogEmpresa_txtSlogan);
            txtCostoEnvio = dialog_layout.findViewById(R.id.dialogEmpresa_txtCostoEnvio);

            builder = new AlertDialog.Builder(getContext());
            builder.setView(dialog_layout);
            builder.setNegativeButton(R.string.dialog_cancelar, null);
            builder.setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, String> parametrosPost = new HashMap<>();
                    parametrosPost.put("id", String.valueOf(empresa_id));
                    parametrosPost.put("ruc", txtRuc.getText().toString());
                    parametrosPost.put("nombre_empresa", txtNombreEmpresa.getText().toString());
                    parametrosPost.put("slogan", txtSlogan.getText().toString());
                    parametrosPost.put("costo_envio", txtCostoEnvio.getText().toString());

                    String url = Dominio.URL_WebServie + Constantes.URL_Actualizar_Datos_Empresa;
                    JSONObject jsonObject = new JSONObject(parametrosPost);
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, responseErrorListener);
                    requestQueue.add(jsonObjectRequest);
                }
            });
            builder.create().show();

            String url = Dominio.URL_WebServie + Constantes.URL_Obtener_Datos_De_Empresa + "/" + empresa_id;
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseErrorListener);
            requestQueue.add(jsonObjectRequest);
        }
    };

    private View.OnClickListener clicCerrarSesion = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("sesion");
            editor.remove("usuario_id");
            editor.remove("empresa_id");
            editor.clear();
            editor.commit();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    if (response.getString("metodo").equals("actualizar")){
                        Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.getString("metodo").equals("obtener")){
                        JSONObject datos = response.getJSONObject("datos");
                        txtRuc.setText( datos.getString("ruc"));
                        txtNombreEmpresa.setText( datos.getString("nombre_empresa"));
                        txtSlogan.setText( datos.getString("slogan"));
                        txtCostoEnvio.setText( datos.getString("costo_envio"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getContext(), "Ocurrió un error en el servidor", Toast.LENGTH_SHORT).show();
        }
    };

    private void mostrarDatosPrevios(){
        String url = Dominio.URL_WebServie + Constantes.URL_Obtener_Cuenta_Empresa +"/"+usuario_id + "/"+empresa_id;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject datos = response.getJSONObject("datos");
                    System.out.println(datos);
                    urlImagen = datos.getString("url_imagen");
                    txtNombreEmpresa.setText(datos.getString("nombre_empresa"));
                    txtSlogan.setText(datos.getString("slogan"));
                    txtNombresUsuario.setText(datos.getString("nombres_usuario"));
                    txtApellidosUsuario.setText(datos.getString("apellidos_usuario"));

                    mostrarImagen(Dominio.URL_Media + urlImagen);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, responseErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    private void mostrarImagen(String url){
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgFotoPerfil.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error al traer la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(imageRequest);
    }

    private void guardarFotoPerfil(){
        HashMap<String, String> parametrosPost = new HashMap<>();
        parametrosPost.put("id", String.valueOf(usuario_id));
        parametrosPost.put("imagen", imgFotoString);

        String url = Dominio.URL_WebServie + Constantes.URL_Actualizar_Foto_Perfil;
        JSONObject jsonObject = new JSONObject(parametrosPost);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, responseErrorListener);
        requestQueue.add(jsonObjectRequest);
    }

    private void registrarActivityResult(){
        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Uri uri = result.getData().getData();
                            int ultimaBarra = uri.getPath().lastIndexOf("/");
                            nombreImagen = uri.getPath().substring(ultimaBarra);
                            try {
                                imgFotoBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                                imgFotoPerfil.setImageBitmap(imgFotoBitmap);
                                imgFotoString = convertirBitmapAString(imgFotoBitmap);
                                seCambioLaImagen = true;
                                guardarFotoPerfil();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cargarFotoDesdeGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mStartForResult.launch(intent);
    }

    public static String convertirBitmapAString(Bitmap bitmap){
        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayStream);
        byte[] imagenByte = arrayStream.toByteArray();
        String imageString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        return imageString;
    }
}