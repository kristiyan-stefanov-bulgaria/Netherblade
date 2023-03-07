package com.hawolt.mitm;

import com.hawolt.io.Core;
import com.hawolt.mitm.rule.IRewrite;
import com.hawolt.mitm.rule.RuleInterpreter;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created: 06/03/2023 13:32
 * Author: Twitter @hawolt
 **/

public class RuleInjector {

    public static void load(InputStream stream) throws IOException {
        String json = Core.read(stream).toString();
        JSONObject object = new JSONObject(json);
        for (String first : object.keySet()) {
            CommunicationType communicationType = CommunicationType.find(first);
            if (communicationType == CommunicationType.UNKNOWN) continue;
            RewriteModule<?> module = RuleInterpreter.map.get(communicationType);
            Map<InstructionType, List<IRewrite<?, ?>>> map = RuleInterpreter.interpret(first, object);
            for (InstructionType type : map.keySet()) {
                if (!module.map.containsKey(type)) module.map.put(type, new ArrayList<>());
                List<IRewrite<?, ?>> rules = map.get(type);
                for (IRewrite<?, ?> rule : new ArrayList<>(rules)) {
                    module.inject(type, rule);
                }
            }
        }
    }
}
