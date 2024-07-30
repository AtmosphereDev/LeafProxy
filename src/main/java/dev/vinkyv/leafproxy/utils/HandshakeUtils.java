package dev.vinkyv.leafproxy.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.cloudburstmc.protocol.bedrock.BedrockSession;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.UUID;

public class HandshakeUtils {
	public static SignedJWT createExtraData(KeyPair key, JsonObject extraData) {
		String publicKeyBase64 = Base64.getEncoder().encodeToString(key.getPublic().getEncoded());
		long timestamp = System.currentTimeMillis() / 1000;

		JsonObject dataChain = new JsonObject();
		dataChain.addProperty("nbf", timestamp - 3600);
		dataChain.addProperty("exp", timestamp + 24 * 3600);
		dataChain.addProperty("iat", timestamp);
		dataChain.addProperty("iss", "self");
		dataChain.addProperty("certificateAuthority", true);
		dataChain.add("extraData", extraData);
		dataChain.addProperty("randomNonce", UUID.randomUUID().getLeastSignificantBits());
		dataChain.addProperty("identityPublicKey", publicKeyBase64);
		return encodeJWT(key, dataChain);
	}

	public static SignedJWT encodeJWT(KeyPair key, JsonObject payload) {
		String publicKeyBase64 = Base64.getEncoder().encodeToString(key.getPublic().getEncoded());
		URI x5u = URI.create(publicKeyBase64);
		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES384).x509CertURL(x5u).build();
		try {
			SignedJWT jwt = new SignedJWT(header, JWTClaimsSet.parse(payload.toString()));
			signJwt(jwt, (ECPrivateKey) key.getPrivate());
			return jwt;
		} catch (JOSEException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static void signJwt(JWSObject jws, ECPrivateKey key) throws JOSEException {
		jws.sign(new ECDSASigner(key, Curve.P_384));
	}

	public static JsonObject parseClientData(JWSObject clientJwt, JsonObject extraData, BedrockSession session) throws Exception {
		JsonObject clientData = (JsonObject) JsonParser.parseString(clientJwt.getPayload().toString());
		// TODO: Idk if i need it
		if (false) {
			// Add waterdog attributes
			clientData.addProperty("Waterdog_XUID", extraData.get("XUID").getAsString());
			clientData.addProperty("Waterdog_IP", ((InetSocketAddress) session.getSocketAddress()).getAddress().getHostAddress());
		}
		return clientData;
	}

	public static JsonObject parseExtraData(LoginPacket packet, JsonObject payload) {
		JsonElement extraDataElement = payload.get("extraData");
		if (!extraDataElement.isJsonObject()) {
			throw new IllegalStateException("Invalid 'extraData'");
		}

		JsonObject extraData = extraDataElement.getAsJsonObject();
		// TODO: Idk if i need it
		if (false) {
			String playerName = extraData.get("displayName").getAsString();
			extraData.addProperty("displayName", playerName.replaceAll(" ", "_"));
		}
		return extraData;
	}
}
