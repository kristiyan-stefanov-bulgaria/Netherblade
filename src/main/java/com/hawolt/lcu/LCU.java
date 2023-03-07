package com.hawolt.lcu;

import com.hawolt.logger.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.Base64;

public class LCU {

    private static OkHttpClient ok;

    static {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        try {

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            LCU.ok = builder.build();
        } catch (Exception e) {
            Logger.error("Failed to setup OkHttp client for LCU, exiting (2)");
            System.exit(2);
        }
    }

    private final Request request;

    public LCU(LeagueClient client, String method, String resource) {
        this(client, method, resource, new byte[0]);
    }

    public LCU(LeagueClient client, String method, String resource, byte[] body) {
        String endpoint = String.format("https://127.0.0.1:%s%s", client.getLeaguePort(), resource);
        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(("riot:" + client.getLeagueAuth()).getBytes()))
                .addHeader("User-Agent", "LeagueOfLegendsClient/")
                .addHeader("Content-type", "application/json")
                .addHeader("Accept", "application/json")
                .url(endpoint);
        if (method.equals("GET")) {
            builder.get();
        } else {
            builder.method(method, RequestBody.create(body));
        }
        this.request = builder.build();
    }

    public Response execute() throws IOException {
        return ok.newCall(request).execute();
    }
}
