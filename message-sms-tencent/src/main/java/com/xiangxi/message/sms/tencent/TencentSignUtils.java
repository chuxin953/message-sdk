package com.xiangxi.message.sms.tencent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * 腾讯云API签名工具类
 * 
 * <p>提供腾讯云API请求签名的完整实现，支持TC3-HMAC-SHA256签名算法。
 * 该工具类实现了腾讯云API签名v3版本的完整流程。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>生成腾讯云API请求的Authorization头</li>
 *   <li>支持自定义时间戳的签名生成</li>
 *   <li>提供灵活的请求头和查询参数处理</li>
 *   <li>完整的SHA256和HMAC-SHA256加密支持</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * String authorization = TencentSignUtils.generateAuthorization(
 *     "your-secret-id",
 *     "your-secret-key", 
 *     "ap-guangzhou",
 *     "sms",
 *     "SendSms",
 *     requestBody
 * );
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public class TencentSignUtils {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SHA256 = "SHA-256";
    private static final String ALGORITHM = "TC3-HMAC-SHA256";

    /**
     * 私有构造函数，防止实例化
     */
    private TencentSignUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

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


    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


    /**
     * 构建规范查询字符串
     *
     * @param queryParams 查询参数
     * @return 规范查询字符串
     */
    private static String buildCanonicalQueryString(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "";
        }

        Map<String, String> sortedParams = new TreeMap<>(queryParams);
        StringBuilder queryString = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (!first) {
                queryString.append("&");
            }
            queryString.append(urlEncode(entry.getKey()))
                      .append("=")
                      .append(urlEncode(entry.getValue()));
            first = false;
        }

        return queryString.toString();
    }

    /**
     * URL编码
     *
     * @param value 待编码的值
     * @return 编码后的值
     */
    private static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    /**
     * 生成腾讯云API请求签名（使用当前时间戳）
     *
     * @param secretId 密钥ID
     * @param secretKey 密钥
     * @param service 服务名
     * @param action 操作名
     * @param payload 请求体
     * @return 完整的Authorization头
     */

    public static String generateAuthorization(String secretId, String secretKey, String host, String service, String action, String payload) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String date = formatDate(timestamp);

        // ************* 步骤 1：拼接规范请求串 *************
        String httpRequestMethod = "POST";
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n"
                + "host:" + host + "\n" + "x-tc-action:" + action.toLowerCase() + "\n";
        String signedHeaders = "content-type;host;x-tc-action";


        String hashedRequestPayload = sha256Hex(payload);
        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

        // ************* 步骤 2：拼接待签名字符串 *************
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
        String stringToSign = ALGORITHM + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        // ************* 步骤 3：计算签名 *************
        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature = DatatypeConverter.printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();

        // ************* 步骤 4：拼接 Authorization *************
        String authorization = ALGORITHM + " " + "Credential=" + secretId + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

        return authorization;
    }

    private static   byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(d).toLowerCase();
    }

    private static String formatDate(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(Long.parseLong(timestamp + "000")));
    }

}
