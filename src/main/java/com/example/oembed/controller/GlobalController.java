package com.example.oembed.controller;

import com.example.oembed.model.OEmbed;
import com.example.oembed.service.GlobalService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class GlobalController {

    private final GlobalService globalService;

    @GetMapping("/search")
    public @ResponseBody OEmbed searchUrlToOEmbed(@RequestParam("url") String url) throws IOException, ParseException {
        return globalService.responseUrl(url);
    }

}
