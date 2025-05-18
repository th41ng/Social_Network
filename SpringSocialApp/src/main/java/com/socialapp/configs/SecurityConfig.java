package com.socialapp.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialapp.filters.JwtFilter; // Import JwtFilter đã sửa
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays; // Import Arrays
import java.util.List;   // Import List

@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true) // Cho phép sử dụng @PreAuthorize, @PostAuthorize
@ComponentScan(basePackages = {
    "com.socialapp.controllers",
    "com.socialapp.repository",
    "com.socialapp.service",
    "com.socialapp.filters", // Thêm package của JwtFilter
    "com.socialapp.utils" // Thêm package của JwtUtils (nếu nó là @Component)
})
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService; // UserService của bạn implement UserDetailsService

    @Autowired
    private JwtFilter jwtFilter; // Inject JwtFilter đã là @Component

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(c -> c.disable()).authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/login", "/api/user").permitAll() // Cho phép những đường dẫn này không cần chứng thực
                //                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll() // <--- ADD THIS LINE
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll() // <
                .requestMatchers(HttpMethod.PATCH, "/api/verify/**").permitAll()
                .requestMatchers("/js/**", "/css/**", "/images/**", "/assets/**").permitAll() // Các tài nguyên tĩnh như JS, CSS, ảnh, v.v.
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
        )
                .formLogin(form -> form.loginPage("/Users/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/Users/login?error=true").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/Users/login").permitAll());//.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dxxwcby8l",
                "api_key", "448651448423589",
                "api_secret", "ftGud0r1TTqp0CGp5tjwNmkAm-A",
                "secure", true));
    }

    @Bean
    @Order(0) // Đảm bảo multipartResolver được ưu tiên
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend của bạn
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true); // Quan trọng nếu bạn gửi cookie hoặc Authorization header
        configuration.setMaxAge(3600L); // Thời gian pre-flight request được cache (giây)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả các đường dẫn
        return source;
    }
}
