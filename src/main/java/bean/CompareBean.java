package bean;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class CompareBean {
    private Mat screenShot;
    private Mat template;
    private ArrayList<Mat> templates;
    private String fileName;
    private String filePath;
    private int compareIndex;

    @Override
    public String toString() {
        return "CompareBean{" +
                "screenShot=" + screenShot +
                ", template=" + template +
                ", templates=" + templates +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", compareIndex=" + compareIndex +
                '}';
    }

    public int getCompareIndex() {
        return compareIndex;
    }

    public void setCompareIndex(int compareIndex) {
        this.compareIndex = compareIndex;
    }

    public Mat getScreenShot() {
        return screenShot;
    }

    public void setScreenShot(Mat screenShot) {
        this.screenShot = screenShot;
    }

    public ArrayList<Mat> getTemplates() {
        return templates;
    }

    public void setTemplates(ArrayList<Mat> templates) {
        this.templates = templates;
    }

    public Mat getTemplate() {
        return template;
    }

    public void setTemplate(Mat template) {
        this.template = template;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
