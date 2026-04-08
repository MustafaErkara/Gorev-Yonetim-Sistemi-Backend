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

/**
 * Kullanıcı aktivasyon durumunu ve bağlantı güvenliğini denetleyen merkezi güvenlik durdurucusudur (Interceptor).
 * Her HTTP isteği Controller katmanına ulaşmadan önce bu sınıftan geçer.
 * * @author Mustafa ERKARA
 * @since 2026-04-07
 * @version 1.1
 */
@Component
@RequiredArgsConstructor
public class UserActivationInterceptor implements HandlerInterceptor {

    /**
     * Kullanıcı bilgilerini veritabanından doğrulamak için kullanılır.
     */
    private final UserRepository userRepository;

    /**
     * JWT (JSON Web Token) çözümleme ve doğrulama işlemleri için kullanılır.
     */
    private final JwtUtils jwtUtils;

    /**
     * Gelen isteği Controller'a gitmeden önce yakalar ve güvenlik kontrollerini gerçekleştirir.
     * * @param request  Gelen HTTP isteği
     *
     * @param response Gönderilecek HTTP yanıtı
     * @param handler  İsteği karşılayacak olan Controller nesnesi
     * @return Denetim başarılıysa true, başarısızsa false döner (istek iptal edilir)
     * @throws Exception Kontrol sırasında oluşabilecek hatalar
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // LOG 1: İstek geldi mi?
        System.out.println("\n--- YENİ İSTEK YAKALANDI ---");
        System.out.println("Metot: " + request.getMethod() + " | URI: " + request.getRequestURI());

        if (!request.isSecure()) {
            System.out.println("HATA: HTTPS değil!");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "HTTPS zorunludur!");
            return false;
        }

        String token = null;
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(c -> "jwt_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            // LOG 2: Çerez bulundu mu?
            System.out.println("Token Durumu: " + (token != null ? "Token Çerezden Okundu" : "jwt_token Çerezi BULUNAMADI!"));
        } else {
            System.out.println("UYARI: İstekte hiç çerez (Cookie) yok!");
        }

        User user = null;

        if (token != null && jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            System.out.println("Token Doğrulandı. Kullanıcı: " + username);

            user = userRepository.findByUsername(username).orElse(null);

            if (user != null && !user.isEnabled()) {
                System.out.println("ENGEL: Kullanıcı hesabı pasif (enabled=false)!");
                response.setStatus(403);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Hesabınız henüz aktif edilmemiş!");
                return false;
            }
        } else if (token != null) {
            System.out.println("HATA: Token var ama GEÇERSİZ (Süresi dolmuş olabilir)!");
        }

        // LOG 3: Sonuç
        System.out.println("Attribute Atanıyor mu?: " + (user != null ? "EVET" : "HAYIR (User null!)"));
        request.setAttribute("authenticatedUser", user);

        return true;
    }
}