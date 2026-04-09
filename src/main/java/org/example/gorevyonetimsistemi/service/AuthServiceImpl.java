package org.example.gorevyonetimsistemi.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Role;
import org.example.gorevyonetimsistemi.entity.User;
import org.example.gorevyonetimsistemi.entity.VerificationToken;
import org.example.gorevyonetimsistemi.model.LoginRequest;
import org.example.gorevyonetimsistemi.model.RegisterRequest;
import org.example.gorevyonetimsistemi.model.RoleType;
import org.example.gorevyonetimsistemi.repository.RoleRepository;
import org.example.gorevyonetimsistemi.repository.UserRepository;
import org.example.gorevyonetimsistemi.repository.VerificationTokenRepository;
import org.example.gorevyonetimsistemi.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Kimlik doğrulama, kullanıcı kayıt ve hesap aktivasyon süreçlerinin
 * iş mantığını (business logic) yürüten servis sınıfıdır.
 * * @author Mustafa ERKARA
 * @since 2026-04-07
 * @version 1.2
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Yeni bir kullanıcı kaydı oluşturur, şifreyi BCrypt ile güvenli hale getirir
     * ve bir aktivasyon token'ı üreterek konsola yazdırır.
     * * @param registerRequest Kullanıcı kayıt bilgilerini içeren nesne
     * @return Başarı mesajı
     */
    @Override
    public String register(RegisterRequest registerRequest) {
        // E-posta mükerrer kayıt kontrolü
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Hata: Bu e-posta adresi zaten kullanımda!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Hata: Başlangıç rolü bulunamadı!"));
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);


        String token = java.util.UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        System.out.println("\n--- YENİ KAYIT AKTİVASYONU ---");
        System.out.println("Aktivasyon Linki: https://localhost:8443/api/auth/verify?token=" + token);

        return "Kullanıcı başarıyla kaydedildi. Lütfen e-postanızı (konsolu) kontrol edin.";
    }

    /**
     * Aktivasyon token'ını doğrular, kullanıcıyı aktif eder ve
     * tarayıcıya HTTPS uyumlu güvenli bir çerez (cookie) basar.
     * * @param token Aktivasyon anahtarı
     * @param response Çerez eklemek için kullanılan HTTP yanıt nesnesi
     * @return Aktivasyon başarı mesajı
     */
    @Override
    @Transactional
    public String verifyAccount(String token, HttpServletResponse response) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz aktivasyon kodu!"));

        // Token süresi dolmuş mu kontrolü
        if (verificationToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Kodun süresi dolmuş.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true); // Hesabı aktif et
        userRepository.save(user);
        tokenRepository.delete(verificationToken); // Kullanılan token'ı temizle

        // HTTPS UYUMLU GÜVENLİ COOKIE (Session bilgisi için)
        jakarta.servlet.http.Cookie authCookie = new jakarta.servlet.http.Cookie("auth_user", user.getUsername());
        authCookie.setHttpOnly(true);  // XSS saldırılarına karşı koruma
        authCookie.setSecure(true);    // Sadece HTTPS üzerinden taşınır
        authCookie.setPath("/");
        authCookie.setMaxAge(7 * 24 * 60 * 60); // 7 gün
        response.addCookie(authCookie);

        return "Hesabınız başarıyla doğrulandı!";
    }

    /**
     * Kullanıcı adı ve şifre kontrolü yapar, eğer hesap aktifse
     * sistemde tanınmasını sağlayacak JWT'yi çerez olarak kullanıcıya döner.
     * * @param loginRequest Giriş bilgileri
     * @param response JWT çerezini iletmek için kullanılan nesne
     * @return Hoş geldin mesajı
     */
    @Override
    public String login(LoginRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // Hesap aktivasyon kontrolü (Interceptor ile uyumlu çalışır)
        if(!user.isEnabled()) {
            throw new RuntimeException("Lütfen önce hesabınızı e-posta ile doğrulayınız.");
        }

        // Şifre eşleşme kontrolü (BCrypt matches)
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Hatalı şifre");
        }

        // Başarılı girişte JWT üretimi
        String token = jwtUtils.generateToken(user.getUsername());

        // JWT'yi HTTPS uyumlu Secure Cookie içine yerleştirme
        jakarta.servlet.http.Cookie jwtCookie = new jakarta.servlet.http.Cookie("jwt_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // Sadece güvenli kanaldan iletim
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 saat geçerli
        response.addCookie(jwtCookie);

        return "Giriş başarılı! Hoş geldin, " + user.getUsername();
    }
}