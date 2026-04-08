package org.example.gorevyonetimsistemi.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.repository.UserRepository;
import org.example.gorevyonetimsistemi.security.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class UserActivationInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. HTTPS Kontrolü (Zaten yapmıştık)
        if (!request.isSecure()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "HTTPS zorunludur!");
            return false;
        }

        String token = null;
        // 2. Cookie'den JWT_TOKEN'ı bul
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(c -> "jwt_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        // 3. Token varsa doğruluğunu ve kullanıcı durumunu kontrol et
        if (token != null && jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null && !user.isEnabled()) {
                response.setStatus(403);
                response.getWriter().write("Hesabiniz henüz aktif edilmemis!");
                return false;
            }
        }
        // Not: Token yoksa veya geçersizse burada engellemiyoruz,
        // çünkü bazı sayfalar (login/register) tokensız da açılmalı.
        // Sadece "aktiflik" kontrolü yapıyoruz.

        return true;
    }
}
