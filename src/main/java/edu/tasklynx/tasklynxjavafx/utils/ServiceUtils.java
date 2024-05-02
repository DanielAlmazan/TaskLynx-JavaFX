package edu.tasklynx.tasklynxjavafx.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

public class ServiceUtils {
    private static String token = null;
    public static final String SERVER = "http://localhost:8080";

    public static void setToken(String token) {
        ServiceUtils.token = token;
    }

    public static void removeToken() {
        ServiceUtils.token = null;
    }

    // Get charset encoding
    public static String getCharset(String contentType) {
        for (String param: contentType.replace(" ", "").split(";", 0)) {
            if (param.startsWith("charset=")) {
                return param.split("=", 2)[1];
            }
        }
        return null;
    }

    public static String getResponse (String url, String data, String method) {
        BufferedReader bufInput = null;
        StringJoiner response = new StringJoiner("\n");
        try {
            URL urlConn = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConn.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(method);

            connection.setRequestProperty("Host", "localhost");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-encoding", "gzip, deflate, br, sdch");
            connection.setRequestProperty("Accept-language", "es-ES, es;q=0.8");
            connection.setRequestProperty("Accept-charset", "utf-8");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");

            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            if (data != null) {
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(data.getBytes());
                wr.flush();
                wr.close();
            }

            String charset = getCharset(connection.getHeaderField("Content-Type"));

            if (charset == null) {
                InputStream in = connection.getInputStream();
                if ("gzip".equals(connection.getContentEncoding())) {
                    in = new GZIPInputStream(in);
                }

                bufInput = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = bufInput.readLine()) != null) {
                    response.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufInput != null) {
                try {
                    bufInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public static CompletableFuture<String> getResponseAsync(String url, String data, String method) {
        return CompletableFuture.supplyAsync(() -> getResponse(url, data, method));
    }
}