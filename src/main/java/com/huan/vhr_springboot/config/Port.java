package com.huan.vhr_springboot.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Setter
@Component
@ConfigurationProperties(prefix = "server")
public class Port {
    private static final String host = "localhost:";
    private Integer port;

    public String getPort() {
        return host + port;
    }
}
