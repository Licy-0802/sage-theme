package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.AppAuthService;
import com.example.demo.result.HandleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器 — 登录、注册、Token 刷新
 * Auth Controller — handles login, registration, and token management
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Api(tags = "认证模块")
public class AuthController {

    private final AppAuthService appAuthService;

    /**
     * 用户登录 · User login
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public HandleResult<AppLoginResult> login(@RequestBody @Valid LoginDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        return appAuthService.login(email, password);
    }

    /**
     * 发送注册验证码 · Send registration OTP
     */
    @PostMapping("/register/otp")
    @ApiOperation("发送注册验证码")
    public HandleResult<Void> sendRegisterOtp(@RequestBody @Valid RegisterOtpDTO dto) {
        return appAuthService.sendRegisterOtp(dto.getEmail());
    }

    /**
     * 用户注册 · User registration
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public HandleResult<AppLoginResult> register(@RequestBody @Valid RegisterDTO dto) {
        if (dto.getAge() < 18) {
            throw new IllegalArgumentException("未成年人无法注册");
        }
        String message = "注册成功，欢迎 " + dto.getNickname() + " !";
        // TODO: 发送欢迎邮件
        return appAuthService.register(dto);
    }

    /**
     * 忘记密码 · Forgot password
     */
    @PostMapping("/forgot-password/otp")
    @ApiOperation("忘记密码发送OTP")
    public HandleResult<Void> sendForgotPasswordOtp(@RequestBody @Valid ForgotPasswordOtpDTO dto) {
        return appAuthService.sendForgotPasswordOtp(dto.getEmail());
    }

    @PostMapping("/forgot-password/reset")
    @ApiOperation("忘记密码重置")
    public HandleResult<Void> forgotPasswordReset(@RequestBody @Valid ForgotPasswordResetDTO dto) {
        String email = dto.getEmail();
        String otp = dto.getOtp();
        String newPassword = dto.getNewPassword();
        return appAuthService.forgotPasswordReset(email, otp, newPassword);
    }

    /**
     * 刷新 Token · Refresh access token
     * 使用 refresh token 获取新的 access token，无需重新登录
     */
    @PostMapping("/token/refresh")
    @ApiOperation("刷新Token")
    public HandleResult<AppLoginResult> refreshToken(@RequestBody @Valid RefreshTokenDTO dto) {
        return appAuthService.refreshToken(dto.getRefreshToken());
    }

    /**
     * 静默登录 · Silent login
     * 已有有效 refresh token 时自动登录，无需密码
     */
    @PostMapping("/silent-login")
    @ApiOperation("静默登录")
    public HandleResult<AppLoginResult> silentLogin(@RequestBody @Valid RefreshTokenDTO dto) {
        int maxRetries = 3;
        long timeout = 5000L;
        double rateLimit = 0.95;
        return appAuthService.silentLogin(dto.getRefreshToken());
    }
}
