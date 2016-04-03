package android.usuario.meuteste;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private AlertDialog alerta;
    private Button btPortugues, btPortuguesSimples, btLibras, btInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle("Ajuda");

        //Ajuda em Português
        btPortugues = (Button) findViewById(R.id.btPortugues);
        btPortugues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portugues("Esta é uma tela que apresenta postagem de assuntos diversos e fotos.");
            }
        });

        //Ajuda em Português Simplificado
        btPortuguesSimples = (Button) findViewById(R.id.btPortuguesSimples);
        btPortuguesSimples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portugues("Tela Mostra Postagem, Fotos.");
            }
        });

        //Ajuda em Vídeo Libras
        btLibras = (Button) findViewById(R.id.btLibras);
        btLibras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video();
            }
        });

        //Ajuda na Internet
        btInternet = (Button) findViewById(R.id.btInternet);
        btInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busca();
            }
        });
    }

    //BOTÃO PORTUGUÊS
    //Cria o gerador do AlertDialog
    public void portugues (String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda em Português");
        builder.setMessage(mensagem)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //BOTÃO LIBRAS
    public void video(){
        final Intent nextActivity = new Intent(this, Video1Activity.class);
        startActivity(nextActivity);
    }

    //BOTÃO INTERNET
    public void busca(){
        String endereco = "https://www.google.com.br/#q=" + "post postagem";
        Uri uri = Uri.parse(endereco);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // BOTÃO VOLTAR
    public void voltar (View v){
        finish();
    }
}
