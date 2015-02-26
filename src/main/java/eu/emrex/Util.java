package eu.emrex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {

    public static HttpURLConnection setupConnection(String wsurl, String method) throws MalformedURLException,
            IOException,
            ProtocolException {
        URL url = new URL(wsurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        return conn;
    }


    public static String getDataFromConnection(HttpURLConnection conn) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = conn.getInputStream();
        byte[] buf = new byte[1024];

        while (is.read(buf) != -1) {
            bos.write(buf);
        }
        return bos.toString();
    }


    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, "UTF-8");
    }
}
