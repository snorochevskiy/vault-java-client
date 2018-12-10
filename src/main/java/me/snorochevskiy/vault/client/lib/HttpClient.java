package me.snorochevskiy.vault.client.lib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpClient {

    public static String post(String url, Map<String, String> headers, String payload) throws IOException {
        System.out.println("POST: " + url);

        URL reqUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) reqUrl.openConnection();
        con.setRequestMethod("POST");

        Optional.of(headers).orElse(Collections.emptyMap())
                .forEach((k, v) -> con.setRequestProperty(k, v));
        {
            con.setDoOutput(true);
            DataOutputStream pw = new DataOutputStream(con.getOutputStream());
            pw.writeBytes(payload);
            pw.flush();
            pw.close();
        }

        String responseText = new BufferedReader(new InputStreamReader(con.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        con.getInputStream().close();
        con.disconnect();

        return responseText;
    }

    public static String postUnchecked(String url, Map<String, String> headers, String payload) {
        System.out.println("POST: " + url);
        try {
            URL reqUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) reqUrl.openConnection();
            con.setRequestMethod("POST");

            Optional.of(headers).orElse(Collections.emptyMap())
                    .forEach((k, v) -> con.setRequestProperty(k, v));
            {
                con.setDoOutput(true);
                DataOutputStream pw = new DataOutputStream(con.getOutputStream());
                pw.writeBytes(payload);
                pw.flush();
                pw.close();
            }

            String responseText = new BufferedReader(new InputStreamReader(con.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

            con.getInputStream().close();
            con.disconnect();

            return responseText;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String url, Map<String, String> headers) {
        return call("GET", url, headers, null);
    }

    public static String put(String url, Map<String, String> headers, String payload) {
        return call("PUT", url, headers, payload);
    }

    public static String call(String method, String url, Map<String, String> headers, String payload) {
        System.out.println(method + ": " + url);
        try {
            URL reqUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) reqUrl.openConnection();
            con.setRequestMethod(method);
            Optional.of(headers).orElse(Collections.emptyMap())
                    .forEach((k, v) -> con.setRequestProperty(k, v));

            if (payload != null) {
                con.setDoOutput(true);
                DataOutputStream pw = new DataOutputStream(con.getOutputStream());
                pw.writeBytes(payload);
                pw.flush();
                pw.close();
            }

            if (con.getResponseCode() == 401 || con.getResponseCode() == 403) {
                throw new AuthException(url);
            }

            String responseText = null;
            if (con.getResponseCode() != 204 && con.getResponseCode() != 404) {
                responseText = new BufferedReader(new InputStreamReader(con.getInputStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));
                con.getInputStream().close();
            }

            con.disconnect();

            return responseText;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
