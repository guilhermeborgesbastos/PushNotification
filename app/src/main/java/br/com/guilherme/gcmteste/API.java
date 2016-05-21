package br.com.guilherme.gcmteste;


import br.com.guilherme.gcmteste.pojo.WebserviceResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by "Guilherme Borges Bastos on 05/21/2016.
 */

public class API {

    /* Rest Adapter Creation */
    public static final String API_URL = "http://endereco_de_sua_api/";
    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(API_URL)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build();




    /*=============================================================================================
    Usuarios
    ==============================================================================================*/

    public static Usuarios usuarios() {
        return REST_ADAPTER.create(Usuarios.class);
    }

    public interface Usuarios {

        @FormUrlEncoded
        @POST("/server.php")
        void updateRegistrationId(
                @Field("action") String action,
                @Field("id_usuario") String id_usuario,
                @Field("registration_id") String registration_id,
                Callback<WebserviceResponse> response);

        @FormUrlEncoded
        @POST("/server.php")
        void sendGcmMessage(
                @Field("action") String action,
                @Field("id_usuario") String id_usuario,
                @Field("author") String author,
                @Field("imagem") String imagem,
                @Field("mensagem") String mensagem,
                Callback<WebserviceResponse> response);

    }


}
