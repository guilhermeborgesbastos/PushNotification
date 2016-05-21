![alt text](https://www.linkedin.com/mpr/mpr/AAEAAQAAAAAAAAiiAAAAJGFiOTNjYzBjLWE1NmMtNDNkNS04YzEyLTg1ODdkNTE2OTkzMA.jpg "Header")


# PushNotification Android
Artigo explicando passo-a-passo como implementar as PushNotification Android, utilizando código PHP para enviar a mensagem e um APP exemplo para receber essas notificações no celular. Ambos, API ( PHP ) e Aplicativo disponíveis para download.

| Resultado final | Baixe o Código APP Android |
| --- | --- |
| ![TinderSwipeBastos_animated](https://meucomercioeletronico.com/tutorial/push_notfication_animeted.gif)  | [![VIDEO](https://meucomercioeletronico.com/tutorial/pushNotification017.jpg)](https://github.com/guilhermeborgesbastos/PushNotification.API.PHP) |


## MainActivity
Esta classe possui os métodos de manipulação do banco de dados e do Google Cloud Messaging

```
package br.com.guilherme.gcmteste;

import java.io.IOException;

import br.com.guilherme.gcmteste.util.AndroidSystemUtil;
import br.com.guilherme.gcmteste.util.HttpConnectionUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String TAG = "Script";

	//altere aqui para o ID DO SEU PROJETO NO Console API Google
	private String SENDER_ID = "758594887344";
	private String regId;
	private GoogleCloudMessaging gcm;
	private TextView tvRegistrationId;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvRegistrationId = (TextView) findViewById(R.id.tvRegistrationId);

        // id_do usuário a qual sera atualizado o seu registration_id
        // isso deve depois ser vinculado automaticamente no Login
        // ser repassado automaticamente
		String id_usuario = String.valueOf(1);

		if(checkPlayServices()){
			gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
			regId = AndroidSystemUtil.getRegistrationId(MainActivity.this);
			
			//if(regId.trim().length() == 0){
				registerIdInBackground(id_usuario);
			//}
		}
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		checkPlayServices();
	}
	
	
	
	// UTIL
	public boolean checkPlayServices(){
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);

		if(resultCode != ConnectionResult.SUCCESS){
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
				GooglePlayServicesUtil.getErrorDialog(resultCode, MainActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST);
			}
			else{
				Toast.makeText(MainActivity.this, "PlayServices sem suporte", Toast.LENGTH_SHORT).show();
				finish();
			}
			return(false);
		}
		return(true);
	}


	public void registerIdInBackground(final String id_usuario){
		new AsyncTask(){
			@Override
			protected Object doInBackground(Object... params) {
				String msg = "";

				try{
					if(gcm == null){
						gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
					}

					regId = gcm.register(SENDER_ID);

					msg = regId;
					Log.i("Register Id:", msg);


					String feedback = HttpConnectionUtil.sendRegistrationIdToBackend(regId, id_usuario, MainActivity.this);
					Log.i(TAG, feedback);

					AndroidSystemUtil.storeRegistrationId(MainActivity.this, regId);
				}
				catch(IOException e){
					Log.i(TAG, e.getMessage());
				}

				return msg;
			}

			@Override
			public void onPostExecute(Object msg){
				tvRegistrationId.setText((String)msg);
			}

		}.execute(null, null, null);
	}
}

```

## GcmBroadcastReceiver
Essa classe extende a WakefulBroadcastReceiver, isso faz com que ela fique sempre alerta mesmo com o celular em standby. Ela é responsável por receber a intent com a mensagem ( Push notification ).

```
package br.com.guilherme.gcmteste;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
		
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

}
```

## GcmIntentService
É quem recebe a intent preciamente tratada pela GcmBroadcastReceiver, expecificamente neste arquivos fazemos ajustes, removemos e adicionamos novos parametros a serem recebidos pelo GCM.

```
package br.com.guilherme.gcmteste;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import br.com.guilherme.gcmteste.util.NotificationCustomUtil;


public class GcmIntentService extends IntentService {
	public static final String TAG = "Script";
	
	public GcmIntentService(){
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GcmIntentService.this);
		String title, author, message, image, messageType = gcm.getMessageType(intent);
		
		
		if(extras != null){
			if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
				Log.i(TAG, "Error: "+extras.toString());
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
				Log.i(TAG, "Deleted: "+extras.toString());
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
				title = extras.getString("title");
				author = extras.getString("author");
				image = extras.getString("image");
				message = extras.getString("message");

				NotificationCustomUtil.sendNotification(GcmIntentService.this, title, image, author, message);
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
}
```


## API
É quem trabalha com o Retrofit, ele é quem comunica com a nossa api ( Backend ).
Na linha 18 temos o código:
>
public static final String API_URL = "http://endereco_de_sua_api/";
<

Deve-se mudar a url para a qual instalou o seu backend.


```
package br.com.guilherme.gcmteste;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import br.com.guilherme.gcmteste.util.NotificationCustomUtil;


public class GcmIntentService extends IntentService {
	public static final String TAG = "Script";
	
	public GcmIntentService(){
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GcmIntentService.this);
		String title, author, message, image, messageType = gcm.getMessageType(intent);
		
		
		if(extras != null){
			if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
				Log.i(TAG, "Error: "+extras.toString());
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
				Log.i(TAG, "Deleted: "+extras.toString());
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
				title = extras.getString("title");
				author = extras.getString("author");
				image = extras.getString("image");
				message = extras.getString("message");

				NotificationCustomUtil.sendNotification(GcmIntentService.this, title, image, author, message);
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
}
```


## HttpConnectionUtil
É basicamente tem faz a comunicaçao HTTP com a API, enviando o id_usuário e seu registration _id fornecido pelo proprio código JAVA. Assim mantemos o usuário com o registration_id do device atualizado com nossa base de dados.

```
package br.com.guilherme.gcmteste.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import br.com.guilherme.gcmteste.API;
import br.com.guilherme.gcmteste.MainActivity;
import br.com.guilherme.gcmteste.pojo.WebserviceResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HttpConnectionUtil {


	public static String sendRegistrationIdToBackend(final String regId, final String id_usuario, final MainActivity activity){

		final String[] result = {null};

        // id_do usuário a qual sera atualizado o seu registration_id
        // isso deve depois ser vinculado automaticamente no Login
        // ser repassado automaticamente

		API.usuarios().updateRegistrationId("updateRegistrationId", id_usuario, regId, new Callback<WebserviceResponse>() {

			@Override
			public void success(WebserviceResponse resultado, Response response) {

				try {

					if (resultado == null) {
						return;
					}

					String mensagem = resultado.getMessage();
					Boolean sucesso = resultado.getSuccess();

					if (!sucesso) {

						Context context = activity;
						CharSequence text = "Erro ao cadastrar o registration_id!";
						int duration = Toast.LENGTH_SHORT;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();

					} else {

                        //faça algo com o sucesso ao salvar o reg_id na API
					}

				} catch (RetrofitError e) {
					Log.i("Error: ", e.toString());
				}


			}

			@Override
			public void failure(RetrofitError error) {
				if (error.getMessage() != null) Log.e("FindMe", error.getMessage());
			}
		});
        return regId;
	}

}
```



## NotificationCustomUtil
Este aqui é quem monta o Layout da notificação que aparece para o usuário no Android, é nele que deve-se trabalha para fazer alteraçoes de lauout nas Notifications.
```
package br.com.guilherme.gcmteste.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import br.com.guilherme.gcmteste.MainActivity;
import br.com.guilherme.gcmteste.R;

public class NotificationCustomUtil {
	private static NotificationManager mNotificationManager;

	private static Bitmap getImageBitmap(String url) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {
			Log.e("NotificationCustomUtil", "Error getting bitmap", e);
		}
		return bm;
	}


	public static void sendNotification(Context context, String title, String imagem, String author, String message) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setLargeIcon(getImageBitmap(imagem))
				.setContentTitle(message)
				.setContentText(author);

        mBuilder.setContentIntent(contentIntent);
        
        Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(AndroidSystemUtil.randInt(), notification);
    }
}

```



## WebserviceResponse
É um callBack do nosso Retrofit, ele apenas separa as informaçoes vindas da API, para que tenhamos como manipulá-las se necessário.
```
package br.com.guilherme.gcmteste.pojo;

/**
 * Created by Guilherme Borges Bastos on 12/23/2015.
 */


public class WebserviceResponse {
    private boolean success;
    private String message;


    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
```

Envie agora uma notification no formulário que deixei ONLINE, terei prazer em recebe-las em meu celular:

[![VIDEO](https://meucomercioeletronico.com/tutorial/pushNotification019.jpg)](https://meucomercioeletronico.com/tutorial/push_notification)


Contato:

[![VIDEO](https://meucomercioeletronico.com/tutorial/facebook.jpg)](https://www.facebook.com/guilherme.borgesbastos)
