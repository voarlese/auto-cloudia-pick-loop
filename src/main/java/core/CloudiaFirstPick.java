package core;

import bean.DesireBean;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import org.opencv.core.*;

import java.awt.Point;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static core.CompareConfig.*;

public class CloudiaFirstPick {

    boolean passing = false;

    private double aspectRatioW;
    private double aspectRatioH;
    public void startInput() {
        Adb adb = new Adb(getID());
        Point point =  adb.getScreenSize();
        aspectRatioW = point.x / 1080d;
        aspectRatioH = point.y / 1920d;
        Scanner s = new Scanner(System.in);
        boolean progress = true;
        System.out.println("輸入指令");
        while (progress) {
            String input = s.next();
            System.out.println("輸入 : " + input);
            doProgress(adb, input);
            progress = !input.equals("-exit");
        }
    }

    int times = 0;
    int whileTimes = 0;
    long startTime;
    long autoOrder = 0;

    public void doProgress(Adb adb, String progress) {
        if (progress.startsWith("pass")) {
            adb.tap((973 * aspectRatioW) + " " + (95 * aspectRatioH));
            return;
        }
        if (progress.startsWith("pick")) {
            adb.tap((250 * aspectRatioW) + " " + (1715 * aspectRatioH));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doProgress(adb, "st");
            return;
        }
        if (progress.startsWith("by-")) {
            progress = progress.replace("by-", "").replace("-", " ");
            adb.tap(progress);
            return;
        }
        if (progress.startsWith("st")) {
            passing = true;
            while (passing) {
                adb.tap((973 * aspectRatioW) + " " + (95 * aspectRatioH));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                times++;
                if (times == 5) {
                    times = 0;
                    passing = false;
                    System.out.println("關閉循環");
                    return;
                }
                System.out.println("點了 : " + times + " 次");
            }
            return;
        }
        if (progress.startsWith("1-shot")) {
            String name = progress.replace("1-shot-", "");
            System.out.println("開始拍照");
            System.out.println("-> 拍照後存放手機位置 ： " + "/sdcard/Download/" + name + ".png");
            adb.takeScreenShot("/sdcard/Download/" + name + ".png");
            System.out.println("-> 存入電腦比對資料夾 ： " + "/Users/chiaowenke/IdeaProjects/autotest/" + name + ".png");
            adb.pullFile("/sdcard/Download/" + name + ".png", "/Users/chiaowenke/IdeaProjects/autotest/" + name + ".png");
            System.out.println("-> 移除手機檔案");
            adb.deleteFile("/sdcard/Download/" + name + ".png");
            return;
        }
        if (progress.startsWith("shot")) {
            System.out.println("開始拍照");
            System.out.println("-> 拍照後存放手機位置 ： " + "/sdcard/Download/" + whileTimes + ".png");
            adb.takeScreenShot("/sdcard/Download/" + whileTimes + ".png");
            System.out.println("-> 存入電腦比對資料夾 ： " + "/Users/chiaowenke/IdeaProjects/autotest/compare/" + autoOrder + "/" + whileTimes + ".png");
            adb.pullFile("/sdcard/Download/" + whileTimes + ".png", "/Users/chiaowenke/IdeaProjects/autotest/compare/" + autoOrder + "/");
            System.out.println("-> 移除手機檔案");
            adb.deleteFile("/sdcard/Download/" + whileTimes + ".png");
            return;
        }
        if (progress.startsWith("compare")) {
            String path = progress.replace("compare-", "");
            new Compare().compareOnce(path);
            return;
        }

        if (progress.startsWith("auto")) {
            autoOrder = System.currentTimeMillis();
            File f = new File(getScreenShotDir() + "/" + autoOrder);
            f.mkdir(); // 建立資料夾
            if (DatabaseManager.getInstance().isInit()) {
                try {
                    DatabaseManager.getInstance().insert(
                            "INSERT INTO AUTO_ORDER VALUES('" +
                                    autoOrder + "','" +
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "'"
                                    + ");");
                } catch (SQLException throwables) {
                    System.out.println(throwables.getMessage());
                }
            }
            autoPick(adb);
            return;
        }

        if (progress.startsWith("repeat")) {
            autoPick(adb);
            return;
        }
        if (progress.startsWith("show-")) {
            String path = progress.replace("show-", "");
            new Compare().compareMultiple(path);
            return;
        }
        if(progress.startsWith("save-")) {
            String path = progress.replace("save-", "");
            new Compare().getPicFromScreenShot295(path);
            return;
        }

        if(progress.startsWith("fuck")) {
            new Compare().screenShotFuck();
            return;
        }
    }

    private void autoPick(Adb adb) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                startTime = System.currentTimeMillis();
                doProgress(adb, "pick");
                emitter.onNext(true);
            }
        }).concatMap(new Function<Boolean, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Boolean b) throws Throwable {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                        Thread.sleep(3000);
                        doProgress(adb, "shot");
                        Thread.sleep(2000);
                        emitter.onNext(whileTimes + ".png");
                    }
                });
            }
        }).concatMap(new Function<String, ObservableSource<HashMap<Integer, ResultBean>>>() {
            @Override
            public ObservableSource<HashMap<Integer, ResultBean>> apply(String s) throws Throwable {
                System.out.println("開始比對");
                return new Compare().getCompareResult(autoOrder + "/"+ s);
            }
        }).subscribe(
                new Observer<HashMap<Integer, ResultBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull HashMap<Integer, ResultBean> map) {
                        System.out.println("抽到的卡 ：" + map.size());
                        DesireBean desireBean = getDesireBean();
                        int RCount = 0;
                        int SRCount = 0;
                        int SSRCount = 0;
                        int roleCount = 0;
                        long pickNo = startTime;
                        String[] sqls = new String[map.size()];
                        int index = 0;
                        for (Map.Entry<Integer, ResultBean> entry : map.entrySet()) {
                            ResultBean resultBean = entry.getValue();
                            System.out.println("-> " + resultBean.getCard().getName());
                            desireBean.check(entry.getValue());
                            if (resultBean.getCard().getLevel().equals("R")) {
                                RCount++;
                            } else if (resultBean.getCard().getLevel().equals("SR")) {
                                SRCount++;
                            } else if (resultBean.getCard().getLevel().equals("SSR")) {
                                SSRCount++;
                            } else {
                                roleCount++;
                            }
//                                total(resultBean.getCard());
                            sqls[index++] = "INSERT INTO PICK (pick_no, order_no, card_no, pick_time) VALUES('" +
                                    pickNo + "','" +
                                    autoOrder + "','" +
                                    resultBean.getCard().getCardNo() + "','" +
                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(pickNo)) + "'" +
                                    ");";
                        }
//                            CompareConfig.SSRCountRecord(SSRCount);
                        System.out.println("-> R : " + RCount);
                        System.out.println("-> SR : " + SRCount);
                        System.out.println("-> SSR : " + SSRCount);
                        System.out.println("-> role : " + roleCount);
                        System.out.println("----");
                        System.out.println("Complated times : " + whileTimes++);
                        System.out.println("耗時 : " + ((double) (System.currentTimeMillis() - startTime) / 1000) + " 秒");
                        // 存入資料庫
                        if (DatabaseManager.getInstance().isInit()) {
                            try {
                                DatabaseManager.getInstance().insertBatch(sqls);
                            } catch (SQLException throwables) {
                                System.out.println(throwables.getMessage());
                            }
                        }
                        if (desireBean.isComplete()) {
                            System.out.println("全部抽到拉～");
                        } else {
                            System.out.println("沒抽到想要的ＱＱ～繼續");
                            doProgress(adb, "repeat");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("error : " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
