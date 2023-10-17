package com.farneser.cloudfilestorage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Configuration
@EnableRedisHttpSession
public class SessionConfig extends RedisHttpSessionConfiguration {
}
