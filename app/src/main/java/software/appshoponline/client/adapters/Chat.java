package software.appshoponline.client.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Chat {
    public String ImagenUrl;
    public String NombreChat;
    public String MensajeCorto;
    public String HoraUltimoMensaje;

    public Chat(String imgUrl, String nombreChat, String mensajeCorto, String horaUltimoChat){
        this.ImagenUrl = imgUrl;
        this.NombreChat = nombreChat;
        this.MensajeCorto = mensajeCorto;
        this.HoraUltimoMensaje = horaUltimoChat;
    }
}
