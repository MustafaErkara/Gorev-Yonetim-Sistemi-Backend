package org.example.gorevyonetimsistemi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC yapılandırmasını yöneten sınıftır.
 * Yazdığımız Interceptor'ların hangi URL'lerde aktif olacağını burada belirtiyoruz.
 * * @author Mustafa ERKARA
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserActivationInterceptor userActivationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Bekçiyi (Interceptor) tüm /api/ altındaki isteklere ata,
        // ama giriş ve kayıt sayfalarında bir şey sormasına gerek yok.
        registry.addInterceptor(userActivationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}