package com.hawolt;

import com.hawolt.http.LocalExecutor;
import com.hawolt.logger.Logger;
import com.hawolt.mitm.rule.RuleInterpreter;
import com.hawolt.socket.SocketServer;
import com.hawolt.ui.Netherblade;
import com.hawolt.util.ReflectHttp;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;

/**
 * Created: 30/07/2022 15:37
 * Author: Twitter @hawolt
 **/

public class Main {

    public static void main(String[] args) {
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
            RuleInterpreter.reload(null);
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
