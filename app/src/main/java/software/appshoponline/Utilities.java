package software.appshoponline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utilities {

    public String nombreImagen;
    public Bitmap imgFotoBitmap;
    public String imgFotoString;

    public void cargarFotoDesdeGaleria(){

    }

    public static String convertirBitmapAString(Bitmap bitmap){
        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayStream);
        byte[] imagenByte = arrayStream.toByteArray();
        String imageString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        return imageString;
    }

    public static int indexUnidadMedida(String unidadMedida){
        for (int i=0; i < Constantes.UNIDADES_DE_MDIDA.length; i++){
            if (Constantes.UNIDADES_DE_MDIDA[i].equals(unidadMedida))
                return i;
        }
        return 0;
    }
}
