package com.hawolt;

import com.hawolt.http.IRequestModifier;
import com.hawolt.http.LocalExecutor;
import com.hawolt.http.proxy.BasicProxyServer;
import com.hawolt.http.proxy.ProxyRequest;
import com.hawolt.http.proxy.ProxyResponse;
import com.hawolt.logger.Logger;
import com.hawolt.socket.SocketServer;
import com.hawolt.ui.Netherblade;
import com.hawolt.util.ReflectHttp;
import com.hawolt.yaml.SystemYaml;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created: 30/07/2022 15:37
 * Author: Twitter @hawolt
 **/

public class Main {

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("TASKKILL /F /IM LeagueClient.exe");
        Runtime.getRuntime().exec("TASKKILL /F /IM RiotClientUx.exe");
        try {
            ReflectHttp.enable("PATCH");
            Javalin.create(config -> config.addStaticFiles("/html", Location.CLASSPATH))
                    .before("/v1/*", context -> {
                        context.header("Access-Control-Allow-Origin", "*");
                    })
                    .routes(LocalExecutor::configure)
                    .start(35199);
            SocketServer.launch();
            Netherblade.create();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logger.error(e);
            System.err.println("Unable to modify permitted HTTP methods, exiting (1).");
            System.exit(1);
        } catch (IOException e) {
            Logger.error(e);
            System.err.println("Unable to launch Netherblade, exiting (1).");
            System.exit(1);
        }
    }

}
