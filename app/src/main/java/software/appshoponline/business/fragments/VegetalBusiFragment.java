package software.appshoponline.business.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;
import software.appshoponline.business.adapters.ProductBusiAdapter;
import software.appshoponline.client.adapters.Product;

public class VegetalBusiFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private int empresa_id;
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

    public VegetalBusiFragment() {
        // Required empty public constructor
    }

    public static VegetalBusiFragment newInstance(String param1, String param2) {
        VegetalBusiFragment fragment = new VegetalBusiFragment();
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
        root = inflater.inflate(R.layout.fragment_vegetal_busi, container, false);
        recycler = root.findViewById(R.id.recyclerCardVegetalesBusi);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        txtBuscar = root.findViewById(R.id.frgvegetalbusi_txtBuscar);
        btnBuscar = root.findViewById(R.id.frgvegetalbusi_btnBuscar);
        btnBuscar.setOnClickListener(btnBuscarProducto);
        btnAgregar = root.findViewById(R.id.frgvegetalbusi_btnAgregar);
        btnAgregar.setOnClickListener(btnAgregarProducto);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        url = Dominio.URL_WebServie + Constantes.URL_Mostrar_Productos_x_Empresa_x_Categoria +"/"+empresa_id +"/1";
        this.requestQueueGetVolley(url);
    }

    private View.OnClickListener btnBuscarProducto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String producto = txtBuscar.getText().toString();
            url = Dominio.URL_WebServie + Constantes.URL_Buscar_Productos_x_Empresa_x_Categoria +"/"+empresa_id + "/1/" + producto;
            requestQueueGetVolley(url);
        }
    };

    private View.OnClickListener btnAgregarProducto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getContext(), "Agregar producto no implementado", Toast.LENGTH_SHORT).show();
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
            System.out.println("Si hay datos");
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
}