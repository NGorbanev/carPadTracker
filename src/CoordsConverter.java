import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CoordsConverter {
    private static HttpClient client = HttpClient.newHttpClient();
    StringBuilder urlAddress = new StringBuilder("https://api.geotree.ru/address.php");
    String key = "iXmn9L2wKCHf";
    //URI address = URI.create("https://api.geotree.ru/address.php");

// https://api.geotree.ru/address.php?key=iXmn9L2wKCHf&lon=30.31043240394659&lat=60.05716195393344

    public ArrayList<String> convert(String lon, String lat){
        ArrayList<String> nearByAddresses = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlAddress.append("?key=" + key + "&lon=" + lon + "&lat="+ lat + "&types=address").toString()))
                .GET()
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement elements = JsonParser.parseString(response.body());
            JsonArray jsonElements = elements.getAsJsonArray();
            for (JsonElement element : jsonElements){
                //System.out.println(element + "\n");
                JsonObject fullInfo = element.getAsJsonObject();
                fullInfo.get("value");
                nearByAddresses.add(String.valueOf(fullInfo.get("value")));
            }


        } catch (IOException | InterruptedException ex) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return nearByAddresses;
    }
}
