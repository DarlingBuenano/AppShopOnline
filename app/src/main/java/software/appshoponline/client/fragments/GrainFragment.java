package software.appshoponline.client.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;

import software.appshoponline.Constantes;
import software.appshoponline.R;
import software.appshoponline.client.adapters.Product;
import software.appshoponline.client.adapters.ProductAdapter;

public class GrainFragment extends Fragment
        implements Response.Listener<JSONObject>, Response.ErrorListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ArrayList<Product> ListaProductos;
    private RecyclerView recycler;
    private View root;
    private ImageButton btnBuscar;
    private EditText txtBuscar;
    private Spinner spinner_filtro;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    public GrainFragment() {
        // Required empty public constructor
    }

    public static GrainFragment newInstance(String param1, String param2) {
        GrainFragment fragment = new GrainFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_grain, container, false);
        recycler = root.findViewById(R.id.recyclerCardGranos);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        //Llamado al webservice
        String url = Constantes.URL_BASE + Constantes.URL_Mostrar_Productos_x_Categoria + "/3";
        this.requestQueueGetVolley(url);

        txtBuscar = root.findViewById(R.id.frggrain_txtBuscar);
        btnBuscar = root.findViewById(R.id.frggrain_btnBuscar);
        btnBuscar.setOnClickListener(btnBuscarProducto);

        // Cargar Spinner
        spinner_filtro = root.findViewById(R.id.frggrain_spinner_filtro);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                root.getContext(), R.array.spinner_filtros_cliente, android.R.layout.simple_spinner_dropdown_item);
        spinner_filtro.setAdapter(adapter);

        return root;
    }

    private View.OnClickListener btnBuscarProducto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String producto = txtBuscar.getText().toString();
            String url = Constantes.URL_BASE + Constantes.URL_Buscar_Productos_x_Categoria + "/3/" + producto;
            requestQueueGetVolley(url);
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
                        Constantes.URL_MEDIA + jsonListaProductos.getJSONObject(i).getString("url_imagen"),
                        producto.getString("nombre"),
                        producto.getString("empresa"),
                        producto.getDouble("precio"),
                        producto.getString("unidad_medida"),
                        producto.getInt("categoria"),
                        producto.getBoolean("like")
                ));
            }
            ProductAdapter productAdapter = new ProductAdapter(ListaProductos, getContext());
            this.recycler.setAdapter(productAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error al procesar los productos: " + e.getMessage());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(root.getContext(), "No se ha podido cargar los datos", Toast.LENGTH_SHORT).show();
        System.out.println("**********************");
        System.out.println();
        System.out.println("Error: " + error);
        System.out.println();
        System.out.println("**********************");
    }
}