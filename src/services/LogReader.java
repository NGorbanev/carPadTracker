package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LogReader {
    final Path logPath = Path.of(
            "/Users/nikitagorbanev/Library/Mobile Documents/" +
                    "iCloud~is~workflow~my~workflows/Documents/Logs/CariPadLog.csv");
    File logfile = new File(logPath.toString());
    String[] fileData;
    Logger log = new Logger();

    public int getFileDataLength(){
        loadData();
        return this.fileData.length;
    }

    // converter from month name from russian wording to month num
    public HashMap<String, String> getUniqueMonths(){
        log.writeLog(this, LogTypes.INFO, "Parsing month name.. ");
        loadData();
        HashMap<String, String> months = new HashMap<>();
        for (String line: fileData){
            String[] ln = line.split(",");
            if (ln.length > 1){
                String[] dt = ln[1].split(" ");
                if (!months.containsKey(dt[1])){
                    switch (dt[1]){
                        case "янв.":
                            months.put(dt[1], "01");
                            break;
                        case "февр.":
                            months.put(dt[1], "02");
                            break;
                        case "марта":
                            months.put(dt[1], "03");
                            break;
                        case "апр.":
                            months.put(dt[1], "04");
                            break;
                        case "мая":
                            months.put(dt[1], "05");
                            break;
                        case "июня":
                            months.put(dt[1], "06");
                            break;
                        case "июля":
                            months.put(dt[1], "07");
                            break;
                        case "авг.":
                            months.put(dt[1], "08");
                            break;
                        case "сент.":
                            months.put(dt[1], "09");
                            break;
                        case "окт.":
                            months.put(dt[1], "10");
                            break;
                        case "нояб.":
                            months.put(dt[1], "11");
                            break;
                        case "дек.":
                            months.put(dt[1], "12");
                            break;
                    }
                }
            }
        }
        log.writeLog(this, LogTypes.INFO, "OK");
        return months;
    }

    public void loadData(){
        try {
            fileData = Files.readString(logfile.toPath()).split(System.lineSeparator());
            }
        catch (IOException e){
            log.writeLog(this, LogTypes.FATAL, "File reading error \n" + e.getMessage());
            e.getMessage();
        }
    }

    //  Method to convert Apple automator data format to dd.MM.yyyy
    public String convertDate(String str){
        log.writeLog(this, LogTypes.INFO, "Converting month name to month num");
        String[] parts = str.split(" ");
        if (parts[0].trim().length() == 1) parts[0] = "0" + parts[0];
        return parts[0] + "." + getUniqueMonths().get(parts[1]) + "." + parts[2].substring(0,4);
    }

    // method for getting the last known position - it's the last record at csv
    public String[] getLastKnownPosition(){

        log.writeLog(this, LogTypes.INFO, "Getting last known position");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        CoordsConverter cc = new CoordsConverter();

        loadData();
        String[] line = fileData[fileData.length-1].split(",");
        String[] editedLine = new String[9];
        log.writeLog(this, LogTypes.INFO, "Update received");
        LocalDate date = LocalDate.parse(convertDate(line[1]), dateFormat);
        LocalTime time = LocalTime.parse(line[2].trim(), timeFormat);
        editedLine[0] = line[0].trim(); // device info
        editedLine[1] = date.format(newFormat); // date dd.MM.yyyy
        editedLine[2] = time.toString(); // time
        editedLine[3] = line[3]; // lattitude (lat)
        editedLine[4] = line[4]; // lon
        editedLine[5] = line[5]; // type
        editedLine[6] = line[6]; // acc charge level

        ArrayList<String> nearByaddresses = cc.convert(line[4], line[3]);

        editedLine[7] = nearByaddresses.get(0).substring(1, nearByaddresses.get(0).length()-1);
        editedLine[8] = nearByaddresses.get(1).substring(1, nearByaddresses.get(1).length()-1);
        log.writeLog(this, LogTypes.INFO, "getLastKnownPosition() processed");

        return editedLine;
    }

    // attempt to send a notification through Apple Script launched from Terminal
    public void notificationTest(){
        try {
            Process process = Runtime.getRuntime().exec(
                    "osascript -e 'display notification \"This is the test\" with title \"Test notif\"'");
            printResults(process);

        } catch (IOException e){
            e.getMessage();
        }
    }

    // just for demonstration of results of previous attempt
    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
