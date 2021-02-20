package bean;

public class PathBean {
    private String screenShotDir;
    private String desire;
    private String img;
    private String device;
    private String cards;
    private String sql;

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
        return desire;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
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

    public String getCard() {
        return cards;
    }

    public void setCard(String cards) {
        this.cards = cards;
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
