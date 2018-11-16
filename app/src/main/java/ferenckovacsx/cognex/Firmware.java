package ferenckovacsx.cognex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Firmware {

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("filetype")
    @Expose
    private String filetype;

    @SerializedName("file")
    @Expose
    private String file;

    @SerializedName("channel")
    @Expose
    private String channel;

    @SerializedName("id")
    @Expose
    private int id;


}
