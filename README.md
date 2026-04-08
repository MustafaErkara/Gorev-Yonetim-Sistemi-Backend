# 🚀 Görev Yönetim Sistemi (Backend)

Bu proje, modern güvenlik standartları (HTTPS, JWT, Secure Cookies) ve ilişkisel veri modeli kullanılarak geliştirilmiş, kurumsal standartlarda bir **Görev Yönetim Sistemi** arka plan uygulamasıdır.

## 🛠️ Teknolojiler
- **Java 21** & **Spring Boot 3.x**
- **PostgreSQL** (Veritabanı)
- **Spring Data JPA / Hibernate** (Veri Erişimi)
- **Spring Security** (Şifreleme & Yetkilendirme)
- **JJWT (io.jsonwebtoken)** (Token Mekanizması)
- **Lombok** (Kod Sadeleştirme)

---

## 🔐 Güvenlik ve Denetim Mimarisi

### 1. HTTPS (SSL/TLS)
Uygulama, veri gizliliğini en üst düzeyde tutmak için **8443** portu üzerinden SSL sertifikası (PKCS12) ile şifreli çalışmaktadır. Güvenli olmayan kanallardan gelen istekler sistem tarafından reddedilir.

### 2. JWT & Cookie Stratejisi
Kimlik doğrulama işlemi **JSON Web Token** üzerinden yürütülür:
- **Üretim:** Kullanıcı giriş yaptığında `JwtUtils` tarafından imzalı bir token üretilir.
- **Depolama:** Token, tarayıcıya `jwt_token` ismiyle **HttpOnly** ve **Secure** bir çerez (Cookie) olarak gönderilir.
- **Koruma:** XSS saldırılarına karşı JavaScript erişimine kapalıdır ve sadece HTTPS üzerinden taşınır.

### 3. Akıllı Nöbetçi (UserActivationInterceptor)
`UserActivationInterceptor` sınıfı, her isteği Controller katmanına ulaşmadan denetler:
- Bağlantının HTTPS olup olmadığını kontrol eder.
- Çerezdeki JWT'yi doğrular ve kullanıcıyı veritabanından bulur.
- Hesabı aktive edilmemiş (`enabled=false`) kullanıcıların işlem yapmasını engeller.
- Doğrulanmış kullanıcı nesnesini `authenticatedUser` etiketiyle isteğe (request) enjekte ederek Controller katmanına güvenli veri aktarımı sağlar.

---

## 🔄 Temel İş Akışları ve Görev Yönetimi

### A. Kayıt ve Aktivasyon (Registration Flow)
1. Kullanıcı `/api/auth/register` ucuna bilgilerini gönderir.
2. Şifre **BCrypt** ile hashlenerek saklanır ve bir aktivasyon kodu üretilir.
3. Kullanıcı konsola basılan linke tıklayarak hesabını aktif eder.

### B. Görev Yönetimi (Task Module)
Sistem, kullanıcılar ve görevler arasında **Bire-Çok (One-to-Many)** ilişki kurar:
- **İzolasyon:** Her kullanıcı sadece kendi oluşturduğu görevleri görebilir ve yönetebilir.
- **Eager Loading:** Listeleme işlemlerinde kullanıcı detayları performanslı bir şekilde `EAGER` olarak yüklenir, böylece "no session" hataları önlenir.
- **Durum Takibi:** Görevler `TODO`, `IN_PROGRESS` ve `DONE` statüleri ile takip edilir.

---

## 🚦 API Uç Noktaları (Endpoints)

### Kimlik Doğrulama (Auth)
| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| POST | `/api/auth/register` | Yeni kullanıcı kaydı oluşturur. |
| GET | `/api/auth/verify` | Hesap aktivasyonunu gerçekleştirir. |
| POST | `/api/auth/login` | Giriş yapar ve JWT çerezi üretir. |

### Görev İşlemleri (Tasks)
| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| GET | `/api/tasks` | Kullanıcıya ait tüm görevleri listeler. |
| POST | `/api/tasks` | Yeni bir görev oluşturur (Otomatik ilişkilendirme). |
| PUT | `/api/tasks/{id}/status` | Görevin durumunu (status) günceller. |
| DELETE | `/api/tasks/{id}` | Belirli bir görevi sistemden siler. |

---

## 📂 Paket Yapısı

- **`entity`**: Veritabanı tabloları (User, Task, Role, VerificationToken).
- **`repository`**: JPA sorgu katmanı.
- **`service`**: İş mantığının (Business Logic) yürüdüğü `@Transactional` katman.
- **`controller`**: Dış dünyaya açılan ve güvenli veri alan API uçları.
- **`security`**: JWT üretimi ve şifreleme ayarları.
- **`config`**: Interceptor ve Spring Security yapılandırmaları.

---

## 📝 Kurulum Notları
1. PostgreSQL veritabanının ayarlı olduğundan emin olun.
2. `src/main/resources` altında `keystore.p12` dosyasının bulunduğundan emin olun.
3. Uygulamayı çalıştırdıktan sonra API testleri için **https://localhost:8443** adresini kullanın.
4. Postman testlerinde `SSL certificate verification` ayarını kapatın.

---
**Geliştirici:** Mustafa ERKARA  
**Sürüm:** 1.0.0