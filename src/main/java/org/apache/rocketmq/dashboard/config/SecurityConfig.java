/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.dashboard.config;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Resource
    private CorsConfigure cors;

    private final List<String> DEFAULT_ALLOWED_METHODS = Lists.newArrayList("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private final List<String> DEFAULT_ALLOWED_HEADERS = Lists.newArrayList("content-type", "Authorization", "X-Requested-With", "Origin", "Accept", "X-XSRF-TOKEN");
    private final List<String> DEFAULT_ALLOWED_ORIGINS = Lists.newArrayList("http://localhost:3002");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/actuator/**")
                        .ignoringRequestMatchers("/rocketmq-dashboard/csrf-token")
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        cors.getAllowedOrigins().addAll(DEFAULT_ALLOWED_METHODS);
        configuration.setAllowedOrigins(distinctList(cors.getAllowedOrigins(), DEFAULT_ALLOWED_ORIGINS));
        configuration.setAllowedMethods(distinctList(cors.getAllowedMethods(), DEFAULT_ALLOWED_METHODS));
        configuration.setAllowedHeaders(distinctList(cors.getAllowedHeaders(), DEFAULT_ALLOWED_HEADERS));
        configuration.setAllowCredentials(cors.getAllowCredentials());
        configuration.setMaxAge(cors.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    private List<String> distinctList(List<String> first, List<String> second) {
        Set<String> set = new HashSet<>();
        set.addAll(first);
        set.addAll(second);
        return new ArrayList<>(set);
    }
}
