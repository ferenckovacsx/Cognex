package ferenckovacsx.cognex;

/**
 * Created by ferenckovacsx on 2018-03-01.
 */

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface APIInterface {

    @GET("index.json")
    Call<ArrayList<Device>> list_devices();

    @GET("{deviceID}/index.json")
    Call<ArrayList<Firmware>> list_firmware(@Path("deviceID") String deviceID);

}
