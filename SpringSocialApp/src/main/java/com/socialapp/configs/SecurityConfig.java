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
    "com.socialapp.utils"    // Thêm package của JwtUtils (nếu nó là @Component)
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Tắt CSRF vì dùng token-based auth
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API là stateless
            .authorizeHttpRequests(authorize -> authorize
                // Các API công khai không cần xác thực
                .requestMatchers("/api/login", "/api/user", "/api/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/verify/**").permitAll()
                // Các tài nguyên tĩnh công khai
                .requestMatchers("/js/**", "/css/**", "/images/**", "/assets/**", "/static/**", "/public/**").permitAll()
                .requestMatchers("/", "/index.html", "/manifest.json", "/favicon.ico", "/*.png", "/*.jpg").permitAll() // Cho React frontend
                // Các endpoint khác của Swagger/OpenAPI nếu có
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Tất cả các request API còn lại ("/api/**") đều yêu cầu xác thực
                .requestMatchers("/api/**").authenticated()
                
                // Các request khác không phải API (nếu có, ví dụ cho trang admin MVC) có thể yêu cầu xác thực khác
                .anyRequest().permitAll() // HOẶC .anyRequest().authenticated() tùy theo bạn muốn bảo vệ các trang MVC không
                                         // Nếu chỉ làm API backend cho React thì có thể không cần phần formLogin và logout dưới đây
            );
            // Bỏ formLogin nếu bạn chỉ làm API backend và không có trang login MVC truyền thống
            // .formLogin(form -> form
            //     .loginPage("/Users/login") 
            //     .loginProcessingUrl("/login")
            //     .defaultSuccessUrl("/", true)
            //     .failureUrl("/Users/login?error=true").permitAll()
            // )
            // .logout(logout -> logout.logoutSuccessUrl("/Users/login").permitAll());

        // Thêm JwtFilter vào trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

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