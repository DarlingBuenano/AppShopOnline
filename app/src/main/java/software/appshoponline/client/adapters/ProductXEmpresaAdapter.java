package software.appshoponline.client.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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

public class ProductXEmpresaAdapter extends RecyclerView.Adapter<ProductXEmpresaAdapter.ViewHolder> {

    private ArrayList<Product> listaProductosXEmpresas;
    private SharedPreferences pref;
    private int usuario;
    private String requestKey;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private Context context;
    private DecimalFormat formatoMoneda;
    private Bundle bundle;
    private FragmentManager fragmentManager;

    public ProductXEmpresaAdapter(ArrayList<Product> products, Context context, FragmentManager fragmentManager, String requestKey){
        this.listaProductosXEmpresas = products;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.pref = context.getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        this.usuario = pref.getInt("usuario_id", 1);
        this.requestKey = requestKey;
        this.bundle = new Bundle();
        requestQueue = Volley.newRequestQueue(context);
        formatoMoneda = new DecimalFormat("#0.00");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra_prod_x_empresa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarInformacion(listaProductosXEmpresas.get(position));
    }

    @Override
    public int getItemCount() {
        return this.listaProductosXEmpresas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements Response.Listener<JSONObject>, Response.ErrorListener{
        ImageView imgProducto;
        TextView txtNombreProducto;
        ImageButton btnDisminuirCantidad;
        TextView txtVerCantidad;
        ImageButton btnAumentarCantidad;
        ImageButton btnEliminarProductoDelCarrito;
        TextView txtPrecioxCantidadProducto;
        Product producto;
        int cantidad = 1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProducto);
            btnDisminuirCantidad = itemView.findViewById(R.id.btnDisminuirCantidad);
            txtVerCantidad = itemView.findViewById(R.id.txtVerCantidad);
            btnAumentarCantidad = itemView.findViewById(R.id.btnAumentarCantidad);
            btnEliminarProductoDelCarrito = itemView.findViewById(R.id.btnEliminarProductoDelCarrito);
            txtPrecioxCantidadProducto = itemView.findViewById(R.id.txtPrecioxCantidadProducto);
        }

        public void asignarInformacion(Product producto){
            this.producto = producto;
            txtNombreProducto.setText(producto.Nombre);
            txtVerCantidad.setText(String.valueOf(cantidad));
            txtPrecioxCantidadProducto.setText("$ " + formatoMoneda.format(producto.Precio));

            String url = Dominio.URL_Media + producto.UrlImagen;

            ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imgProducto.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error al consultar la imagen del producto: " + producto.Nombre);
                }
            });
            requestQueue.add(imageRequest);

            btnDisminuirCantidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cantidad > 1){
                        cantidad = cantidad - 1;
                        actualizarCantidad(producto.Precio * -1);
                    }
                }
            });
            btnAumentarCantidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cantidad = cantidad + 1;
                    actualizarCantidad(producto.Precio);
                }
            });
            btnEliminarProductoDelCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Dominio.URL_WebServie + Constantes.URL_Eliminar_Producto_del_Carrito +"/"+usuario +"/"+producto.Id;
                    requestQueueGetVolley(url);
                }
            });
        }

        private void actualizarCantidad(double precio){
            producto.Cantidad = cantidad;
            bundle.putDouble(requestKey, precio);
            fragmentManager.setFragmentResult(requestKey, bundle);

            if (precio < 0)
                precio = precio * -1;
            txtVerCantidad.setText(String.valueOf(cantidad));
            txtPrecioxCantidadProducto.setText("$ " + formatoMoneda.format(cantidad * precio));
        }

        private void requestQueueGetVolley(String url){
            requestQueue = Volley.newRequestQueue(context);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            requestQueue.add(jsonObjectRequest);
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                // Accion eliminar
                if(response.getBoolean("accion")){
                    listaProductosXEmpresas.remove(this.producto);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Se ha quitado un producto de la lista", Toast.LENGTH_SHORT).show();

                    //Notificar al Adapter padre que se eliminó todos los items
                    bundle.putDouble(requestKey, listaProductosXEmpresas.size());
                    fragmentManager.setFragmentResult(requestKey, bundle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error al intentar convertir el json: " + e.getMessage());
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("Error al intentar quitar el producto de la lista del carrito: " + error.getMessage());
        }
    }
}
