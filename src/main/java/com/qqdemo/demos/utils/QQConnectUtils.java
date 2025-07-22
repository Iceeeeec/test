package com.qqdemo.demos.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class QQConnectUtils {

    @Value("${qq.app.id}")
    private String appId;

    @Value("${qq.app.key}")
    private String appKey;

    @Value("${qq.redirect.uri}")
    private String redirectUri;

    @Value("${qq.authorize.url}")
    private String authorizeUrl;

    @Value("${qq.access.token.url}")
    private String accessTokenUrl;

    @Value("${qq.openid.url}")
    private String openidUrl;

    @Value("${qq.user.info.url}")
    private String userInfoUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取QQ授权登录地址
     */
    public String getAuthorizeUrl() {
        String state = UUID.randomUUID().toString().replace("-", "");
        StringBuilder url = new StringBuilder();
        url.append(authorizeUrl)
           .append("?response_type=code")
           .append("&client_id=").append(appId)
           .append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
           .append("&scope=get_user_info")
           .append("&state=").append(state);
        
        return url.toString();
    }

    /**
     * 通过授权码获取access_token
     */
    public String getAccessToken(String code) throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(accessTokenUrl)
           .append("?grant_type=authorization_code")
           .append("&client_id=").append(appId)
           .append("&client_secret=").append(appKey)
           .append("&code=").append(code)
           .append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));

        String response = httpGet(url.toString());
        log.info("获取access_token响应: {}", response);

        // 解析响应 access_token=xxx&expires_in=7776000&refresh_token=xxx
        Map<String, String> params = parseQueryString(response);
        String accessToken = params.get("access_token");
        
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("获取access_token失败: " + response);
        }
        
        return accessToken;
    }

    /**
     * 通过access_token获取openid
     */
    public String getOpenId(String accessToken) throws Exception {
        String url = openidUrl + "?access_token=" + accessToken;
        String response = httpGet(url);
        log.info("获取openid响应: {}", response);

        // 响应格式: callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
        String jsonStr = response.substring(response.indexOf('{'), response.lastIndexOf('}') + 1);
        JsonNode jsonNode = objectMapper.readTree(jsonStr);
        
        String openid = jsonNode.get("openid").asText();
        if (openid == null || openid.isEmpty()) {
            throw new RuntimeException("获取openid失败: " + response);
        }
        
        return openid;
    }

    /**
     * 获取QQ用户信息
     */
    public Map<String, Object> getUserInfo(String accessToken, String openid) throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(userInfoUrl)
           .append("?access_token=").append(accessToken)
           .append("&oauth_consumer_key=").append(appId)
           .append("&openid=").append(openid);

        String response = httpGet(url.toString());
        log.info("获取用户信息响应: {}", response);

        JsonNode jsonNode = objectMapper.readTree(response);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("openid", openid);
        userInfo.put("nickname", jsonNode.get("nickname").asText());
        userInfo.put("figureurl_qq_1", jsonNode.get("figureurl_qq_1").asText());
        userInfo.put("figureurl_qq_2", jsonNode.get("figureurl_qq_2").asText());
        userInfo.put("gender", jsonNode.get("gender").asText());
        userInfo.put("province", jsonNode.get("province").asText());
        userInfo.put("city", jsonNode.get("city").asText());
        
        return userInfo;
    }

    /**
     * 发送HTTP GET请求
     */
    private String httpGet(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }

    /**
     * 解析查询字符串
     */
    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
} 