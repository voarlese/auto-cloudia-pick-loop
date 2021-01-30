package bean;

import com.google.gson.annotations.SerializedName;
import org.opencv.core.Point;

public class CardBean {
    @SerializedName("card_no")
    String cardNo;
    @SerializedName("level_name")
    String level;
    @SerializedName("name")
    String name;
    @SerializedName("type_no")
    String type;
    @SerializedName("type_name")
    String typeName;
    Point centerPoint;
    @SerializedName("img_name")
    String imageName;

    @Override
    public String toString() {
        return "CardBean{" +
                "cardNo='" + cardNo + '\'' +
                ", level='" + level + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", typeName='" + typeName + '\'' +
                ", centerPoint=" + centerPoint +
                ", imageName='" + imageName + '\'' +
                '}';
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
