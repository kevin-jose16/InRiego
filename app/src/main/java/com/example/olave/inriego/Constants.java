package com.example.olave.inriego;

/**
 * Created by olave on 31/07/2018.
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.example.olave.inriego.action.main";
        public static String STARTFOREGROUND_ACTION = "com.example.olave.inriego.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.example.olave.inriego.action.stopforeground";
    }
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
