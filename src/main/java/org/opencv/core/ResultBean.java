package org.opencv.core;

import bean.CardBean;

public class ResultBean {
    double maxVal;
    CardBean card;
    double distance;
    int srcIndex;
    public ResultBean(double maxVal, CardBean card) {
        this.maxVal = maxVal;
        this.card = card;
    }

    public ResultBean(double maxVal, CardBean card, int srcIndex) {
        this.maxVal = maxVal;
        this.card = card;
        this.srcIndex = srcIndex;
    }

    public ResultBean(double maxVal) {
        this.maxVal = maxVal;
    }

    public double getDistance() {
        return distance;
    }

    public ResultBean(double maxVal, CardBean card, double distance) {
        this.maxVal = maxVal;
        this.card = card;
        this.distance = distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public CardBean getCard() {
        return card;
    }

    public int getSrcIndex() {
        return srcIndex;
    }

    public void setSrcIndex(int srcIndex) {
        this.srcIndex = srcIndex;
    }

    public void setCard(CardBean card) {
        this.card = card;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "maxVal=" + maxVal +
                ", card=" + card +
                ", distance=" + distance +
                '}';
    }
}
