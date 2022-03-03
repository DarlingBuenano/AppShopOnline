package software.appshoponline.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;
import software.appshoponline.client.adapters.Mensaje;
import software.appshoponline.client.adapters.MensajeAdapter;

public class MensajesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    SharedPreferences pref;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    View root;
    ArrayList<Mensaje> ListaMensajes;
    MensajeAdapter mensajeAdapter;

    String imagen_Url;
    String nombre_Chat;
    String pedido;
    int usuario_id;
    int empresa_id;
    int usuario_empresa_id;
    int sala_id;

    private ImageButton btnRegresar;
    private ImageView imgChat;
    private TextView txtNombreChat;
    private RecyclerView recyclerMensajes;
    private TextView txtMensajeAEnviar;
    private ImageButton btnEnviarMensaje;

    Date date;
    SimpleDateFormat formatter;

    public MensajesFragment() {
        // Required empty public constructor
    }

    public static MensajesFragment newInstance(String param1, String param2) {
        MensajesFragment fragment = new MensajesFragment();
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

            usuario_empresa_id = getArguments().getInt("usuario_empresa_id");
            empresa_id = getArguments().getInt("empresa_id");
            usuario_id = getArguments().getInt("usuario_id");
            nombre_Chat = getArguments().getString("nombre_chat");
            imagen_Url = getArguments().getString("img_url");
            pedido = getArguments().getString("pedido");
            sala_id = getArguments().getInt("sala_id");
        }
        requestQueue = Volley.newRequestQueue(getContext());

        formatter = new SimpleDateFormat("HH:mm");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mensajes, container, false);

        recyclerMensajes = root.findViewById(R.id.recyclerListaMensajes);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        ListaMensajes = new ArrayList<Mensaje>();

        btnRegresar = root.findViewById(R.id.frgmsj_btnRegresar);
        btnRegresar.setOnClickListener(clickBtnRegresar);
        imgChat = root.findViewById(R.id.frgmsj_imgFotoPerfil);
        txtNombreChat = root.findViewById(R.id.frgmsj_txtNombreChat);
        txtMensajeAEnviar = root.findViewById(R.id.frgmsj_txtMensaje);
        btnEnviarMensaje = root.findViewById(R.id.frgmsj_btnEnviar);
        btnEnviarMensaje.setOnClickListener(clickBtnEnviarMensaje);

        txtNombreChat.setText(nombre_Chat);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (pedido != null){
            mensajeAdapter = new MensajeAdapter(ListaMensajes, getContext(), usuario_empresa_id);
            enviarMensaje(Dominio.URL_WebServie + Constantes.URL_Registrar_Mensaje_x_Usuario, pedido);
            recyclerMensajes.setAdapter(mensajeAdapter);
        }
        else{
            cargarMensajesAntiguos();
            //mensajeAdapter = new MensajeAdapter(ListaMensajes, getContext(), usuario_empresa_id);
        }
        //obtenerDatosEmpresa(empresa_id);
        cargarImagenPerfil();
    }

    private View.OnClickListener clickBtnRegresar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().finish();
        }
    };

    private void cargarMensajesAntiguos(){
        String url = Dominio.URL_WebServie + Constantes.URL_Mostrar_Mensajes_x_Sala +"/" +sala_id;
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    private void mostrarMensajesAntiguosEnElChat(JSONArray arrayMensajes){
        try {
            for (int i = 0; i < arrayMensajes.length(); i++) {
                JSONObject mensaje = arrayMensajes.getJSONObject(i);
                ListaMensajes.add(new Mensaje(
                        mensaje.getInt("usuario_id"),
                        mensaje.getString("texto"),
                        mensaje.getString("hora")
                ));
            }
            mensajeAdapter = new MensajeAdapter(ListaMensajes, getContext(), usuario_empresa_id);
            recyclerMensajes.setAdapter(mensajeAdapter);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarImagenPerfil(){
        ImageRequest imageRequest = new ImageRequest(Dominio.URL_Media + imagen_Url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgChat.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(itemView.getContext(), "Error al traer la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(imageRequest);
    }

    private View.OnClickListener clickBtnEnviarMensaje = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            enviarMensaje(Dominio.URL_WebServie + Constantes.URL_Registrar_Mensaje_x_Usuario, txtMensajeAEnviar.getText().toString());
        }
    };

    private void enviarMensaje(String url, String msj){
        date = new Date();
        //response.getString("hora")
        mostrarMensajeEnElChat(formatter.format(date), msj);
        HashMap<String, String> paramPost = new HashMap<>();
        paramPost.put("usuario_id", String.valueOf(usuario_id));
        paramPost.put("sala_id", String.valueOf(sala_id));
        paramPost.put("mensaje", msj);

        JSONObject jsonObject = new JSONObject(paramPost);

        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, errorListener);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    private void mostrarMensajeEnElChat(String hora, String msj){
        Mensaje mensaje = new Mensaje(usuario_id, msj, hora);
        ListaMensajes.add(mensaje);
        txtMensajeAEnviar.setText("");
    }

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    //System.out.println("Mensaje gaurdado con exito");
                    if (response.getString("metodo").equals("obtener")){
                        mostrarMensajesAntiguosEnElChat(response.getJSONArray("datos"));
                    }
                }
                else{
                    Toast.makeText(getContext(), "No se pudo guardar el mensaje", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getContext(), "Ocurrió un error al intentar conectarse con el servidor", Toast.LENGTH_SHORT).show();
        }
    };

    /*private void obtenerDatosEmpresa(int empresa_id){
        String url = Dominio.URL_WebServie + Constantes.URL_Obtener_Datos_De_Empresa + "/"+empresa_id;

        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("accion")){
                        JSONObject empresa = response.getJSONObject("datos");
                        String urlImagen = empresa.getString("url_imagen");
                        cargarImagenPerfil(Dominio.URL_Media + urlImagen);
                    }
                    else{
                        Toast.makeText(getContext(), "NO se pudo guardar el mensaje", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Ocurrió un error al intentar conectarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
        this.requestQueue.add(this.jsonObjectRequest);
    }*/
}