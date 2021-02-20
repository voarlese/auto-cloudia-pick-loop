package core;

import bean.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.parser.TokenType;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompareConfig {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static HashMap<String, CardBean> picNameMap;
    private static ArrayList<CardBean> cards;
    static final String templatePath = "/Users/chiaowenke/IdeaProjects/autotest/template.jpg";
    static final String SQL_CARD_DATA = "select A.*, B.level_name, C.type_name, D.coordinate_x, D.coordinate_y, D.img_name from CARD as A, CARD_LEVEL as B, CARD_TYPE as C, cardImage as D where A.level_no = B.level_no and A.type_no = C.type_no and A.card_no = D.card_no order by A.card_no;";
    // 比較用 執行緒
    private static ExecutorService sch1;
    private static ExecutorService sch2;
    private static ExecutorService sch3;
    private static ExecutorService sch4;
    private static ExecutorService sch5;
    private static ArrayList<SourceBean> sourceList;
    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "jpg", "gif", "png", "bmp" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    private static String iWantIt(String name) {
        return "\033[47;4m" + name + "\033[0m";
    }

    private static String role(String name) {
        return "\033[33;4m" + name + "\033[0m";
    }

    public static void init() {
        if (picNameMap == null) {
            picNameMap = new HashMap<>();
            cards = new ArrayList<>();
            if (DatabaseManager.getInstance().isInit()) {
                ResultSet resultSet = null;
                Connection conn = null;
                Statement stmt = null;
                try {
                    conn = DatabaseManager.getInstance().getConnection();
                    stmt = conn.createStatement();
                    resultSet = stmt.executeQuery(SQL_CARD_DATA);
                    while (resultSet.next()) {
                        CardBean cardBean = new CardBean();
                        cardBean.setCardNo(resultSet.getString("card_no"));
                        cardBean.setName(resultSet.getString("name"));
                        cardBean.setLevel(resultSet.getString("level_name"));
                        cardBean.setType(resultSet.getString("type_no"));
                        cardBean.setTypeName(resultSet.getString("type_name"));
                        cardBean.setCenterPoint(new Point(resultSet.getDouble("coordinate_x"), resultSet.getDouble("coordinate_y")));
                        cardBean.setImageName(resultSet.getString("img_name"));
                        picNameMap.put(resultSet.getString("img_name"), cardBean);
                        cards.add(cardBean);
                    }
                    resultSet.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException throwables) {
                    try {
                        resultSet.close();
                        stmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    throwables.printStackTrace();
                }
            } else {
                cards = new Gson().fromJson(FileUtils.readLineByLineJava8(getPathBean().getCard()), new TypeToken<ArrayList<CardBean>>(){}.getType());
                for (CardBean card : cards) {
                    picNameMap.put(card.getImageName(), card);
                }
            }

            System.out.println(picNameMap);
        }
        sch1 = Executors.newSingleThreadExecutor();
        sch2 = Executors.newSingleThreadExecutor();
        sch3 = Executors.newSingleThreadExecutor();
        sch4 = Executors.newSingleThreadExecutor();
        sch5 = Executors.newSingleThreadExecutor();

        sourceList = new ArrayList<>();
        File dir = new File(getPathBean().getImg());
        File[] sourcesFile = dir.listFiles(CompareConfig.IMAGE_FILTER);
        for (int i = 0; i < sourcesFile.length; i++) {
            SourceBean sourceBean = new SourceBean(sourcesFile[i], Imgcodecs.imread(sourcesFile[i].getAbsolutePath()));
            sourceList.add(sourceBean);
        }
    }

    /**
     * 用中心點判斷卡片 (大圖用)
     *
     * @param matchPoint
     * @return
     */
    public static CardBean getCard(Point matchPoint) {
        for (CardBean card : cards) {
            Point cardCenter = card.getCenterPoint();
            double distance = Math.sqrt(Math.abs((matchPoint.x - cardCenter.x) * (matchPoint.x - cardCenter.x) + (matchPoint.y - cardCenter.y) * (matchPoint.y - cardCenter.y)));
//            double distance = Math.sqrt(Math.pow(Math.abs(matchPoint.x - cardCenter.x), Math.abs(matchPoint.x - cardCenter.x)) + Math.pow(Math.abs(matchPoint.y - cardCenter.y), Math.abs(matchPoint.y - cardCenter.y)));
            System.out.println("兩點距離 : " + distance);
            if (distance < 137.5) {
                return card;
            }
        }
        return null;
    }

    public static HashMap<String, CardBean> getPicNameMap() {
        return picNameMap;
    }


    private static HashMap<String, Integer> totalMap;
    private static HashMap<String, Integer> levelTotalMap;
    private static HashMap<Integer, Integer> onceCountMap;

    /**
     * 統計
     *
     * @param cardBean
     */
    public static void total(CardBean cardBean) {
        if (totalMap == null) {
            totalMap = new HashMap<>();
        }
        if (levelTotalMap == null) {
            levelTotalMap = new HashMap<>();
            levelTotalMap.put("R", 0);
            levelTotalMap.put("SR", 0);
            levelTotalMap.put("SSR", 0);
            levelTotalMap.put("Role", 0);
        }

        if (!totalMap.containsKey(cardBean.getName())) {
            totalMap.put(cardBean.getName(), 1);
        } else {
            totalMap.put(cardBean.getName(), totalMap.get(cardBean.getName()) + 1);
        }

        if (cardBean.getLevel().equals("R")) {
            levelTotalMap.put("R", levelTotalMap.get("R") + 1);
        } else if (cardBean.getLevel().equals("SR")) {
            levelTotalMap.put("SR", levelTotalMap.get("SR") + 1);
        } else if (cardBean.getLevel().equals("SSR")) {
            levelTotalMap.put("SSR", levelTotalMap.get("SSR") + 1);
        } else {
            levelTotalMap.put("Role", levelTotalMap.get("Role") + 1);
        }
    }

    public static CardBean getCardBean(String fileName) {
        return picNameMap.get(fileName);
    }

    public static void SSRCountRecord(int SSRCount) {
        if (onceCountMap == null) {
            onceCountMap = new HashMap<>();
            onceCountMap.put(0, 0);
            onceCountMap.put(1, 0);
            onceCountMap.put(2, 0);
            onceCountMap.put(3, 0);
            onceCountMap.put(4, 0);
            onceCountMap.put(5, 0);
            onceCountMap.put(6, 0);
            onceCountMap.put(7, 0);
            onceCountMap.put(8, 0);
            onceCountMap.put(9, 0);
            onceCountMap.put(10, 0);
        }
        // 計算ssr 的次數
        onceCountMap.put(SSRCount, onceCountMap.get(SSRCount) + 1);
    }

    public static SqlBean getSQLBean() {
        return new Gson().fromJson(FileUtils.readLineByLineJava8(getPathBean().getSql()), SqlBean.class);
    }

    public static PathBean getPathBean() {
        return new Gson().fromJson(FileUtils.readLineByLineJava8("../../../config/path.json"), PathBean.class);
    }

    public static String getScreenShotDir() {
        return getPathBean().getScreenShotDir();
    }

    public static DesireBean getDesireBean() {
        String desireJson = FileUtils.readLineByLineJava8(getPathBean().getDesire());
        return new Gson().fromJson(desireJson, DesireBean.class);
    }

    public static String getID() {
        return new Gson().fromJson(FileUtils.readLineByLineJava8(getPathBean().getDevice()), DeviceBean.class).getId();
    }

    public static HashMap<String, Integer> getLevelTotalMap() {
        return levelTotalMap;
    }

    public static HashMap<String, Integer> getTotalMap() {
        return totalMap;
    }

    public static HashMap<Integer, Integer> getOnceCountMap() {
        return onceCountMap;
    }

    public static ExecutorService getSch1() {
        return sch1;
    }

    public static ExecutorService getSch2() {
        return sch2;
    }

    public static ExecutorService getSch3() {
        return sch3;
    }

    public static ExecutorService getSch4() {
        return sch4;
    }

    public static ExecutorService getSch5() {
        return sch5;
    }

    public static ArrayList<SourceBean> getSourceList() {
        return sourceList;
    }

    public static void setSourceList(ArrayList<SourceBean> sourceList) {
        CompareConfig.sourceList = sourceList;
    }

    public static void destroy() {
        sch1.shutdown();
        sch2.shutdown();
        sch3.shutdown();
        sch4.shutdown();
        sch5.shutdown();
    }
}
