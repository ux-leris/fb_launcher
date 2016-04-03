package android.usuario.meuteste;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FotoActivity extends AppCompatActivity {
    ImageView img;
    private Bitmap bitmap;
    public String meuCaminho; //usado para armazenar o local onde se encontra a FOTO
    public Boolean mudarFoto; //Teste para chavear imagem antiga e foto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        img = (ImageView) findViewById(R.id.imageView);

        mudarFoto = false; //Teste para controle de imagens

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamera();
            }
        });
    }

    public void abrirCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setImageBitmap(photo);

            // Chame este método pra obter a URI da imagem
            Uri uri = getImageUri(getApplicationContext(), photo);

            // Em seguida chame este método para obter o caminho do arquivo
            File file = new File(getRealPathFromURI(uri));

            //System.out.println(file.getPath());

            mudarFoto = true; //Teste para controle de imagens
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        meuCaminho = cursor.getString(idx);  //Armazena o local da imagem
        return cursor.getString(idx);
    }

    @Override
    public void onBackPressed() {

        Bitmap data = ((BitmapDrawable) img.getDrawable()).getBitmap();
        Intent intent = new Intent();
        intent.putExtra("MyData", data);
        intent.putExtra("MyWay", meuCaminho);
        intent.putExtra("MyBoolean", mudarFoto);
        //intent.putExtra("MyData", bytes); //enviando comprimido
        setResult(2, intent);
        super.onBackPressed();
    }

    //Solução para o problema de rotação (que recarrega a Activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
            savedInstanceState.putBoolean("MyBoolean", mudarFoto); //Teste para controle de imagens
            savedInstanceState.putString("MyString", meuCaminho);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        //Guarda o caminho da Foto

            mudarFoto = savedInstanceState.getBoolean("MyBoolean");
        if(mudarFoto) {
            meuCaminho = savedInstanceState.getString("MyString");
            //reconstroi a imagem a partir do Caminho da Foto
            Bitmap bitmap = BitmapFactory.decodeFile(meuCaminho);
            img.setImageBitmap(bitmap);
        }
    }

}
