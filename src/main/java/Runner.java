
import bean.CardBean;
import core.*;
import sun.security.krb5.Config;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Runner {

    public static void main(String[] args) {
        CtrlC c = new CtrlC();
        Thread t = new Thread(c);
        t.setName("Ctrl C Thread");
        t.start();
        // 檢查 有沒有填資料庫參數
        if (StringUtils.isNotBlank(CompareConfig.getSQLBean().getUrl())) {
            DatabaseManager.getInstance().init();
        }
        CompareConfig.init();
        CloudiaFirstPick cloudFirstPick = new CloudiaFirstPick();
        cloudFirstPick.startInput();
    }

    public static class CtrlC implements Runnable {
        private boolean bExit = false;
        private class ExitHandler extends Thread {
            public ExitHandler() {
                super("Exit Handler");
            }
            public void run() {
                DatabaseManager.getInstance().close();
                CompareConfig.destroy();
//                System.out.println("統計");
//                System.out.println("單張卡片次數");
//
//                for (Map.Entry<String, Integer> stringIntegerEntry : CompareConfig.getTotalMap().entrySet()) {
//                    System.out.println("-> " + stringIntegerEntry.getKey() + " : " + stringIntegerEntry.getValue());
//                }
//                System.out.println("Level次數");
//
//                for (Map.Entry<String, Integer> stringIntegerEntry : CompareConfig.getLevelTotalMap().entrySet()) {
//                    System.out.println("-> " + stringIntegerEntry.getKey() + " : " + stringIntegerEntry.getValue());
//                }
//                System.out.println("每次紅球次數");
//
//                for (Map.Entry<Integer, Integer> integerIntegerEntry : CompareConfig.getOnceCountMap().entrySet()) {
//                    System.out.println("-> " + integerIntegerEntry.getKey() + " : " + integerIntegerEntry.getValue());
//                }
//                bExit = true;
            }
        }
        public CtrlC() {
            Runtime.getRuntime().addShutdownHook(new ExitHandler());
        }
        public void run() {
            while (!bExit) {
            }
            System.out.println("Exit OK");
        }
    }
}
