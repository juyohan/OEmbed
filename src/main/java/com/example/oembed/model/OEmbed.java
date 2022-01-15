package com.example.oembed.model;

import lombok.Data;

@Data
public class OEmbed {
    private String title;
    private String type;
    private String version;
    private String provider_name;
    private String provider_url;
    private String author_name;
    private String author_url;
    private String is_plus;
    private String html;
    private int width;
    private int height;
    private int duration;
    private String description;
    private String thumbnail_url;
    private int thumbnail_width;
    private int thumbnail_height;
    private String thumbnail_url_with_play_button;
    private String upload_date;
    private Long video_id;
    private String uri;
}
