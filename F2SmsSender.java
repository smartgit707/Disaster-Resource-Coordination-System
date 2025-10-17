package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class F2SmsSender {
    // Your actual Fast2SMS API key
    private static final String API_KEY = "tviHgyuG1qbA0CY7fZ6ernwLcMX5UdPlNO9EsDJoWKmkp8QVzIK5pLbn7TQwV4meXZN0iuGY6lcj9Ovy";

    public static void sendSms(String message, String recipientNumber) throws IOException {
        String url = "https://www.fast2sms.com/dev/bulkV2";

        String data = "sender_id=FSTSMS"
                + "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8)
                + "&route=q"
                + "&numbers=" + recipientNumber;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("authorization", API_KEY);
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        os.write(data.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();
        InputStream is = null;
        if (responseCode >= 200 && responseCode < 300) {
            is = con.getInputStream();
        } else {
            is = con.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line, response = "";
        while ((line = in.readLine()) != null) {
            response += line;
        }
        in.close();

        System.out.println("Response code: " + responseCode);
        System.out.println("SMS API Response: " + response);
    }


    // Test main method
    public static void main(String[] args) throws IOException {
        // Use 10-digit mobile number without +91:
        sendSms("Hello from Fast2SMS!", "7267884308");
    }
}
