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

    public Firmware(String description, String title, String created, String version, String filetype, String file, String channel, int id) {
        this.description = description;
        this.title = title;
        this.created = created;
        this.version = version;
        this.filetype = filetype;
        this.file = file;
        this.channel = channel;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
