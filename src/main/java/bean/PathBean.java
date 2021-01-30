package bean;

public class PathBean {
    private String screenShotDir;
    private String desire;
    private String img;
    private String device;

    public String getScreenShotDir() {
        return screenShotDir;
    }

    public void setScreenShotDir(String screenShotDir) {
        this.screenShotDir = screenShotDir;
    }

    public String getDevice() {
        System.out.println("device : " + device);
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDesire() {
        System.out.println("desire : " + desire);
        return desire;
    }

    public void setDesire(String desire) {
        this.desire = desire;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "PathBean{" +
                "screenShotDir='" + screenShotDir + '\'' +
                ", desire='" + desire + '\'' +
                ", img='" + img + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
