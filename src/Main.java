import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import services.LogReader;
import services.LogTypes;
import services.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    //public static int DEFAULT_TG_CHAT = -484021021;
    public static int DEFAULT_TG_CHAT = 203120607;
    private final static int TABLET_CRITICAL_BATTERY_LEVEL = 30;
    public static Logger log = new Logger();
    public static void main(String[] args) {
        LogReader lr = new LogReader();
        int recordsCount = lr.getFileDataLength();
        log.writeLog("main", LogTypes.INFO, "Service started");
        try {
            while (true) {
                if (recordsCount < lr.getFileDataLength()) {
                    log.writeLog("main", LogTypes.INFO, "new record detected");
                    String report = makeReport(lr);
                    log.writeLog("main", LogTypes.INFO, "Report text: " + report.toString());
                    sendReportToTelegram(report); // enable / disable report to TG sending
                    log.writeLog("main", LogTypes.INFO, "Report is posted to TG, chatId: " + DEFAULT_TG_CHAT);
                    recordsCount = lr.getFileDataLength();
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException ex){
            log.writeLog("main", LogTypes.FATAL, "Unexpected error: " + ex.getMessage());
            sendReportToTelegram("Информер остановлен из-за серверной ошибки");
        }
    }

    public static String makeReport(LogReader lr){
        System.out.println("Данные из последней записи в логе:\n");
        String[] reportFull = lr.getLastKnownPosition();
        //for (String part: reportFull){
        //    System.out.println(" - " + part.trim());
        //}

        String batIcon;
        if (Integer.valueOf(reportFull[6]) < TABLET_CRITICAL_BATTERY_LEVEL) batIcon = "🪫";
        else batIcon = "🔋";

        StringBuilder report = new StringBuilder();
        report.append("Данные по позиции авто:%0A");
        report.append("🚘 Источник:%20" + reportFull[0] + "%0A");
        report.append("📆 Дата и время записи:%20" + reportFull[1] + " " + reportFull[2] + "%0A");
        report.append("📋 Тип записи: " + reportFull[5] + "%0A");
        report.append(batIcon + " Уровень заряда планшета:%20" + reportFull[6] + "%0A");
        report.append("Координаты: " + reportFull[4] + "," + reportFull[3] + "%0A");
        report.append("%0A🌏 Авто расположено около:%20%0A");
        report.append("%0A - " + reportFull[7]);
        report.append("%0A - " + reportFull[8] + "%0A");
        log.writeLog("main", LogTypes.INFO, "Report generated");
        return report.toString();
    }

    private static void sendReportToTelegram(String reportText){
        HttpClient client = HttpClient.newHttpClient();
        String[] tmpStr = reportText.split(" ");
        StringBuilder finalText = new StringBuilder();
        for (String word : tmpStr){
            finalText.append("%20" + word);
        }
        String str = "https://api.telegram.org/" +
                "bot6228293833:AAH9IVD79rydktc5MQcd8J9C_CM0-Ai_Buc/" +
                "sendMessage?chat_id="+DEFAULT_TG_CHAT+"&text="+finalText.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(str))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.writeLog("main", LogTypes.INFO, "Sending report to TG.. ");
            JsonElement jsonElement = JsonParser.parseString(response.body());
            if (!jsonElement.isJsonObject()){
                log.writeLog("main", LogTypes.WARN, "Server response is not the same as expected");
            }
        } catch (IOException | InterruptedException e) { // обработка ошибки отправки запроса
            log.writeLog("main", LogTypes.WARN, "TG Message was not sent");
        }
    }
}