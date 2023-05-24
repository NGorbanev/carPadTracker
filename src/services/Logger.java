package services;

import services.LogTypes;

import java.time.LocalDateTime;

public class Logger {

    private String sourse;
    public void writeLog(Object obj, LogTypes type, String logInfo) {
        this.sourse = obj.getClass().getSimpleName();
        System.out.println(LocalDateTime.now() + " | " + type  + " " + sourse + ": " + logInfo);
    }

    public void writeLog(String source, LogTypes type, String logInfo) {
        this.sourse = source;
        System.out.println(LocalDateTime.now() + " | " + type  + " " + source + ": " + logInfo);
    }
}
