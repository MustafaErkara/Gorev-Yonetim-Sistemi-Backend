package org.example.gorevyonetimsistemi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Uygulamanın genel güvenlik yapılandırmasını tanımlayan sınıftır.
 * Şifreleme yöntemleri, URL bazlı yetkilendirme kuralları ve
 * temel güvenlik protokolleri (CSRF, CORS vb.) burada yapılandırılır.
 */
@Configuration
public class SecurityConfig {

    /**
     * Kullanıcı şifrelerini güvenli bir şekilde hashlemek için kullanılacak
     * şifreleme algoritmasını (BCrypt) tanımlar.
     * BCrypt, her şifreleme işleminde rastgele bir "salt" (tuz) kullanarak
     * şifre güvenliğini en üst düzeye çıkarır.
     * * @return BCryptPasswordEncoder örneği
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP isteklerinin güvenliğini yöneten ana filtre zincirini yapılandırır.
     * Hangi URL'lerin herkese açık, hangilerinin giriş yapılmış olması
     * gerektiğini belirleyen kuralları içerir.
     * * @param http Spring Security'nin ana yapılandırma nesnesi
     * @return Yapılandırılmış SecurityFilterChain nesnesi
     * @throws Exception Yapılandırma sırasında oluşabilecek hatalar
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF korumasını devre dışı bırakır (REST API projelerinde genellikle kapalı tutulur)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS (Cross-Origin Resource Sharing) ayarlarını varsayılan olarak devre dışı bırakır
                .cors(AbstractHttpConfigurer::disable)

                // İstek bazlı yetkilendirme kuralları
                .authorizeHttpRequests(auth -> auth
                        // '/api/auth/' ile başlayan tüm isteklere (login, register vb.) herkese izin ver
                        .requestMatchers("/api/auth/**").permitAll()

                        // Diğer tüm isteklere erişim için kullanıcı girişi (authentication) şarttır
                        .anyRequest().authenticated()
                )

                // Güvenlik başlıklarını yapılandırır (Örn: H2-Console kullanımı için FrameOptions kapatıldı)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}