package org.example.gorevyonetimsistemi.config;

import lombok.RequiredArgsConstructor;
import org.example.gorevyonetimsistemi.entity.Role;
import org.example.gorevyonetimsistemi.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Uygulama ayağa kalktığında çalışacak başlangıç verilerini yapılandıran sınıftır.
 * CommandLineRunner arayüzü sayesinde Spring Boot uygulamasının başlatılması
 * tamamlandığında run metodu otomatik olarak tetiklenir.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    /**
     * Rol işlemlerini veritabanı seviyesinde gerçekleştirmek için kullanılan depo (repository).
     */
    private final RoleRepository roleRepository;

    /**
     * Uygulama başlangıcında otomatik olarak çalıştırılan metot.
     * Bu metot, sistemin düzgün çalışması için gerekli olan temel rolleri
     * veritabanında kontrol eder ve eksikse oluşturur.
     * * @param args Komut satırı argümanları (varsa)
     */
    @Override
    public void run(String... args) {
        /*
         * Eğer veritabanında 'ROLE_USER' adında bir kayıt bulunamazsa,
         * sisteme ilk defa dahil olan kullanıcılara atanacak olan varsayılan rolü oluşturur.
         */
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }
    }
}