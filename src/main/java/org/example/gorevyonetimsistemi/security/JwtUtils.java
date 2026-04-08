package org.example.gorevyonetimsistemi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // Buradaki key en az 32 karakter olmalı (HS256 için)
    private final String jwtSecret = "CokGizliVeCokUzunBirSifreAnahtariBurayaGelecek1234567890";
    private final int jwtExpirationMs = 86400000; // 24 saat

    private final SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    // Token Üretme (Darphane)
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    // Token'dan Kullanıcı Adını Çekme (Kimlik Kontrolü)
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Token Geçerli mi? (Mühür Kontrolü)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Token sahteyse, süresi dolduysa veya mühür bozuksa buraya düşer
            return false;
        }
    }
}
