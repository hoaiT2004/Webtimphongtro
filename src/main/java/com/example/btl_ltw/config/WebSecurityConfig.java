package com.example.btl_ltw.config;

import com.example.btl_ltw.service.Impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Bean
    public UserDetailsService detailsService() {
        return new UserServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(detailsService());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https, HttpServletRequest request) throws Exception {
        https
                .authorizeHttpRequests()
                .requestMatchers( "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html","/api/payment/payment-return", "https://982b-103-238-68-84.ngrok-free.app/api/payment/payment-return","/api/home/*", "/api/home", "/user/sendEmailAgain","/user/register","/user/login","/user/retrievePassword", "/api/room", "/user/createNewPassword")
                .permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .requestMatchers("/admin/*").hasAuthority("Admin")
                .requestMatchers("/api/room/addStar", "/room/favorites", "/room/{roomId}/*", "/user/schedule", "/api/comment/add", "/api/room/schedule", "/api/comment/confirmEdit", "/api/comment/edit").hasAuthority("Tenant")
                .requestMatchers("/api/comment/remove").hasAnyAuthority("Tenant", "Landlord")
                .requestMatchers("/landlord/*").hasAuthority("Landlord")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login/credentials")
                .defaultSuccessUrl("/api/home")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/api/home")
                .and()
                .requestCache()
                .requestCache(requestCache());

        return https.build();
    }

    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }
}

