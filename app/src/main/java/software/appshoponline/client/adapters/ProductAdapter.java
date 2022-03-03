package software.appshoponline.client.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import software.appshoponline.Constantes;
import software.appshoponline.Dominio;
import software.appshoponline.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Product> ListaProductos;
    private SharedPreferences pref;
    private int usuario;
    private RequestQueue requestQueue;
    private Context context;

    public ProductAdapter(ArrayList<Product> listaProductos, Context context){
        this.ListaProductos = listaProductos;
        this.context = context;
        this.pref = context.getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        this.usuario = pref.getInt("usuario_id", 1);
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarInformacion(ListaProductos.get(position));
    }

    @Override
    public int getItemCount() {
        return ListaProductos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtEmpresa;
        TextView txtPrecio_Unidad;
        ImageView imgProducto;
        Button btnLikeProducto;
        Button btnAgregarAlCarrito;
        JsonObjectRequest jsonObjectRequest;
        Product producto;
        boolean isBtnLike;
        private DecimalFormat formatoMoneda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProducto);
            txtEmpresa = itemView.findViewById(R.id.txtEmpresaProducto);
            txtPrecio_Unidad = itemView.findViewById(R.id.txtPrecioxUnidadProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            btnLikeProducto = itemView.findViewById(R.id.btnLikeProducto);
            btnAgregarAlCarrito = itemView.findViewById(R.id.btnAgregarAlCarrito);
            formatoMoneda = new DecimalFormat("#0.00");
        }

        public void asignarInformacion(Product producto){
            this.producto = producto;
            txtNombre.setText(producto.Nombre);
            txtEmpresa.setText(producto.Empresa);
            txtPrecio_Unidad.setText("$ " + formatoMoneda.format(producto.Precio) + " / " + producto.Unidad_Medida);

            if (producto.Like){
                btnLikeProducto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
            }
            btnLikeProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "";
                    if (producto.Like){
                        producto.Like = false;
                        btnLikeProducto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_like, 0, 0, 0);
                        url = Dominio.URL_WebServie + Constantes.URL_Eliminar_Producto_Favorito +"/"+usuario +"/"+producto.Id;
                    }
                    else{
                        producto.Like = true;
                        btnLikeProducto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                        url = Dominio.URL_WebServie + Constantes.URL_Guardar_Producto_Favorito +"/"+usuario +"/"+producto.Id;
                    }
                    isBtnLike = true;
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
                    requestQueue.add(jsonObjectRequest);
                }
            });

            btnAgregarAlCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isBtnLike = false;
                    String url = Dominio.URL_WebServie + Constantes.URL_Agregar_Producto_al_Carrito + "/"+usuario + "/"+producto.Id;
                    jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
                    requestQueue.add(jsonObjectRequest);
                }
            });

            ImageRequest imageRequest = new ImageRequest(producto.UrlImagen,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            imgProducto.setImageBitmap(response);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(itemView.getContext(), "Error al consultar la imagen", Toast.LENGTH_SHORT).show();
                    System.out.println("Error al consultar la imagen del producto: " + producto.Nombre);
                }
            });
            requestQueue.add(imageRequest);
        }

        private Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("accion")){
                        if (!isBtnLike){
                            Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.getInt("categoria") != 1){
                            ListaProductos.remove(producto);
                            notifyDataSetChanged();
                        }
                    }
                    else{
                        if (!isBtnLike){
                            Toast.makeText(context, "El producto ya habia sido añadido al carrito", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    if (!isBtnLike)
                        System.out.println("Error al guardar el producto al carrito: " + e.getMessage());
                    else
                        System.out.println("Error al guardar el producto a favoritos: " + e.getMessage());
                }
            }
        };

        private Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error: " + error.getMessage());
            }
        };
    }
}
