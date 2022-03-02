package software.appshoponline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utilities extends Fragment {

    public String nombreImagen;
    public Bitmap imgFotoBitmap;
    public String imgFotoString;
    public Boolean seCambioLaImagen = false;

    private ActivityResultLauncher<Intent> mStartForResult;

    public Utilities(ImageView imageView){
        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Uri uri = result.getData().getData();
                            int ultimaBarra = uri.getPath().lastIndexOf("/");
                            nombreImagen = uri.getPath().substring(ultimaBarra);
                            try {
                                imgFotoBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                                imageView.setImageBitmap(imgFotoBitmap);
                                imgFotoString = convertirBitmapAString(imgFotoBitmap);
                                seCambioLaImagen = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cargarFotoDesdeGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mStartForResult.launch(intent);
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
