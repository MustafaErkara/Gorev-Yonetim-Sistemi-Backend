package org.example.gorevyonetimsistemi.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Role;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.entity.VerificationToken;
import org.example.gorevyonetimsistemi.model.LoginRequest;
import org.example.gorevyonetimsistemi.model.RegisterRequest;
import org.example.gorevyonetimsistemi.repository.RoleRepository;
import org.example.gorevyonetimsistemi.repository.UserRepository;
import org.example.gorevyonetimsistemi.repository.VerificationTokenRepository;
import org.example.gorevyonetimsistemi.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Override
    public String register(RegisterRequest registerRequest) {

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Hata: Bu e-posta adresi zaten kullanımda!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Hata: Başlangıç rolü bulunamadı!"));
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);

        String token = java.util.UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
        System.out.println();
        System.out.println("Aktivasyon Linki: http://localhost:8080/api/auth/verify?token=" + token);

        return "Kullanıcı başarıyla kaydedildi. Lütfen konsoldaki linke tıklayarak hesabınızı doğrulayın.";

    }

    @Override
    @Transactional
    public String verifyAccount(String token, jakarta.servlet.http.HttpServletResponse response) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz aktivasyon kodu!"));

        if (verificationToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        // --- HTTPS UYUMLU GÜVENLİ COOKIE OLUŞTURMA ---
        jakarta.servlet.http.Cookie authCookie = new jakarta.servlet.http.Cookie("auth_user", user.getUsername());
        authCookie.setHttpOnly(true);  // JavaScript erişemez (XSS koruması)
        authCookie.setSecure(true);    // Sadece HTTPS üzerinden gönderilir
        authCookie.setPath("/");       // Tüm uygulama için geçerli
        authCookie.setMaxAge(7 * 24 * 60 * 60); // 7 gün geçerli

        response.addCookie(authCookie);
        // --------------------------------------------

        return "Hesabınız doğrulandı ve giriş yapıldı!";
    }

    @Override
    public String login(LoginRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if(!user.isEnabled()) {
            throw new RuntimeException("Lütfen önce hesabını e-posta ile doğrulayınız.");
        }

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Hatalı şifre");
        }

        String token = jwtUtils.generateToken(user.getUsername());

        // 5. JWT'yi HTTPS uyumlu Secure Cookie içine koy
        jakarta.servlet.http.Cookie jwtCookie = new jakarta.servlet.http.Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // HTTPS şartı
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 saat
        response.addCookie(jwtCookie);

        return "Giriş başarılı! Hoş geldin, " + user.getUsername();
    }
}
