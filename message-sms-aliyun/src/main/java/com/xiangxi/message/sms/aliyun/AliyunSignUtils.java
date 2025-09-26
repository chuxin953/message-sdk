package com.xiangxi.message.sms.aliyun;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** 阿里云 OpenAPI（RPC 2017-05-25）签名工具 - HMAC-SHA1 */
final class AliyunSignUtils {
    private static final DateTimeFormatter ISO8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter BASIC_ISO8601 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
            .withZone(ZoneOffset.UTC);

    static Map<String, String> buildCommonParams(String accessKeyId) {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("Format", "JSON");
        p.put("SignatureMethod", "HMAC-SHA1");
        p.put("SignatureVersion", "1.0");
        p.put("SignatureNonce", nonce());
        p.put("Timestamp", ISO8601.format(Instant.now()));
        p.put("AccessKeyId", accessKeyId);
        return p;
    }

    static String sign(String httpMethod, Map<String, String> params, String accessKeySecret) throws Exception {
        String canonicalized = canonicalize(params);
        String stringToSign = httpMethod.toUpperCase(Locale.ROOT) + "&" + percentEncode("/") + "&" + percentEncode(canonicalized);
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec((accessKeySecret + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    private static String canonicalize(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            String v = params.get(k);
            sb.append(percentEncode(k)).append("=").append(percentEncode(v));
            if (i < keys.size() - 1) sb.append("&");
        }
        return sb.toString();
    }

    static String percentEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (Exception e) { return s; }
    }

    private static String nonce() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // ================= ACS V3（ROA风格）签名 =================
    static String nowBasicUtc() { return BASIC_ISO8601.format(Instant.now()); }

    static String sha256Hex(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    static String canonicalRequest(String method, String uri, String query, SortedMap<String,String> headers, String payloadSha256) {
        StringBuilder canonicalHeaders = new StringBuilder();
        List<String> signed = new ArrayList<>();
        headers.forEach((k,v)->{ canonicalHeaders.append(k.toLowerCase(Locale.ROOT)).append(":").append(v.trim()).append("\n"); signed.add(k.toLowerCase(Locale.ROOT));});
        String signedHeaders = String.join(";", signed);
        return method.toUpperCase(Locale.ROOT) + "\n" + uri + "\n" + (query==null?"":query) + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + payloadSha256;
    }

    static String stringToSign(String canonicalRequest) throws Exception {
        String hash = sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        return "ACS3-HMAC-SHA256\n" + hash;
    }

    static String signV3(String stringToSign, String accessKeySecret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(accessKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : signData) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    static Map<String,String> buildV3Headers(String accessKeyId, String accessKeySecret, String method, String host, String uri, String query, byte[] body, Map<String,String> extra) throws Exception {
        String bodySha256 = sha256Hex(body==null? new byte[0]: body);
        SortedMap<String,String> headers = new TreeMap<>();
        headers.put("host", host);
        headers.put("content-type", "application/json; charset=utf-8");
        headers.put("x-acs-date", nowBasicUtc());
        headers.put("x-acs-content-sha256", bodySha256);
        if (extra != null) extra.forEach((k,v)-> headers.put(k.toLowerCase(Locale.ROOT), v));
        String canonical = canonicalRequest(method, uri, query, headers, bodySha256);
        String sts = stringToSign(canonical);
        String signature = signV3(sts, accessKeySecret);
        String signedHeaders = String.join(";", headers.keySet());
        String auth = "ACS3-HMAC-SHA256 Credential=" + accessKeyId + ",SignedHeaders=" + signedHeaders + ",Signature=" + signature;
        Map<String,String> finalHeaders = new LinkedHashMap<>();
        headers.forEach(finalHeaders::put);
        finalHeaders.put("authorization", auth);
        return finalHeaders;
    }
}


