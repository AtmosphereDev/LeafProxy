package dev.vinkyv.leafproxy.utils;

import lombok.Value;

import java.util.UUID;

@Value
public class AuthData {
    private final String displayName;
    private final UUID identity;
    private final String xuid;
}