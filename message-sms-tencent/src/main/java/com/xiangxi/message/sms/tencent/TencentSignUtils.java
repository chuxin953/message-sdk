package com.xiangxi.message.sms.tencent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 腾讯云API签名工具
 * @author 初心
 * Create by on 2025/9/21 17:36
 */
public class TencentSignUtils {
    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 生成腾讯云API签名
     *
     * @param secretKey 密钥
     * @param stringToSign 待签名字符串
     * @return 签名字符串
     */
    public static String sign(String secretKey, String stringToSign) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }


    /**
     * 生成腾讯云API请求签名
     *
     * @param secretId 密钥ID
     * @param secretKey 密钥
     * @param region 地域
     * @param service 服务名
     * @param action 操作名
     * @param payload 请求体
     * @return 完整的Authorization头
     */
    public static String generateAuthorization(String secretId, String secretKey, String region,
                                               String service, String action, String payload) {
        try {
            // 1. 创建规范请求
            String httpRequestMethod = "POST";
            String canonicalUri = "/";
            String canonicalQueryString = "";
            String canonicalHeaders = "content-type:application/json; charset=utf-8\n" +
                    "host:sms.tencentcloudapi.com\n";
            String signedHeaders = "content-type;host";
            String hashedRequestPayload = sha256(payload);

            String canonicalRequest = httpRequestMethod + "\n" +
                    canonicalUri + "\n" +
                    canonicalQueryString + "\n" +
                    canonicalHeaders + "\n" +
                    signedHeaders + "\n" +
                    hashedRequestPayload;

            // 2. 创建待签名字符串
            String algorithm = "TC3-HMAC-SHA256";
            String requestTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(requestTimestamp) * 1000));
            String credentialScope = date + "/" + region + "/" + service + "/tc3_request";
            String hashedCanonicalRequest = sha256(canonicalRequest);

            String stringToSign = algorithm + "\n" +
                    requestTimestamp + "\n" +
                    credentialScope + "\n" +
                    hashedCanonicalRequest;

            // 3. 计算签名
            byte[] secretDate = hmacSha256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
            byte[] secretService = hmacSha256(secretDate, service);
            byte[] secretSigning = hmacSha256(secretService, "tc3_request");
            String signature = bytesToHex(hmacSha256(secretSigning, stringToSign));

            // 4. 构建Authorization头
            return algorithm + " " +
                    "Credential=" + secretId + "/" + credentialScope + ", " +
                    "SignedHeaders=" + signedHeaders + ", " +
                    "Signature=" + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate authorization", e);
        }
    }

    private static String sha256(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static byte[] hmacSha256(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, HMAC_SHA256);
            mac.init(secretKeySpec);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC-SHA256 not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
