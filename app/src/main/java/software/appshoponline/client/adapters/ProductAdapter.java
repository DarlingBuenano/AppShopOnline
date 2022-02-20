package software.appshoponline.client.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    ArrayList<Product> ListaProductos;

    public ProductAdapter(ArrayList<Product> listaProductos){
        this.ListaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, null, false);
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
        RequestQueue requestQueue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProducto);
            txtEmpresa = itemView.findViewById(R.id.txtEmpresaProducto);
            txtPrecio_Unidad = itemView.findViewById(R.id.txtPrecioxUnidadProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);

            requestQueue = Volley.newRequestQueue(itemView.getContext());
        }

        public void asignarInformacion(Product producto){
            txtNombre.setText(producto.Nombre);
            txtEmpresa.setText(producto.Empresa);
            String precio = String.valueOf(producto.Precio);
            txtPrecio_Unidad.setText("$ " + precio + " / " + producto.Unidad_Medida);

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
    }
}
