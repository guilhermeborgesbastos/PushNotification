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

	/*public static String sendRegistrationIdToBackend(String regId, String token){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://findme.meucomercioeletronico.com/gcm/ctrl/CtrlGcm.php");
		String answer = "";

		try{
			ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
			valores.add(new BasicNameValuePair("method", "save-gcm-registration-id"));
			valores.add(new BasicNameValuePair("reg-id", regId));
			valores.add(new BasicNameValuePair("id_usuario", token));

			httpPost.setEntity(new UrlEncodedFormEntity(valores));
			HttpResponse resposta = httpClient.execute(httpPost);
			answer = EntityUtils.toString(resposta.getEntity());
		}
		catch(NumberFormatException e){ e.printStackTrace(); }
		catch(NullPointerException e){ e.printStackTrace(); }
		catch(ClientProtocolException e){ e.printStackTrace(); }
		catch(IOException e){ e.printStackTrace(); }
		return(answer);
	}*/

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
