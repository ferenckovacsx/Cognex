package ferenckovacsx.cognex.api;

/**
 * Created by ferenckovacsx on 2018-03-01.
 */

import java.util.ArrayList;

import ferenckovacsx.cognex.models.Device;
import ferenckovacsx.cognex.models.Firmware;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;


public interface APIInterface {

    @GET("index.json")
    Call<ArrayList<Device>> list_devices();

    @GET("{deviceID}/index.json")
    Call<ArrayList<Firmware>> list_firmware(@Path("deviceID") String deviceID);

    @Streaming
    @GET("{filename}")
    Call<ResponseBody> downloadFileWithFixedUrl(@Path("filename") String filename);

}
