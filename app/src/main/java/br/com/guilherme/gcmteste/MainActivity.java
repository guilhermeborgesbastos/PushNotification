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

/*
//DEEVO
AI KEY: AIzaSyBKNVr9lOO3822Ja1fyzZDzOQpI-oaqezw
Sender ID: 489640997668

API KEY : AIzaSyD0mfKoPF_NmrabyMhI9cM7O5GgxoL1a80
Sender ID: 463797562795
*/


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
