package software.appshoponline.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;
import software.appshoponline.Dominio;
import software.appshoponline.MensajeActivity;
import software.appshoponline.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private ArrayList<Chat> ListaChats;
    private Context context;
    private RequestQueue requestQueue;

    public ChatAdapter(ArrayList<Chat> listaChats, Context context){
        this.ListaChats = listaChats;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.asignarInformacion(this.ListaChats.get(position));
    }

    @Override
    public int getItemCount() {
        return this.ListaChats.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        //Atributos
        CircleImageView imgChat;
        TextView txtNombreChat;
        TextView txtMensajeCortoChat;
        TextView txtHoraUltimoMensaje;
        LinearLayout btnAbrirChat;
        Chat chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgChat = itemView.findViewById(R.id.chat_imgPerfil);
            txtNombreChat = itemView.findViewById(R.id.chat_txtNombre);
            txtMensajeCortoChat = itemView.findViewById(R.id.chat_txtMensaje);
            txtHoraUltimoMensaje = itemView.findViewById(R.id.chat_txtHora);
            btnAbrirChat = itemView.findViewById(R.id.chat_btnAbrirChat);
        }

        public void asignarInformacion(Chat chat){
            this.chat = chat;
            txtNombreChat.setText(chat.NombreChat);
            if (chat.MensajeCorto.length() > 30){
                String msj = chat.MensajeCorto.replace("\n", " ");
                msj = msj.substring(0, 26) + "...";
                txtMensajeCortoChat.setText(msj);
            }
            else{
                txtMensajeCortoChat.setText(chat.MensajeCorto);
            }
            txtHoraUltimoMensaje.setText(chat.HoraUltimoMensaje.substring(0, 5));
            btnAbrirChat.setOnClickListener(clicBtnAbrirChat);

            cargarImagenPerfil(Dominio.URL_Media + chat.ImagenUrl);
        }

        public void cargarImagenPerfil(String imgUrl){
            ImageRequest imageRequest = new ImageRequest(imgUrl,
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

        private View.OnClickListener clicBtnAbrirChat = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MensajeActivity.class);
                intent.putExtra("sala_id", chat.Sala_id);
                intent.putExtra("usuario_id", chat.Usuario_id);
                intent.putExtra("usuario_empresa_id", chat.Usuario_Empresa_id);
                intent.putExtra("empresa_id", chat.Empresa_id);
                intent.putExtra("nombre_chat", chat.NombreChat);
                intent.putExtra("img_url", chat.ImagenUrl);
                context.startActivity(intent);
            }
        };
    }
}
