package com.hawolt.mitm.rule;

import com.hawolt.http.proxy.ProxyRequest;
import com.hawolt.http.proxy.ProxyResponse;
import com.hawolt.io.Core;
import com.hawolt.logger.Logger;
import com.hawolt.mitm.CommunicationType;
import com.hawolt.mitm.InstructionType;
import com.hawolt.mitm.RewriteModule;
import com.hawolt.mitm.impl.RequestModule;
import com.hawolt.mitm.impl.ResponseModule;
import io.javalin.http.Handler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created: 22/11/2022 04:33
 * Author: Twitter @hawolt
 **/

public class RuleInterpreter {

    public static final Handler RELOAD = context -> {
        reload(context.queryParam("file"));
    };

    public static void reload(String file) throws IOException {
        String filename = file == null ? "instructions.json" : file;
        JSONObject object = new JSONObject(Core.read(Core.getFileAsStream(Paths.get(filename))).toString());
        interpret(object);
    }

    public static Map<CommunicationType, RewriteModule<?>> map = new HashMap<CommunicationType, RewriteModule<?>>() {{
        put(CommunicationType.INGOING, new ResponseModule());
        put(CommunicationType.OUTGOING, new RequestModule());
    }};

    public static void interpret(JSONObject object) {
        for (String first : object.keySet()) {
            Map<InstructionType, List<RewriteRule>> map = new HashMap<>();
            CommunicationType communicationType = CommunicationType.find(first);
            if (communicationType == CommunicationType.UNKNOWN) continue;
            JSONObject communicationTypeJson = object.getJSONObject(first);
            for (String second : communicationTypeJson.keySet()) {
                InstructionType instructionType = InstructionType.find(second);
                if (instructionType == InstructionType.UNKNOWN) continue;
                if (!map.containsKey(instructionType)) map.put(instructionType, new ArrayList<>());
                JSONArray rules = communicationTypeJson.getJSONArray(second);
                for (int i = 0; i < rules.length(); i++) {
                    map.get(instructionType).add(new RewriteRule(rules.getJSONObject(i)));
                }
            }
            RuleInterpreter.map.get(communicationType).supply(map);
        }
    }
}
