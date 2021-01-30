package bean;

import org.opencv.core.Point;

public class CardBean {
    String cardNo;
    String level;
    String name;
    String type;
    String typeName;
    Point centerPoint;
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
