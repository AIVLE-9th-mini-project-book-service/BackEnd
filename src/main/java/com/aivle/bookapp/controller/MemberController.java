package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.Member;
import com.aivle.bookapp.dto.LoginRequest;
import com.aivle.bookapp.dto.LoginResponse;
import com.aivle.bookapp.dto.SignupRequest;
import com.aivle.bookapp.dto.SignupResponse;
import com.aivle.bookapp.service.MemberService;
import com.aivle.bookapp.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/members/signup")
    public SignupResponse signup(
            @Valid @RequestBody SignupRequest request) {
        return memberService.signup(request);
    }

    @PostMapping("/members/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {
        return memberService.login(request);
    }

    @GetMapping("/members/me")
    public ResponseEntity<Map<String, Object>> getMe(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);

        Member member = memberService.findByEmail(email);

        Map<String, Object> body = Map.of(
                "id", member.getId(),
                "name", member.getName(),
                "email", member.getEmail()
        );
        return ResponseEntity.ok(body);
    }
}