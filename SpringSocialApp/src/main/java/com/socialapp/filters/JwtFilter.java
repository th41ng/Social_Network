package com.socialapp.filters;

import com.socialapp.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections; // QUAN TRỌNG: Import Collections
import org.slf4j.Logger;      // QUAN TRỌNG: Import Logger
import org.slf4j.LoggerFactory; // QUAN TRỌNG: Import LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails; // Cần nếu bạn muốn load UserDetails
// import org.springframework.security.core.userdetails.UserDetailsService; // Cần nếu inject UserDetailsService

public class JwtFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class); // Thêm logger

    // Nếu bạn muốn load UserDetails để có authorities, bạn cần inject UserDetailsService
    // @Autowired
    // private UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // HttpServletResponse httpResponse = (HttpServletResponse) response; // Không cần nếu không sendError trực tiếp

        String header = httpRequest.getHeader("Authorization");
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Bỏ qua xử lý token cho OPTIONS requests (pre-flight CORS)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Log mỗi khi filter được gọi để debug
        logger.debug("JwtFilter processing request: {} {} with Authorization header: {}",
                httpRequest.getMethod(), requestPath, (header != null && header.startsWith("Bearer ")));

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = JwtUtils.validateTokenAndGetUsername(token);
                // Chỉ thiết lập Authentication nếu username hợp lệ VÀ chưa có Authentication trong context
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Nếu bạn có UserDetailsService và muốn load chi tiết user (bao gồm roles/authorities):
                    // UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    // UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Phiên bản đơn giản không load UserDetails (chỉ có username, không có authorities cụ thể từ DB)
                    // QUAN TRỌNG: Sử dụng Collections.emptyList() cho authorities
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Successfully authenticated user '{}' from token for request: {}", username, requestPath);
                } else if (username == null) {
                    logger.warn("Token validation failed (username is null) for request: {}. Token: {}", requestPath, token);
                } else {
                    // Đã có Authentication trong context, không cần làm gì thêm
                    logger.debug("Authentication already exists in SecurityContext for user '{}', request: {}",
                            SecurityContextHolder.getContext().getAuthentication().getName(), requestPath);
                }
            } catch (Exception e) {
                logger.warn("Invalid JWT token for request {}: {}. Error: {}", requestPath, token, e.getMessage());
                // Không cần gửi lỗi từ filter, Spring Security sẽ xử lý nếu endpoint yêu cầu xác thực.
                // Xóa context nếu token không hợp lệ để đảm bảo không dùng lại context cũ.
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("No Authorization Bearer token found for request: {}", requestPath);
        }

        chain.doFilter(request, response);
    }
}