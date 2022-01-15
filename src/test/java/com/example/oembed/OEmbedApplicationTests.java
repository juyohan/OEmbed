package com.example.oembed;

import com.example.oembed.service.GlobalService;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.MalformedURLException;

@SpringBootTest
class OEmbedApplicationTests {

    @Autowired
    GlobalService globalService;

    @Test
    void contextLoads() throws IOException, ParseException {
        globalService.responseUrl("https://twitter.com/hellopolicy/status/867177144815804416");
    }

    @Test
    void test() throws MalformedURLException {
    }
}
