package services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Logger {

    final Path path = Path.of("config.cfg");
    int loggingLevel;
    String[] config = new String[1];
    public Logger() {
        try {
            config = Files.readString(path).split(System.lineSeparator());
        } catch (IOException ex) {
            writeLog(this, LogTypes.WARN, String.format("Config reading error. %s", ex.getMessage()));
            writeLog(this, LogTypes.FATAL, String.format("Logging level is set to %s", loggingLevel));
        }
        //this.loggingLevel = config[0];
        if (config[0] == null) loggingLevel = 2;
        else {
            switch (config[0]) {
                case "TRACE":
                    this.loggingLevel = 0;
                    break;
                case "INFO":
                    this.loggingLevel = 1;
                    break;
                case "WARN":
                    this.loggingLevel = 2;
                    break;
                case "FATAL":
                    this.loggingLevel = 2;
                    break;
                default:
                    this.loggingLevel = 2;
                    break;

            }
        }
        writeLog(this, LogTypes.WARN, String.format("Logging level is set to %s", loggingLevel));
    }

    private boolean isToSend(LogTypes type) {
        switch (loggingLevel) {
            case 0:
                return true;
            case 1:
                if (type != LogTypes.TRACE) return true;
                else return false;
            case 2:
                if (type != LogTypes.TRACE && type != LogTypes.INFO) return true;
                else return false;
            default: return false;
        }
    }

    private String source;
    public void writeLog(Object obj, LogTypes type, String logInfo) {
        this.source = obj.getClass().getSimpleName();
        if (isToSend(type)) {
            System.out.println(LocalDateTime.now() + " | " + type + " " + source + ": " + logInfo);
        }
    }

    public void writeLog(String source, LogTypes type, String logInfo) {
        this.source = source;
        if (isToSend(type)) {
            System.out.println(LocalDateTime.now() + " | " + type + " " + source + ": " + logInfo);
        }
    }
}
