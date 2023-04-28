import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static int DEFAULT_TG_CHAT = -484021021;
    public static void main(String[] args) {
        LogReader lr = new LogReader();
        int recordsCount = lr.getFileDataLength();
        try {
            while (true) {
                if (recordsCount < lr.getFileDataLength()) {
                    sendReportToTelegram(makeReport(lr));
                    recordsCount = lr.getFileDataLength();
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException ex){
            sendReportToTelegram("Информер остановлен из-за серверной ошибки");
        }
        //sendReportToTelegram(report.toString());
    }

    public static String makeReport(LogReader lr){
        //LogReader lr = new LogReader();

        System.out.println("Данные из последней записи в логе:\n");
        String[] reportFull = lr.getLastKnownPosition();
        for (String part: reportFull){
            System.out.println(" - " + part.trim());
        }

        String batIcon;
        if (Integer.valueOf(reportFull[6]) < 30) batIcon = "🪫";
        else batIcon = "🔋";

        StringBuilder report = new StringBuilder();
        report.append("Данные по позиции авто:%0A");
        report.append("🚘 Источник:%20" + reportFull[0] + "%0A");
        report.append("📆 Дата и время:%20" + reportFull[1] + " " + reportFull[2] + "%0A");
        report.append("📋 Тип записи: " + reportFull[5] + "%0A");
        report.append(batIcon + " Уровень заряда планшета:%20" + reportFull[6] + "%0A");
        report.append("%0A🌏 Авто расположено около:%20%0A");
        report.append("%0A - " + reportFull[7]);
        report.append("%0A - " + reportFull[8] + "%0A");
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
            System.out.println("Отправка нотификации в TG ... " + response.statusCode());
            //System.out.println("Ответ: " + response.body());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            if (!jsonElement.isJsonObject()){
                System.out.println("Ответ от сервера не соответствует ожидаемому.");
                return;
            }
        } catch (IOException | InterruptedException e) { // обработка ошибки отправки запроса
            System.out.println("Какая-то ошибка в адресе. Сообщение не отправлено");
        }
    }
}