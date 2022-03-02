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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;
import software.appshoponline.Utilities;
import software.appshoponline.business.adapters.ProductBusiAdapter;
import software.appshoponline.client.adapters.Product;

public class GrainBusiFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private int empresa_id;
    private static String categoria_id = "3";
    private SharedPreferences pref;
    private String url;

    private ArrayList<Product> ListaProductos;
    private RecyclerView recycler;
    private View root;
    private ImageButton btnBuscar;
    private EditText txtBuscar;
    private LinearLayout btnAgregar;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    private Bitmap imgBitmap;
    private String imgString;
    private String imgNombre;

    private ActivityResultLauncher<Intent> mStartForResult;
    private View dialog_layout;
    private AlertDialog.Builder builder;
    private ImageView dialog_imgProducto;
    private Button dialog_btnCambiarFoto;
    private EditText dialog_txtNombreProducto;
    private EditText dialog_txtPrecio;
    private Spinner unidadesMedidas;
    private Boolean seCambioLaFoto = false;

    public GrainBusiFragment() {
        // Required empty public constructor
    }

    public static GrainBusiFragment newInstance(String param1, String param2) {
        GrainBusiFragment fragment = new GrainBusiFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_grain_busi, container, false);
        recycler = root.findViewById(R.id.recyclerCardGranosBusi);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        txtBuscar = root.findViewById(R.id.frggrainbusi_txtBuscar);
        btnBuscar = root.findViewById(R.id.frggrainbusi_btnBuscar);
        btnBuscar.setOnClickListener(btnBuscarProducto);
        btnAgregar = root.findViewById(R.id.frggrainbusi_btnAgregar);
        btnAgregar.setOnClickListener(btnAgregarProducto);

        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Uri uri = result.getData().getData();
                            int ultimaBarra = uri.getPath().lastIndexOf("/");
                            imgNombre = uri.getPath().substring(ultimaBarra);
                            try {
                                imgBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                                dialog_imgProducto.setImageBitmap(imgBitmap);
                                imgString = Utilities.convertirBitmapAString(imgBitmap);
                                seCambioLaFoto = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        url = Dominio.URL_WebServie + Constantes.URL_Mostrar_Productos_x_Empresa_x_Categoria +"/"+empresa_id +"/"+categoria_id;
        this.requestQueueGetVolley(url);
    }

    private View.OnClickListener btnBuscarProducto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String producto = txtBuscar.getText().toString();
            url = Dominio.URL_WebServie + Constantes.URL_Buscar_Productos_x_Empresa_x_Categoria +"/"+empresa_id + "/"+categoria_id+"/" + producto;
            requestQueueGetVolley(url);
        }
    };

    private View.OnClickListener btnAgregarProducto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            abrirDialogoAgregarProducto();
        }
    };

    private void requestQueueGetVolley(String url){
        this.requestQueue = Volley.newRequestQueue(getContext());
        this.jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        this.requestQueue.add(this.jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray jsonListaProductos = response.getJSONArray("productos");
            this.ListaProductos = new ArrayList<Product>();
            for(int i = 0; i < jsonListaProductos.length(); i++){
                JSONObject producto = jsonListaProductos.getJSONObject(i);
                ListaProductos.add(new Product(
                        producto.getInt("id"),
                        Dominio.URL_Media + jsonListaProductos.getJSONObject(i).getString("url_imagen"),
                        producto.getString("nombre"),
                        producto.getDouble("precio"),
                        producto.getString("unidad_medida")
                ));
            }
            ProductBusiAdapter productBusiAdapter = new ProductBusiAdapter(ListaProductos, getContext());
            this.recycler.setAdapter(productBusiAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error al procesar los productos: " + e.getMessage());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(root.getContext(), "No se ha podido cargar los datos", Toast.LENGTH_SHORT).show();
        System.out.println("**********************");
        System.out.println("Error: " + error);
        System.out.println("**********************");
    }

    private void abrirDialogoAgregarProducto(){
        dialog_layout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_product, null);
        builder = new AlertDialog.Builder(getContext());
        builder.setView(dialog_layout);
        builder.setNegativeButton(R.string.dialog_cancelar, null);
        builder.setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //validar campos
                if (seCambioLaFoto &&
                        dialog_txtNombreProducto.getText().toString()!="" &&
                        dialog_txtPrecio.getText().toString()!=""){
                    HashMap<String, String> parametrosPost = new HashMap<>();
                    parametrosPost.put("empresa_id", String.valueOf(pref.getInt("empresa_id", 0)));
                    parametrosPost.put("categoria_id", categoria_id);
                    parametrosPost.put("nombre", dialog_txtNombreProducto.getText().toString());
                    parametrosPost.put("precio", dialog_txtPrecio.getText().toString());
                    parametrosPost.put("unidad_medida", Constantes.UNIDADES_DE_MDIDA[unidadesMedidas.getSelectedItemPosition()]);
                    parametrosPost.put("imagen", imgString);
                    String url = Dominio.URL_WebServie + Constantes.URL_Agregar_Producto_x_Empresa;
                    JSONObject jsonObject = new JSONObject(parametrosPost);
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, responseListener, responseErrorListener);
                    requestQueue.add(jsonObjectRequest);
                }
                else{
                    Toast.makeText(getContext(), "Faltan campos por llenar", Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
                }
            }
        });
        builder.create().show();

        dialog_imgProducto = dialog_layout.findViewById(R.id.dialog_imgProducto);
        dialog_btnCambiarFoto = dialog_layout.findViewById(R.id.dialog_btnCambiarImagen);
        dialog_btnCambiarFoto.setText("Agregar foto");
        dialog_btnCambiarFoto.setOnClickListener(clicBtnCambiarFoto);

        dialog_txtNombreProducto = dialog_layout.findViewById(R.id.dialog_txtNombreProducto);
        dialog_txtPrecio = dialog_layout.findViewById(R.id.dialog_txtPrecio);

        unidadesMedidas = dialog_layout.findViewById(R.id.dialog_spinnerUnidadMedida);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                dialog_layout.getContext(), R.array.spinner_unidades_de_medidas, android.R.layout.simple_spinner_dropdown_item);
        unidadesMedidas.setAdapter(adapter);
    }

    private View.OnClickListener clicBtnCambiarFoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            abrirFotoDesdegaleria();
        }
    };

    private void abrirFotoDesdegaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mStartForResult.launch(intent);
    }

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response.getBoolean("accion")){
                    Toast.makeText(getContext(), "Producto agregado con éxito", Toast.LENGTH_SHORT).show();
                    seCambioLaFoto = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ocurrió un error al agregar el producto", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getContext(), "Ocurrió un error con el servidor", Toast.LENGTH_SHORT).show();
        }
    };
}