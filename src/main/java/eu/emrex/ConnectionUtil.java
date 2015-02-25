package eu.emrex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ConnectionUtil {

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


    public static String getJson(HttpURLConnection conn) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = conn.getInputStream();
        byte[] buf = new byte[1024];

        while (is.read(buf) != -1) {
            bos.write(buf);
        }
        return bos.toString();
    }
}
