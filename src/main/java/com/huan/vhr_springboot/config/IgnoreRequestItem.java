package com.huan.vhr_springboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "ignore")
public class IgnoreRequestItem {
    private List<String> get = new ArrayList<>();
    private List<String> post = new ArrayList<>();
}
