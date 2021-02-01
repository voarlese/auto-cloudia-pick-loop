package core;

import bean.CardBean;
import bean.CompareBean;
import bean.DesireBean;
import bean.SourceBean;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static core.CompareConfig.*;

// 截圖300 290
public class Compare {
    private long startTime;
    private Mat screenShot;
    private Mat template; // 目前用不到 比大圖用的
    public Observable<HashMap<Integer, ResultBean>> getCompareResult(String sourceName) {
        screenShot = Imgcodecs.imread(getScreenShotDir() + sourceName);
//        template = Imgcodecs.imread(templatePath);
        ArrayList<Observable<ResultBean>> obList = getObservableListEach();
        return Observable.zip(obList, new Function<Object[], HashMap<Integer, ResultBean>>() {
            @Override
            public HashMap<Integer, ResultBean> apply(Object[] objects) throws Throwable {
                HashMap<Integer, ResultBean> resultBeanList = new HashMap<>();
                for (Object object : objects) {
                    ResultBean resultBean = (ResultBean) object;
                    if (resultBean.getMaxVal() > .73) {
                        System.out.println("新增 : " + resultBean.getSrcIndex() + " " + resultBean.toString());
                        resultBeanList.put(resultBean.getSrcIndex(), resultBean);
                    }
                }
                return resultBeanList;
            }
        });
    }
    /**
     * 輸入截圖名稱
     *
     * @param sourceName
     */
    public void compareOnce(String sourceName) {
        startTime = System.currentTimeMillis();
        System.out.println("比對就緒 : " + startTime);
        getCompareResult(sourceName).subscribe(new Observer<HashMap<Integer, ResultBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull HashMap<Integer, ResultBean> map) {
                System.out.println("抽到的卡 ：");
                DesireBean desireBean = getDesireBean();
                for (Map.Entry<Integer, ResultBean> entry : map.entrySet()) {
                    desireBean.check(entry.getValue());
                    System.out.println("-> " + entry.getValue().toString());
                }
                if (desireBean.isComplete()) {
                    System.out.println("全部抽到拉～");
                } else {
                    System.out.println("沒抽到想要的ＱＱ～繼續");
                }
                System.out.println("----");
                System.out.println("耗時 : " + ((double) (System.currentTimeMillis() - startTime) / 1000) + " 秒");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onComplete() {
                template = null;
                screenShot = null;
                System.out.println("Complated");
            }
        });
    }

    /**
     * 比對中心點(大圖用)
     * @param compareBean
     * @return
     */
    public Observable<ResultBean> compareCenterPoint(CompareBean compareBean) {
        return Observable.create(new ObservableOnSubscribe<ResultBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ResultBean> emitter) throws Throwable {
                Core.MinMaxLocResult mmr = matchTemplateMaxLoc(compareBean.getScreenShot(), compareBean.getTemplate());
                // 輸出比對圖
//                outputResultImg(compareBean.getScreenShot(), compareBean.getTemplate(), mmr.maxLoc, "/Users/chiaowenke/IdeaProjects/autotest/compare/completed/" + compareBean.getFileName());
                // 計算比對圖的中心點
                Point matchPoint = new Point(mmr.maxLoc.x + compareBean.getTemplate().cols() / 2f,
                        mmr.maxLoc.y + compareBean.getTemplate().rows() / 2f);
                // 用中心點判斷是哪一張卡片 並取得
                System.out.println("compare -> " + "比對出來位置中心點 ：" + matchPoint);
                System.out.println("compare -> 用中心點判斷位置");
                CardBean card = CompareConfig.getCard(matchPoint);
                System.out.println("compare -> 比對成功 : " + card.toString());
                ResultBean resultBean = new ResultBean(mmr.maxVal, card);
                emitter.onNext(resultBean);
            }
        });
    }

    /**
     * 比對並輸出結果
     * @param compareBean
     * @return
     */
    public Observable<ResultBean> compareEach(CompareBean compareBean) {
        return Observable.create(new ObservableOnSubscribe<ResultBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ResultBean> emitter) throws Throwable {
                Core.MinMaxLocResult mmr = matchTemplateMaxLoc(compareBean.getScreenShot(), compareBean.getTemplate());
                // 輸出比對圖
                // outputResultImg(compareBean.getScreenShot(), compareBean.getTemplate(), mmr.maxLoc, "/Users/chiaowenke/IdeaProjects/autotest/compare/completed/" + compareBean.getFileName());
                CardBean card = CompareConfig.getCardBean(compareBean.getFileName());
                ResultBean resultBean = new ResultBean(mmr.maxVal, card, compareBean.getCompareIndex());
                System.out.println("compareIndex : " + compareBean.getCompareIndex() + " ratio : " + resultBean.getMaxVal() + " " + resultBean.getCard().getImageName());
                emitter.onNext(resultBean);
            }
        });
    }

    private Core.MinMaxLocResult matchTemplateMaxLoc(Mat screenShot, Mat template) {
        Mat outputImage = new Mat();
        int machMethod = Imgproc.TM_CCOEFF_NORMED;
        //Template matching method
        Imgproc.matchTemplate(screenShot, template, outputImage, machMethod);
        return Core.minMaxLoc(outputImage);
    }

    private void outputResultImg(Mat screenShot, Mat template, Point matchLoc, String target) {
        //Draw rectangle on result image
        Imgproc.rectangle(screenShot, matchLoc, new Point(matchLoc.x + template.cols(),
                matchLoc.y + template.rows()), new Scalar(0, 0, 255));
        // 輸出配對圖
        Imgcodecs.imwrite(target, screenShot);
    }

    /**
     * 一一比對單圖
     * @return
     */
    public ArrayList<Observable<ResultBean>> getObservableListEach() {
        ArrayList<Observable<ResultBean>> obList = new ArrayList<>();
        double aspectRatioW = screenShot.width() / 1080d;
        double aspectRatioH = screenShot.height() / 1920d;
        // 拆出十張圖
        ArrayList<Mat> mats = getPicFromScreenShot295();
        for (int i = 0; i < mats.size(); i++) {
            for (int i1 = 0; i1 < getSourceList().size(); i1++) {
                SourceBean sourceBean = getSourceList().get(i1);
                Mat src = new Mat(mats.get(i), new Rect((int) (90 * aspectRatioW), (int) (80 * aspectRatioH), (int) (135 * aspectRatioW), (int) (145 * aspectRatioH))); // 縮小比對範圍
                CompareBean compareBean = new CompareBean();
                compareBean.setTemplate(src); // 小圖
                compareBean.setScreenShot(sourceBean.getSource()); // 大圖
                compareBean.setFileName(sourceBean.getFile().getName());
                compareBean.setCompareIndex(i);
                if ((i1 + 1) % 5 == 0) {
                    obList.add(compareEach(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch1())));
                } else if ((i1 + 1) % 5 == 1) {
                    obList.add(compareEach(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch2())));
                } else if ((i1 + 1) % 5 == 2) {
                    obList.add(compareEach(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch3())));
                } else if ((i1 + 1) % 5 == 3) {
                    obList.add(compareEach(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch4())));
                } else if ((i1 + 1) % 5 == 4) {
                    obList.add(compareEach(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch5())));
                }
            }
        }
        return obList;
    }

    public ArrayList<Observable<ResultBean>> getObservableListCenterPoint() {
        ArrayList<Observable<ResultBean>> obList = new ArrayList<>();
        ArrayList<Mat> mats = getPicFromScreenShot295();
        // 抓十張圖根總表比對 共十次
        for (int i = 0; i < mats.size(); i++) {
            CompareBean compareBean = new CompareBean();
            compareBean.setTemplate(mats.get(i));
            compareBean.setScreenShot(template);
            compareBean.setFileName(i + ".jpg");
            if ((i + 1) % 5 == 0) {
                obList.add(compareCenterPoint(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch1())));
            } else if ((i + 1) % 5 == 1) {
                obList.add(compareCenterPoint(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch2())));
            } else if ((i + 1) % 5 == 2) {
                obList.add(compareCenterPoint(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch3())));
            } else if ((i + 1) % 5 == 3) {
                obList.add(compareCenterPoint(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch4())));
            } else if ((i + 1) % 5 == 4) {
                obList.add(compareCenterPoint(compareBean).subscribeOn(Schedulers.from(CompareConfig.getSch5())));
            }
        }
        return obList;
    }

    /**
     * 拿已經拍好的3040張截圖 取出其中不一樣的圖片
     */
    public void screenShotFuck() {
        // 第一張圖
        for (int i = 0; i < 3040; i++) {
            // 拆出十張圖
            ArrayList<Mat> mats = getPicFromScreenShot295(i + ".png");
            for (Mat mat : mats) {
                // 目前已經截圖的圖  找到一樣的  就不儲存 mat
                boolean match = false;
                File dir = new File(templatePath);
                File[] files = dir.listFiles(CompareConfig.IMAGE_FILTER);
                String fileName = System.currentTimeMillis() + "-" + (files.length + 1) + "-" + i + ".jpg" ;
                for (int i1 = 0; i1 < files.length; i1++) {
                    Mat src = new Mat(mat, new Rect(90, 80, 135,145)); // 縮小比對範圍
                    Core.MinMaxLocResult mmr = matchTemplateMaxLoc(src, Imgcodecs.imread(files[i1].getAbsolutePath()));
                    // 輸出比對圖
                    System.out.println( fileName + " " + "與 " + files[i1].getName() + "相似度 : " + mmr.maxVal  + " Complated ." + i);
                    if (mmr.maxVal > .75) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    Imgcodecs.imwrite(templatePath + fileName, mat);
                }
            }
        }
    }

    private ArrayList<Mat> getMats(Mat screenShot) {
        ArrayList<Mat> mats = new ArrayList<>();
//            col 2
        double aspectRatioW = screenShot.width() / 1080d;
        double aspectRatioH = screenShot.height() / 1920d;
        int offsetH = (int) (130d * aspectRatioH);
        int w = (int) (190d * aspectRatioW);
        int h = (int) (165d * aspectRatioH);

//            col 2
        int x = (int) (450 * aspectRatioW);
        int y = (int) (270 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + offsetH + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h + offsetH) * 2, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h + offsetH) * 3, w, h)));
