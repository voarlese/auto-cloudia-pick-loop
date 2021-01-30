package bean;

import org.opencv.core.Mat;

import java.io.File;

public class SourceBean {
    File file;
    Mat source;

    public SourceBean(File file, Mat source) {
        this.file = file;
        this.source = source;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Mat getSource() {
        return source;
    }

    public void setSource(Mat source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "SourceBean{" +
                "file=" + file +
                ", source=" + source +
                '}';
    }
}
