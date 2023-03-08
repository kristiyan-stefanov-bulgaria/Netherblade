package com.hawolt;

import com.hawolt.http.LocalExecutor;
import com.hawolt.logger.Logger;
import com.hawolt.mitm.cache.InternalStorage;
import com.hawolt.mitm.rule.RuleInterpreter;
import com.hawolt.socket.SocketMessageCallback;
import com.hawolt.socket.SocketProxy;
import com.hawolt.socket.SocketServer;
import com.hawolt.ui.Netherblade;
import com.hawolt.util.ReflectHttp;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

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
            //TODO experimental
            InternalStorage.registerStorageListener(true, "xmpptoken", xmpptoken -> {
                JSONObject object = new JSONObject(new String(Base64.getDecoder().decode(xmpptoken.split("\\.")[1])));
                String affinity = object.getString("affinity");
                InternalStorage.registerStorageListener(false, affinity, host -> {
                    Logger.info("[XMPP] relaying to {}", host);
                    SocketProxy proxy = new SocketProxy(new SocketMessageCallback() {
                        @Override
                        public byte[] onOutgoing(byte[] b) {
                            Logger.debug("XMPP-OUT {}", new String(b));
                            return b;
                        }

                        @Override
                        public byte[] onIngoing(byte[] b) {
                            Logger.error("XMPP-IN {} ", new String(b));
                            return b;
                        }
                    }, host, 5223, 5223);
                    proxy.start();
                });
            });
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