//            col 1
        x = (int) (150 * aspectRatioW);
        y = (int) (407 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + offsetH + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h + offsetH) * 2, w, h)));

//col 3
        x = (int) (745 * aspectRatioW);
        y = (int) (407 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + offsetH + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h + offsetH) * 2, w, h)));

        return mats;
    }

    /**
     * 多圖比對
     *
     * @param fileName
     */
    public void compareMultiple(String fileName) {
        Mat src = Imgcodecs.imread(getScreenShotDir() + fileName);
        Mat tem = Imgcodecs.imread("/Users/chiaowenke/IdeaProjects/autotest/img/new.png", 0);
        Mat gray = Mat.zeros(src.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        Mat resultImage = null;

        int resultImage_cols = gray.cols() - tem.cols() + 1;
        int resultImage_rows = gray.rows() - tem.rows() + 1;
//	int resultImage_cols = gray.cols;
//	int resultImage_rows = gray.rows;
        resultImage = new Mat(resultImage_cols, resultImage_rows, CvType.CV_32FC1);

        //进行匹配和标准化
	/*
	参数四：SQDIFF和SQDIFF_NORMED越小数值匹配效果更好，其他方法则反之。
	TM_SQDIFF	TM_SQDIFF_NORMED	TM_CCORR
	TM_CCORR_NORMED	TM_CCOEFF	TM_CCOEFF_NORMED
	*/
        int matchMethod = Imgproc.TM_CCOEFF_NORMED;//1 3 5

        Imgproc.matchTemplate(gray, tem, resultImage, matchMethod);
        HighGui.imshow("效果图片1", resultImage);
//	cout << resultImage;
        double threshold;
        int lastX = 0;
        int lastY = 0;

        for (int i = 0; i < resultImage.rows(); i++) {
            for (int j = 0; j < resultImage.cols(); j++) {
                threshold = 0.6;
                if (resultImage.get(i, j)[0] > threshold && resultImage.get(i, j)[0] < 1) {
                    int mx = Math.abs(lastX - j);
                    int my = Math.abs(lastY - i);
                    if (mx < 5 || my < 5) {
                        continue;
                    }
                    Imgproc.rectangle(src, new Point(j, i), new Point(j + tem.cols(), i + tem.rows()), new Scalar(0, 0, 255), 1, 8, 0);
                    System.out.println(j + " " + i);

                    lastX = j;
                    lastY = i;
//                    System.out.println(j + " " + i);
//                    Mat m = new Mat(src, new Rect(j, i, j + 50, i + 295));
//                    Imgcodecs.imwrite("/Users/chiaowenke/IdeaProjects/autotest/img/a1/" + i+j+".jpg", m);
                }
            }
        }
        Imgcodecs.imwrite("/Users/chiaowenke/IdeaProjects/autotest/compare/completed/" + System.currentTimeMillis() + ".jpg", src);
    }

    public ArrayList<Mat> getPicFromScreenShot295(String src) {
        Mat screenShot = Imgcodecs.imread(getScreenShotDir() + src);
        ArrayList<Mat> mats = new ArrayList<>();
//            col 2
        double aspectRatioW = screenShot.width() / 1080d;
        double aspectRatioH = screenShot.height() / 1920d;
        int offsetH = (int) (130d * aspectRatioH);
        int w = (int) (295d * aspectRatioW);
        int h = (int) (295d * aspectRatioH);

//            col 2
        int x = (int) (388 * aspectRatioW);
        int y = (int) (182 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 2, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 3, w, h)));
//            col 1
        x = (int) (84 * aspectRatioW);
        y = (int) (338 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 2, w, h)));

//col 3
        x = (int) (687 * aspectRatioW);
        y = (int) (338 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 2, w, h)));
        return mats;
    }

    public ArrayList<Mat> getPicFromScreenShot295() {
        ArrayList<Mat> mats = new ArrayList<>();
        double aspectRatioW = screenShot.width() / 1080d;
        double aspectRatioH = screenShot.height() / 1920d;
        int offsetH = (int) (130d * aspectRatioH);
        int w = (int) (295d * aspectRatioW);
        int h = (int) (295d * aspectRatioH);

//            col 2
        int x = (int) (388 * aspectRatioW);
        int y = (int) (182 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 2, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 3, w, h)));
//            col 1
        x = (int) (84 * aspectRatioW);
        y = (int) (338 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 2, w, h)));

//col 3
        x = (int) (687 * aspectRatioW);
        y = (int) (338 * aspectRatioH);
        mats.add(new Mat(screenShot, new Rect(x, y, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + h, w, h)));
        mats.add(new Mat(screenShot, new Rect(x, y + (h) * 2, w, h)));
        return mats;
    }
}
