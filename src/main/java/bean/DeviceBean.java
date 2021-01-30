package bean;

import com.google.gson.annotations.SerializedName;

public class DeviceBean {
    @SerializedName("device")
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
