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

    /** Kullanıcı bilgilerini veritabanından doğrulamak için kullanılır. */
    private final UserRepository userRepository;

    /** JWT (JSON Web Token) çözümleme ve doğrulama işlemleri için kullanılır. */
    private final JwtUtils jwtUtils;

    /**
     * Gelen isteği Controller'a gitmeden önce yakalar ve güvenlik kontrollerini gerçekleştirir.
     * * @param request  Gelen HTTP isteği
     * @param response Gönderilecek HTTP yanıtı
     * @param handler  İsteği karşılayacak olan Controller nesnesi
     * @return Denetim başarılıysa true, başarısızsa false döner (istek iptal edilir)
     * @throws Exception Kontrol sırasında oluşabilecek hatalar
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /*
         * 1. ADIM: HTTPS KONTROLÜ
         * Veri gizliliği için isteğin şifreli (Secure) kanaldan geldiğinden emin olunur.
         */
        if (!request.isSecure()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "HTTPS zorunludur!");
            return false;
        }

        String token = null;
        /*
         * 2. ADIM: ÇEREZ (COOKIE) TARAMA
         * Tarayıcı tarafından otomatik gönderilen çerezler taranarak 'jwt_token' isimli anahtar bulunur.
         */
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(c -> "jwt_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        /*
         * 3. ADIM: AKTİFLİK VE TOKEN DOĞRULAMA
         * Eğer bir token mevcutsa, bu token'ın geçerliliği ve kullanıcı hesabının aktiflik durumu kontrol edilir.
         */
        if (token != null && jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username).orElse(null);

            // Kullanıcı veritabanında mevcutsa ancak hesabı henüz aktive edilmemişse (enabled=false) erişim engellenir.
            if (user != null && !user.isEnabled()) {
                response.setStatus(403);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Hesabınız henüz aktif edilmemiş! Lütfen e-postanızı kontrol edin.");
                return false;
            }
        }

        // Token yoksa veya kullanıcı aktifse isteğin Controller'a devam etmesine izin verilir.
        return true;
    }
}