package dev.vinkyv.leafproxy.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ChainData {
    private final Gson GSON = new Gson();
    public JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        String json = new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8);
        //Server.getInstance().getLogger().debug(json);
        return GSON.fromJson(json, JsonObject.class);
    }
}
