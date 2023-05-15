//package com.ekov.workBenchDB.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
//import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
//import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
//import org.springframework.session.security.SpringSessionBackedSessionRegistry;
//
//@EnableWebSecurity
//@EnableJdbcHttpSession
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//
//    private final UserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationFailureHandler securityErrorHandler;
//    private final ConcurrentSessionStrategy concurrentSessionStrategy;
//    private final SessionRegistry sessionRegistry;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors().and()
//                //для защиты о csrf атак
//                .csrf().and()
//                .httpBasic().and()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated().and()
//                //Логаут
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
//                //Возвращаем при логауте 200(по умолчанию возвращается 203)
//                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
//                //Инвалидируем сессию при логауте
//                .invalidateHttpSession(true)
//                .clearAuthentication(false)
//                //Удаляем всю информацию с фронта при логауте(т.е. чистим куки, хидеры и т.д.)
//                .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.ALL)))
//                .permitAll().and()
//                //Включаем менеджер сессий(для контроля количества сессий)
//                .sessionManagement()
//                //Указываем макимальное возможное количество сессий(тут указано не 1, т.к. мы будем пользоваться своей кастомной стратегией, объяснение будет ниже)
//                .maximumSessions(3)
//                //При превышение количества активных сессий(3) выбрасывается исключение  SessionAuthenticationException
//                .maxSessionsPreventsLogin(true)
//                //Указываем как будут регестрироваться наши сессии(тогда во всем приложение будем использовать именно этот бин)
//                .sessionRegistry(sessionRegistry).and()
//                //Добавляем нашу кастомную стратегию для проверки кличества сессий
//                .sessionAuthenticationStrategy(concurrentSessionStrategy)
//                //Добавляем перехватчик для исключений
//                .sessionAuthenticationFailureHandler(securityErrorHandler);
//    }
//
//    //для инвалидации сессий при логауте
//    @Bean
//    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
//        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
//    }
//
//    @Bean
//    public static SessionRegistry sessionRegistry(JdbcIndexedSessionRepository sessionRepository) {
//        return new SpringSessionBackedSessionRegistry(sessionRepository);
//    }
//
//    @Bean
//    public static PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }
//
//}