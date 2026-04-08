package org.example.gorevyonetimsistemi.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.gorevyonetimsistemi.model.LoginRequest;
import org.example.gorevyonetimsistemi.model.RegisterRequest;

public interface AuthService {
    // Kayıt işlemi (Burada Cookie'ye gerek yok, sadece kayıt yapıyoruz)
    String register(RegisterRequest registerRequest);

    // Hesap doğrulama (Burada kullanıcıyı aktif edip tarayıcısına mühürlü Cookie'yi basacağız)
    String verifyAccount(String token, HttpServletResponse response);

    String login(LoginRequest loginRequest, HttpServletResponse response);
}
