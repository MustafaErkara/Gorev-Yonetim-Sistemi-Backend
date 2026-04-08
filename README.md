# 🚀 Görev Yönetim Sistemi (Backend)

Bu proje, modern güvenlik standartları (HTTPS, JWT, Secure Cookies) kullanılarak geliştirilmiş bir **Görev Yönetim Sistemi** arka plan uygulamasıdır.

## 🛠️ Teknolojiler
- **Java 21** & **Spring Boot 4.0.5**
- **PostgreSQL** (Veritabanı)
- **Spring Security** (Şifreleme & Güvenlik)
- **JJWT (io.jsonwebtoken)** (Token Mekanizması)
- **Lombok** (Kod Sadeleştirme)

---

## 🔐 Güvenlik Mimarisi

### 1. HTTPS (SSL/TLS)
Uygulama, veri güvenliğini sağlamak için **8443** portu üzerinden SSL sertifikası ile çalışmaktadır.
- Sertifika Tipi: PKCS12
- Şifreleme: RSA (2048-bit)

### 2. JWT & Cookie Stratejisi
Kimlik doğrulama işlemi **JSON Web Token** üzerinden yürütülür:
- **Üretim:** Kullanıcı giriş yaptığında `JwtUtils` sınıfı tarafından imzalı bir token üretilir.
- **Depolama:** Token, tarayıcıya `jwt_token` ismiyle **HttpOnly** ve **Secure** bir çerez (Cookie) olarak gönderilir.
- **Güvenlik:** JavaScript üzerinden erişilemez (XSS koruması) ve sadece HTTPS üzerinden taşınır.

### 3. Nöbetçi (Interceptor) Mekanizması
`UserActivationInterceptor` sınıfı, her isteği Controller'a ulaşmadan denetler:
- Bağlantının HTTPS olup olmadığını kontrol eder.
- Çerezdeki JWT'yi doğrular.
- Kullanıcının mail aktivasyonunu yapıp yapmadığını (`enabled`) veritabanından teyit eder.

---

## 🔄 Temel İş Akışları

### A. Kayıt ve Aktivasyon (Registration Flow)
1. Kullanıcı `/api/auth/register` ucuna bilgilerini gönderir.
2. Şifre **BCrypt** ile hashlenerek kaydedilir ve bir aktivasyon kodu üretilir.
3. Kullanıcı konsola basılan linke tıklayarak `/api/auth/verify` üzerinden hesabını aktif eder.

### B. Giriş ve Yetkilendirme (Login Flow)
1. Kullanıcı `/api/auth/login` ucuna kullanıcı adı ve şifresini gönderir.
2. Şifre doğruysa, sistem bir **JWT** üretir ve bunu kullanıcının tarayıcısına çerez olarak enjekte eder.
3. Sonraki tüm isteklerde bu çerez otomatik olarak gönderilir ve Interceptor tarafından doğrulanır.

---

## 📂 Proje Yapısı (Paketler)

- **`entity`**: Veritabanı tabloları (User, VerificationToken).
- **`repository`**: Veritabanı erişim katmanı.
- **`service`**: İş mantığının (Business Logic) yürüdüğü mutfak.
- **`controller`**: Dış dünyaya açılan API uçları.
- **`security`**: JWT üretimi ve şifreleme ayarları.
- **`config`**: Interceptor ve CORS gibi sistem yapılandırmaları.
- **`model`**: Veri transfer nesneleri (DTO).

---

## 📝 Kurulum Notları
1. PostgreSQL veritabanının ayarlı olduğundan emin olun.
2. `src/main/resources` altında `keystore.p12` dosyasının bulunduğundan emin olun.
3. Uygulamayı çalıştırdıktan sonra API testleri için **https://localhost:8443** adresini kullanın.
4. Postman testlerinde `SSL certificate verification` ayarını kapatın.