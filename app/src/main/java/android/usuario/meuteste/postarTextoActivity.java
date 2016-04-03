package android.usuario.meuteste;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.List;

public class postarTextoActivity extends AppCompatActivity {

    EditText editText;
    private CallbackManager callbackManager;
    public ShareDialog shareDialog;
    private FacebookCallback callbackM;
    public String texto, myURL;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postar_texto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setTitle("Postar Texto");

        editText = (EditText) findViewById(R.id.editText);

        //Pega dados da tela anterior
        Intent intent = getIntent();
        texto = intent.getStringExtra("my_text");
        myURL = intent.getStringExtra("my_url");
        editText.setText(texto);

        shareDialog = new ShareDialog(this); //Confirmação
    }

    public void postar (View v){  //Passa dados para a SecondActivity
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Confirmar");
        //define a mensagem
        builder.setMessage("Compartilhar Texto");
        //define um botão como positivo

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(myURL))
                        .build();

                ShareApi shareApi = new ShareApi(content);
                shareApi.setMessage(texto);

                shareApi.share(callbackM);
                Toast.makeText(getApplicationContext(), "Texto Compartilhado!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }



    // BOTÃO VOLTAR
    public void voltar (View v){
        finish();
    }
}
