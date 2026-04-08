package org.example.gorevyonetimsistemi.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.gorevyonetimsistemi.model.LoginRequest;
import org.example.gorevyonetimsistemi.model.RegisterRequest;

/**
 * Kimlik doğrulama ve kullanıcı kayıt süreçlerini tanımlayan servis arayüzüdür.
 * Sistemin güvenlik ve kullanıcı yönetimi ile ilgili sunduğu temel yetenekleri dekale eder.
 * * @author Mustafa ERKARA
 * @since 2026-04-07
 * @version 1.0
 */
public interface AuthService {

    /**
     * Yeni bir kullanıcının sisteme kayıt edilmesi sürecini yönetir.
     * Kullanıcı bilgilerini doğrular, şifreyi güvenli hale getirir ve aktivasyon süreci başlatır.
     * * @param registerRequest Kullanıcı kayıt bilgilerini içeren veri transfer nesnesi
     * @return İşlem sonucuna dair bilgilendirme mesajı
     */
    String register(RegisterRequest registerRequest);

    /**
     * Kullanıcının e-posta adresine gönderilen token aracılığıyla hesabını onaylar.
     * Hesap onaylandığında kullanıcıyı sistemde aktif hale getirir ve oturum çerezini oluşturur.
     * * @param token    E-posta ile gönderilen benzersiz aktivasyon anahtarı
     * @param response İstemciye mühürlü çerez (JWT) göndermek için kullanılan HTTP yanıt nesnesi
     * @return Aktivasyon sonucunu belirten mesaj
     */
    String verifyAccount(String token, HttpServletResponse response);

    /**
     * Mevcut bir kullanıcının sisteme giriş yapmasını sağlar.
     * Kullanıcı adı ve şifre doğrulaması yapıldıktan sonra geçerli bir JWT çerezi üretilir.
     * * @param loginRequest Giriş yapacak kullanıcının kimlik bilgileri
     * @param response     Oturum çerezini (Cookie) istemciye iletmek için kullanılan nesne
     * @return Giriş işleminin başarılı olup olmadığını belirten mesaj
     */
    String login(LoginRequest loginRequest, HttpServletResponse response);
}