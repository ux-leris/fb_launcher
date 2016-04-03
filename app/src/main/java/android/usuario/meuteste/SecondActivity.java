package android.usuario.meuteste;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import java.util.Arrays;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private FacebookCallback callbackM;
    TextView textViewNome;
    ImageView imageView;
    private CallbackManager callbackManager;
    private LoginManager manager;
    Bitmap bitmap;
    Button voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        FacebookSdk.sdkInitialize(getApplicationContext());

        textViewNome = (TextView) findViewById(R.id.textViewNome);
        imageView = (ImageView) findViewById(R.id.imageView);

        callbackManager = CallbackManager.Factory.create();

        List<String> permissionNeeds = Arrays.asList("publish_actions");

        //Pega dados da MainActivity
        Intent intent = getIntent();
        String texto = intent.getStringExtra("my_text");
        bitmap = intent.getParcelableExtra("my_Photo");
        textViewNome.setText(texto);
        imageView.setImageBitmap(bitmap);
        voltar = (Button) findViewById(R.id.idVoltar);

        //Loga o facebook com requisição para publicar e publica
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);

        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sharePhotoToFacebook();
                Toast.makeText(getApplicationContext(), "Foto Compatilhada!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }


    //Compartilha automatico
    private void sharePhotoToFacebook() {

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(textViewNome.getText().toString())
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, callbackM);

    }

    public void voltarTela(View v) {
            finish();
    }
}