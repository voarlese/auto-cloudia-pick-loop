package core;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Adb {
    String ID;

    public Adb(String deviceID) {
        ID = deviceID;
    }

    public static String command(String command) {
        if (command.startsWith("adb")) {
            command = command.replace("adb ", ServerManager.getAndroidHome() + "/platform-tools/adb ");
        } else {
            throw new RuntimeException("This method is desingend to run ADB commands only");
        }
        String output = ServerManager.runCommand(command);
        if (output == null) {
            return "";
        } else {
            return output;
        }
    }

    public static void killServer() {
        command("adb kill-server");
    }

    public static void startServer() {
        command("adb start-server");
    }

    public static ArrayList<String> getConnectedDevices() {
        ArrayList<String> list = new ArrayList();
        String output = command("adb devices");
        for (String s : output.split("\n")) {
            s = s.trim();
            if (s.endsWith("device")) {
                list.add(s.replace("device", "").trim());
            }
        }
        return list;
    }

    public String getForegroundActivity() {
        return command("adb -s " + ID + " shell dumpsys window windows | grep mCurrentFocus");
    }

    public String getAndroidVersionString() {
        String output = command("adb -s " + ID + " shell getprop ro.build.version.release");
        if (output.length() == 3) {
            output +=".0";
        }
        return output;
    }

    public Point getScreenSize() {
        String output = command("adb shell wm size");
        System.out.println(output);
        String[] wm = output.replace("Physical size: ", "").split("x");
        Point p = new Point();
        p.x = Integer.parseInt(wm[0].trim());
        p.y = Integer.parseInt(wm[1].trim());
        return p;
    }

    public ArrayList<String> getInstalledPackage() {
        ArrayList<String> packages = new ArrayList<>();
        String[] outputs = command("adb -s " + ID + " shell pm list packages").split("\n");
        for (String packageId : outputs) {
            packages.add(packageId.replace("package:", "").trim());
        }
        return packages;
    }

    public void openActivity(String packageID, String activityID) {
        command("adb -s " + ID + " shell am start -c api.android.intent.category.LAUNCHER -a api.android.intent.action.MAIN -n " + packageID + "/" + activityID);
    }

    public void tap(String path) {
        command("adb shell input tap " + path);
    }

    public void installApp(String apkPath) {
        command("adb -s " + ID + " install " + apkPath);
    }

    public void pullFile(String source, String target) {
        command("adb -s " + ID + " pull " + source + " " + target);
    }

    public void deleteFile(String target) {
        command("adb -s " + ID + " shell rm " + target);
    }

    public void moveFile(String source, String target) {
        command("adb -s " + ID + " shell mv " + source + " " + target);
    }

    public void takeScreenShot(String target) {
        command("adb -s " + ID + " shell screencap " + target);
    }

}
