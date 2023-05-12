//package com.ekov.workBenchDB.security;
//
//import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//public class WebConfig {
//    @ConditionalOnDefaultWebSecurity
//    static class SecurityFilterChainConfiguration {
//        SecurityFilterChainConfiguration() {
//        }
//
//        @Bean
//        @Order(2147483642)
//        SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
//                throws Exception {
//            (http.authorizeRequests().anyRequest()).authenticated();
//            http.formLogin();
//            http.httpBasic();
//            return (SecurityFilterChain) http.build();
//        }
//    }
//}
