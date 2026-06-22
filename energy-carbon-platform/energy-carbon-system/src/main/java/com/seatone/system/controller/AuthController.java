package com.seatone.system.controller;

import com.seatone.common.result.Result;
import com.seatone.common.utils.JwtUtils;
import com.seatone.system.entity.SysUser;
import com.seatone.system.service.SysUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginDTO dto) {
        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        String accessToken = JwtUtils.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = JwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("expiresIn", 7200);

        return Result.ok(result);
    }

    @GetMapping("/userinfo")
    public Result<Map<String, Object>> userinfo() {
        // 从SecurityContext获取当前用户
        Map<String, Object> userinfo = new HashMap<>();
        userinfo.put("username", "admin");
        userinfo.put("nickname", "管理员");
        userinfo.put("permissions", new String[]{"*"});
        return Result.ok(userinfo);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    @Data
    public static class LoginDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
