package org.example.gorevyonetimsistemi.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.model.LoginRequest;
import org.example.gorevyonetimsistemi.model.RegisterRequest;
import org.example.gorevyonetimsistemi.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * Kullanıcı kayıt, giriş ve hesap doğrulama işlemlerini yöneten REST API denetleyicisidir.
 * Bu sınıf, istemciden gelen istekleri karşılar ve ilgili iş mantığı servislerine yönlendirir.
 * * @author Mustafa ERKARA
 * @since 2026-04-07
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Kimlik doğrulama ve kullanıcı işlemlerini yürüten servis katmanı. */
    private final AuthService authService;

    /**
     * Yeni bir kullanıcı kaydı oluşturur.
     * Gelen veriler @Valid notasyonu ile yapısal olarak kontrol edilir.
     * * @param registerRequest Kullanıcı adı, e-posta ve şifre bilgilerini içeren istek nesnesi
     * @return Kayıt işleminin sonucuna dair bilgilendirme mesajı
     */
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    /**
     * E-posta üzerinden gönderilen aktivasyon linkini işleyerek kullanıcı hesabını doğrular.
     * Başarılı doğrulamada kullanıcıya bir oturum çerezi (JWT) basılır.
     * * @param token E-posta ile gönderilen benzersiz aktivasyon kodu
     * @param response HTTP yanıt nesnesi (çerez eklemek için kullanılır)
     * @return Hesap doğrulama durumuna dair mesaj
     */
    @GetMapping("/verify")
    public String verifyAccount(@RequestParam String token, jakarta.servlet.http.HttpServletResponse response) {
        return authService.verifyAccount(token, response);
    }

    /**
     * Kullanıcı adı ve şifre ile sisteme giriş yapılmasını sağlar.
     * Doğrulama başarılıysa istemciye HTTP-Only ve Secure bir JWT çerezi döner.
     * * @param loginRequest Giriş bilgilerini içeren istek nesnesi
     * @param response HTTP yanıt nesnesi (JWT çerezini taşımak için kullanılır)
     * @return Giriş işleminin sonucu (Başarılı/Hatalı)
     */
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }
}