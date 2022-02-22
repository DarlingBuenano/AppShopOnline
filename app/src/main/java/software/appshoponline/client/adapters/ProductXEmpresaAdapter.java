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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import software.appshoponline.Constantes;
import software.appshoponline.R;

public class ProductXEmpresaAdapter extends RecyclerView.Adapter<ProductXEmpresaAdapter.ViewHolder>{

    private ArrayList<Product> listaProductosXEmpresas;
    private SharedPreferences pref;
    private int usuario;
    private RequestQueue requestQueue;
    private Context context;

    public ProductXEmpresaAdapter(ArrayList<Product> products, Context context){
        this.listaProductosXEmpresas = products;
        this.context = context;
        this.pref = context.getSharedPreferences("shared_login_data", Context.MODE_PRIVATE);
        this.usuario = pref.getInt("usuario_id", 1);
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra_prod_x_empresa, null, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView txtNombreProducto;
        Button btnDisminuirCantidad;
        TextView txtVerCantidad;
        Button btnAumentarCantidad;
        Button btnEliminarProductoDelCarrito;
        TextView txtPrecioxCantidadProducto;

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
            txtNombreProducto.setText(producto.Nombre);
            txtPrecioxCantidadProducto.setText(String.valueOf(producto.Precio));

            String url = Constantes.URL_MEDIA + producto.UrlImagen;

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
        }
    }
}
