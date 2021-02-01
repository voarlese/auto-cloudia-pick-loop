package bean;

import com.google.gson.annotations.SerializedName;
import org.opencv.core.ResultBean;

import java.util.ArrayList;

public class DesireBean {
    @SerializedName("desire_card")
    private ArrayList<Item> desireCard;
    @SerializedName("desire_role")
    private ArrayList<Item> desireRole;
    @SerializedName("limit_card")
    private int limitCard;
    @SerializedName("limit_role")
    private int limitRole;

    public void check(ResultBean resultBean) {
        CardBean card = resultBean.getCard();
        if (card.getType().equals("1")) { // 聖物
            setPick(card, desireCard);
        } else { // 角色
            setPick(card, desireRole);
        }
    }

    public boolean isComplete() {
        int cardPickCount = 0;
        for (Item item : desireCard) {
            if (item.getType() == 1) { // 一定要抽到
                if (!item.isPick()) {
                    return false;
                }
                limitCard--;
            } else { // type == 0
                if (item.isPick()) {
                    cardPickCount++;
                }
            }
        }
        if (limitCard > 0) {
            if (desireCard.size() == 0) {
                // 沒放卡 繼續
            }
            if (cardPickCount < limitCard) {
                return false;
            }
        }
        int rolePickCount = 0;
        for (Item item : desireRole) {
            if (item.getType() == 1) { // 一定要抽到
                if (!item.isPick()) {
                    return false;
                }
                limitRole--;
            } else {
                if (item.isPick()) {
                    rolePickCount++;
                }
            }
        }
        if (limitRole > 0) {
            if (desireRole.size() == 0) {
                return true;
            }
            return rolePickCount >= limitRole;
        }
        return true;
    }

    private void setPick(CardBean card, ArrayList<Item> list) {
        for (Item item : list) {
            if (item.getName().equals(card.getName())) {
                item.setPick(true);
                return;
            }
        }
    }

    public static class Item {
        String name;
        /**
         * 1 : 一定要抽到
         * 0 : 選擇性, 要搭配limit
         * <p>
         * limit - (type = 1) 的數量後, 為 type = 0 必須要抽到的數量
         */
        int type;

        boolean pick;

        public boolean isPick() {
            return pick;
        }

        public void setPick(boolean pick) {
            this.pick = pick;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    ", pick=" + pick +
                    '}';
        }
    }

    public ArrayList<Item> getDesireCard() {
        return desireCard;
    }

    public void setDesireCard(ArrayList<Item> desireCard) {
        this.desireCard = desireCard;
    }

    public ArrayList<Item> getDesireRole() {
        return desireRole;
    }

    public void setDesireRole(ArrayList<Item> desireRole) {
        this.desireRole = desireRole;
    }

    public int getLimitCard() {
        return limitCard;
    }

    public void setLimitCard(int limitCard) {
        this.limitCard = limitCard;
    }

    public int getLimitRole() {
        return limitRole;
    }

    public void setLimitRole(int limitRole) {
        this.limitRole = limitRole;
    }

    @Override
    public String toString() {
        return "DesireBean{" +
                "desireCard=" + desireCard +
                ", desireRole=" + desireRole +
                ", limitCard=" + limitCard +
                ", limitRole=" + limitRole +
                '}';
    }
}
