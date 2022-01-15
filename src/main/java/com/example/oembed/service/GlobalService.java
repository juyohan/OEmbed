package com.example.oembed.service;

import com.example.oembed.model.OEmbed;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalService {

    /**
     * url 의 host 의 값만 가져오고 www. 로 시작한다면 제거해줍니다.
     * www. 로 시작 안하는 url 의 값이 있을 수 있기 때문입니다.
     * @param url
     * @return www. 로 시작한다면, 제거한 정보를 반환합니다.
     *         아니라면, 그대로 반환합니다.
     * @throws MalformedURLException
     */
    private String getUrlHost(String url) throws MalformedURLException {
        String host = new URL(url).getHost();
        return host.startsWith("www.") ? host.substring(4) : host;
    }

    /**
     * provider.json 의 정보를 가져옵니다.
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private JSONArray getProviderJson() throws IOException, ParseException {
        ClassPathResource resource = new ClassPathResource("static/json/providers.json");
        return (JSONArray) new JSONParser().parse(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * provider.json 의 파일을 읽어와 provider_url 의 값을 추출한 뒤, 입력받은 url 과 비교하여 포함된 객체를 반환합니다.
     * 반환된 객체에서 oembed 에 접근할 때, 필요한 url 을 알맞게 생성합니다.
     * 생성된 url 을 통해 접근하여 매핑 된 OEmbed 객체를 반환합니다.
     * @param url
     * @return OEmbed 객체를 반환합니다.
     * @throws IOException
     * @throws ParseException
     */
    public OEmbed responseUrl(String url) throws IOException, ParseException {
        JSONArray array = getProviderJson();

        List<JSONObject> objects = (List<JSONObject>) array.stream()
                .filter(json -> {
                    JSONObject object = (JSONObject) json;
                    String providerUrl = object.get("provider_url").toString();
                    try {
                        return getUrlHost(url).contains(getUrlHost(providerUrl));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.toList());

        String embedUrl = createUrl(url, getEmbedUrl(objects));
        return getEmbedData(embedUrl);
    }

    /**
     * endpoints 에서 oembed 에 접근하기 위한 url 의 값만 추출합니다.
     * @param jsonObjects
     * @return 마지막의 format 에 대한 정보를 입력해야하는 url 이라면, 해당 값을 자르고 json 을 붙여주고 반환합니다.
     *         아니라면, 그대로 반환합니다.
     */
    private String getEmbedUrl(List<JSONObject> jsonObjects) {
        String embedUrl = jsonObjects.stream()
                .map(jsonObject -> {
                    String endpoints = jsonObject.get("endpoints").toString();
                    Object parse = null;
                    try {
                        parse = new JSONParser().parse(endpoints);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONObject object = (JSONObject) ((JSONArray) parse).get(0);
                    return object.get("url").toString();
                })
                .findFirst()
                .orElse(null);

        return embedUrl.endsWith("{format}") ? embedUrl.substring(0, embedUrl.lastIndexOf("{")) + "json" : embedUrl;
    }

    /**
     * @param url
     * @param embedUrl
     * @return
     */
    private String createUrl(String url, String embedUrl) {
        return embedUrl + "?url=" + url;
    }

    /**
     * RestTemplate 를 통해 url 에 접근하여 데이터를 OEmbed.class 에 매핑시켜 가져옵니다.
     * @param url
     * @return 매핑된 객체를 반환합니다.
     */
    private OEmbed getEmbedData(String url) {
        RestTemplate restTemplate = new RestTemplate();
        OEmbed oEmbed = restTemplate.getForObject(url, OEmbed.class);
        return oEmbed;
    }
}
