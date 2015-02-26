package eu.emrex;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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


    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, "UTF-8");
    }


    public static <T> T getJsonObjectFromRequest(HttpServletRequest request) throws IOException {
        StringBuffer jb = new StringBuffer();
        String line = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            jb.append(line);

        String inputReq = jb.toString();

        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<T>() {
        }.getType();
        return gson.fromJson(inputReq, type);
    }


    public static int levenshteinDistance(String s, String t) {
        if (s == null && t == null) {
            return 0;
        }
        if (s == null || s.length() == 0) {
            return t.length();
        }
        if (t == null || t.length() == 0) {
            return s.length();
        }

        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        for (int i = 0; i < s.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            // edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < t.length(); j++) {
                int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
                v1[j + 1] = Math.min(Math.min(v1[j] + 1, v0[j + 1] + 1), v0[j] + cost);
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            for (int j = 0; j < v0.length; j++) {
                v0[j] = v1[j];
            }
        }

        return v1[t.length()];
    }

}
