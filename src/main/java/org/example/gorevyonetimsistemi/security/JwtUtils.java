package org.example.gorevyonetimsistemi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JSON Web Token (JWT) üretim, çözümleme ve doğrulama işlemlerinden sorumlu yardımcı sınıftır.
 * Sistemin "Dijital Darphanesi" olarak çalışır; kullanıcılar için güvenli oturum anahtarları oluşturur.
 * * @author Mustafa ERKARA
 * @since 2026-04-07
 * @version 1.0
 */
@Component
public class JwtUtils {

    /** Token imzalamak için kullanılan gizli anahtar (HS256 algoritması için en az 32 karakter olmalıdır). */
    private final String jwtSecret = "CokGizliVeCokUzunBirSifreAnahtariBurayaGelecek1234567890";

    /** Token'ın geçerlilik süresi (24 saat = 86.400.000 milisaniye). */
    private final int jwtExpirationMs = 86400000;

    /** Gizli metinden türetilen ve kriptografik işlemlerde kullanılan güvenli anahtar nesnesi. */
    private final SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    /**
     * Kullanıcı adına özel, imzalı bir erişim token'ı (JWT) üretir.
     * * @param username Token içerisine gömülecek olan kullanıcı adı
     * @return Oluşturulan ve imzalanan JWT string değeri
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Şifreli token içerisindeki veriyi (payload) çözerek kullanıcı adını geri döndürür.
     * * @param token Çözümlenecek olan JWT
     * @return Token içindeki kullanıcı adı (subject)
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Gelen token'ın doğruluğunu, imzasını ve süresinin dolup dolmadığını kontrol eder.
     * * @param token Kontrol edilecek olan JWT
     * @return Token geçerli ve güvenli ise true, aksi halde false
     */
    public boolean validateToken(String token) {
        try {
            // Token parser, imza hatası veya süre aşımı durumunda otomatik olarak Exception fırlatır.
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Token sahteyse, tahrif edildiyse veya süresi dolduysa doğrulama başarısız olur.
            return false;
        }
    }
}