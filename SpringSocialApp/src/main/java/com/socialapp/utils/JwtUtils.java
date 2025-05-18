package com.socialapp.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component; // THÊM ANNOTATION NÀY

import java.text.ParseException;
import java.util.Date;

@Component // Đánh dấu là Spring component để có thể inject
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // SECRET nên được lưu bằng biến môi trường trong thực tế
    private static final String SECRET = "12345678901234567890123456789012"; // Phải đủ 32 bytes cho HS256
    private static final long EXPIRATION_MS = 86400000; // 1 ngày (24 * 60 * 60 * 1000 ms)

    public String generateToken(String username) {
        try {
            JWSSigner signer = new MACSigner(SECRET);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer("com.socialapp") // Tên người phát hành token (tùy chọn)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            logger.error("Không thể tạo JWT token: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi tạo JWT token", e); // Hoặc một custom exception
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet != null) {
                return claimsSet.getSubject();
            }
        } catch (ParseException e) {
            logger.error("Không thể parse JWT token để lấy username: {}", e.getMessage());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);

            if (!signedJWT.verify(verifier)) {
                logger.warn("Chữ ký JWT không hợp lệ.");
                return false;
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet == null) {
                logger.warn("JWT claims set rỗng.");
                return false;
            }

            Date expiration = claimsSet.getExpirationTime();
            if (expiration == null) {
                logger.warn("Token không có thời gian hết hạn.");
                return false; // Hoặc true tùy theo chính sách của bạn
            }

            if (expiration.before(new Date())) {
                logger.warn("Token đã hết hạn vào lúc: {}", expiration);
                return false;
            }

            return true; // Token hợp lệ
        } catch (ParseException e) {
            logger.error("Không thể parse JWT token để validate: {}", e.getMessage());
        } catch (Exception e) { // Bao gồm JOSEException từ MACVerifier
            logger.error("Lỗi khi verify JWT token: {}", e.getMessage());
        }
        return false;
    }

    // Phương thức cũ của bạn, bạn có thể chọn dùng phương thức này hoặc 2 phương thức getUsernameFromToken và validateToken ở trên
    public String validateTokenAndGetUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);

            if (signedJWT.verify(verifier)) {
                Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (expiration != null && expiration.after(new Date())) {
                    return signedJWT.getJWTClaimsSet().getSubject();
                } else {
                    logger.warn("Token đã hết hạn hoặc không có thời gian hết hạn.");
                }
            } else {
                 logger.warn("Chữ ký JWT không hợp lệ.");
            }
        } catch (ParseException e) {
            logger.error("Token malformed, không thể parse: {}", e.getMessage());
        } catch (Exception e) { // Bao gồm JOSEException
            logger.error("Lỗi khi xác thực token: {}", e.getMessage());
        }
        return null;
    }
}