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

public class Utilities extends AppCompatActivity {

    public String nombreImagen;
    public Bitmap imgFotoBitmap;
    public String imgFotoString;

    public void cargarFotoDesdeGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //startActivityForResult(intent, 1);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            try {
                Uri uri = data.getData();
                int ultimaBarra = uri.getPath().lastIndexOf("/");
                this.nombreImagen = uri.getPath().substring(ultimaBarra);
                this.imgFotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                this.imgFotoString = convertirBitmapAString(this.imgFotoBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertirBitmapAString(Bitmap bitmap){
        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayStream);
        byte[] imagenByte = arrayStream.toByteArray();
        String imageString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        return imageString;
    }
}
