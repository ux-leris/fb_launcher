package android.usuario.meuteste;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.widget.LikeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private TextView textViewMsg, textDuvida, msgPost;
    private EditText textoPost;
    private LoginManager loginManager;
    private List<String> facebookPermitions;
    private ImageView imView;
    private AlertDialog alerta;
    Button idPostar;
    public Boolean curtirStatus;
    LikeView mLikeView;
    public Boolean mudarFoto, duvida;
    public String meuCaminho, myURL;
    public ImageButton imbtLibras;
    public View view;


    private String token;
    private String userId;
    private String message = "";
    private String title = "";
    private String imgURL = "";

    // JSON Node names
    private static final String TAG_ENTRIES = "data";
    private static final String TAG_FROM = "from";
    private static final String TAG_NAME = "name";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_DESCRIPTION = "name";
    private static final String TAG_DESCRIPTION2 = "description";


    // feeds JSONArray
    JSONArray entries = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> entriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mudarFoto = false;
        duvida = false;
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        facebookPermitions = Arrays.asList("email", "public_profile", "user_friends", "user_posts");
        imbtLibras = (ImageButton) findViewById(R.id.imbtLibras);//Botão Help
        imView = (ImageView) findViewById(R.id.imView);
        textoPost = (EditText) findViewById(R.id.textoPost);
        idPostar = (Button) findViewById(R.id.idPostar);
        textDuvida = (TextView) findViewById(R.id.textDuvida);

        //this loginManager helps you eliminate adding a LoginButton to your UI
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, facebookPermitions);

        //botão like (curtir)
        mLikeView = (LikeView) findViewById(R.id.like_view);

                loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Pega dados do usuário
                        Toast.makeText(getApplicationContext(), "SUCESSO!", Toast.LENGTH_LONG).show();
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //System.out.println(object.toString());
                                String jsonStr = object.toString();
                                Log.v("feed", jsonStr.toString());
                                if (jsonStr != null) {
                                    try {
                                        JSONObject jsonObj = object;

                                        // Getting JSON Array node
                                        JSONObject posts = jsonObj.getJSONObject("posts");
                                        entries = posts.getJSONArray(TAG_ENTRIES);
                                        Log.d("Entries: ", "> " + entries);

                                        // looping through All Feeds
                                        for (int i = 0; i < entries.length(); i++) {
                                            JSONObject c = entries.getJSONObject(i);

                                            if (c.has(TAG_MESSAGE)) {
                                                message = c.getString(TAG_MESSAGE);
                                            } // if it's a story, it has only description
                                            else if (c.has(TAG_DESCRIPTION)) {
                                                message = c.getString(TAG_DESCRIPTION);
                                            } else {
                                                message = "";
                                            }
                                            // From node is JSON Object

                                            if (c.has("picture")) {
                                                imgURL = c.getString("picture");
                                            } else {
                                                imgURL = "";
                                            }

                                            // tmp hashmap for single message feed
                                            HashMap<String, String> feed = new HashMap<String, String>();

                                            // adding each child node to HashMap key => value
                                            feed.put(TAG_NAME, "Titulo");
                                            feed.put(TAG_MESSAGE, message);
                                            feed.put("url", imgURL);


                                            // adding feed to feed list
                                            entriesList.add(feed);
                                            // clear the fields
                                            Log.v("Teste", title);
                                            Log.v("Teste", message);
                                            Log.v("Teste", imgURL);
                                            title = "";
                                            message = "";
                                            imgURL = "";

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                                }


                                try {

                                    String name = object.getString("name");
                                    textViewMsg = (TextView) findViewById(R.id.textViewMsg);
                                    textViewMsg.setText(name.toString());
                                    msgPost = (TextView) findViewById(R.id.msgPost);
                                    textViewMsg.setText(entriesList.get(0).get("message"));
                                    String myId = object.getString("id");

                                    //Tentativa para pegar comentários
                                    TextView textComments = (TextView)findViewById(R.id.textComments);
                                    textComments.setText(entriesList.get(0).get("comments{message}"));
                                    //Fim tentativa

                                    myURL = "https://www.facebook.com/profile.php?id=" + myId;
                                    if (!mudarFoto) {
                                        GetImage getImage = new GetImage();
                                        String imageURL = entriesList.get(0).get("url");
                                        //Carrega o Like com a url da imagem para poder curtir
                                        mLikeView.setObjectIdAndType(
                                                imageURL,
                                                LikeView.ObjectType.PAGE);

                                        getImage.execute(myId, imageURL);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,posts{picture,message,created_time,likes,comments,from,to}");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }


                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "CANCEL!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_LONG).show();
                    }
                });

        // Botão para Ajuda https://www.youtube.com/watch?v=mTv3JxdVb9o
        imbtLibras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duvida = !duvida;
                view = findViewById(R.id.view);
                if (duvida) {
                    view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));//muda cor de fundo
                    textDuvida.setText("Toque onde está a dúvida.");
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));//muda cor de fundo
                    textDuvida.setText("Toque no botão Libras para dúvidas");
                }
            }
        });

        imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (duvida) {
                    ajuda();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);

        if (requestCode == 2) {  //retorno FotoActivity
            Bitmap bitmap = data.getParcelableExtra("MyData");
            meuCaminho = data.getStringExtra("MyWay");
            mudarFoto = data.getBooleanExtra("MyBoolean", mudarFoto);
            if (mudarFoto)
                imView.setImageBitmap(bitmap);
        }
    }

    public class GetImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {   // insere a imagem na variável após exec
            if (bitmap != null && !mudarFoto) {
                imView.setImageBitmap(bitmap);
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {// Busca a imagem
            String userId = params[0];
            String url = params[1];
            return getFacebookPostPicture(url);
        }
    }

    public static Bitmap getFacebookProfilePicture(String userID) {
        Bitmap bitmap = null;
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap getFacebookPostPicture(String url) {
        Bitmap bitmap = null;
        try {
            URL imageURL = new URL(url);
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public void comentar (View v){  //Passa dados para a SecondActivity
        //Gera a próxima tela criando os parâmetros para serem enviados
        final Intent nextActivity = new Intent(this, postarTextoActivity.class);

        String texto = textoPost.getText().toString(); //Parâmetros a serem passados para a próxima tela
        nextActivity.putExtra("my_text", texto);
        nextActivity.putExtra("my_url", myURL);
        startActivity(nextActivity);
        textoPost.setText("");
    }

    public void postar (View v){  //Passa dados para a SecondActivity

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Confirmar");
        //define a mensagem
        builder.setMessage("Compartilhar Foto");
        //define um botão como positivo

        //Gera a próxima tela criando os parâmetros para serem enviados
        final Intent nextActivity = new Intent(this, SecondActivity.class);
        String texto = textoPost.getText().toString(); //Parâmetros a serem passados para a próxima tela
        Bitmap bitmap = ((BitmapDrawable) imView.getDrawable()).getBitmap();
        nextActivity.putExtra("my_text", texto);
        nextActivity.putExtra("my_Photo", bitmap);

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                startActivity(nextActivity);
                //Toast.makeText(MainActivity.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Toast.makeText(MainActivity.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

    public void fotografar(View v){
        final Intent nextActivity = new Intent(this, FotoActivity.class);
        startActivityForResult(nextActivity, 2);
    }

    //Solução para o problema de rotação (que recarrega a Activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putString("MyString", meuCaminho);
        savedInstanceState.putBoolean("MyBoolean", mudarFoto);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        //Guarda o caminho da Foto
        mudarFoto=savedInstanceState.getBoolean("MyBoolean");
        if (mudarFoto) {
            meuCaminho = savedInstanceState.getString("MyString");
            //reconstroi a imagem a partir do Caminho da Foto
            Bitmap bitmap = BitmapFactory.decodeFile(meuCaminho);
            imView.setImageBitmap(bitmap);
        }
    }

    public void ajuda(){
        final Intent nextActivity = new Intent(this, MenuActivity.class);
        startActivityForResult(nextActivity, 3);
        duvida=false;
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light));//muda cor de fundo
        textDuvida.setText("Toque no botão Libras para dúvidas");
    }

    public void news (View v){

        final Intent nextActivity = new Intent(this, NewsFeed.class);

        startActivity(nextActivity);
        textoPost.setText("");
    }



}