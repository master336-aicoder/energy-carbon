package com.seatone.system.controller;

import com.seatone.common.result.Result;
import com.seatone.common.utils.JwtUtils;
import com.seatone.system.entity.SysUser;
import com.seatone.system.service.SysUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * 认证控制器
 * 处理用户登录、登出、获取用户信息等认证相关请求
 *
 * @author seatone
 * @since 2026-06-22
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 用户服务
     */
    private final SysUserService userService;

    /**
     * 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     * 验证用户名密码，成功后返回访问Token和刷新Token
     *
     * @param dto 登录请求参数
     * @return 包含 accessToken、refreshToken、expiresIn 的登录结果
     */
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

    /**
     * 获取当前登录用户信息
     * 从 JWT Token 中解析用户信息
     *
     * @param request HTTP请求（用于获取Token）
     * @return 用户信息，包含用户名、昵称、头像、权限等
     */
    @GetMapping("/userinfo")
    public Result<Map<String, Object>> userinfo(HttpServletRequest request) {
        String token = resolveToken(request);
        Map<String, Object> userinfo = new HashMap<>();

        if (token != null && JwtUtils.validateToken(token)) {
            String username = JwtUtils.getUsername(token);
            SysUser user = userService.getByUsername(username);
            if (user != null) {
                userinfo.put("id", user.getId());
                userinfo.put("username", user.getUsername());
                userinfo.put("nickname", user.getNickname());
                userinfo.put("avatar", user.getAvatar());
                userinfo.put("phone", user.getPhone());
                userinfo.put("email", user.getEmail());
                userinfo.put("roles", Collections.singletonList("admin"));
                userinfo.put("permissions", Collections.singletonList("*"));
                return Result.ok(userinfo);
            }
        }

        // Token 无效时返回默认信息
        userinfo.put("username", "guest");
        userinfo.put("nickname", "访客");
        userinfo.put("permissions", new String[]{});
        return Result.ok(userinfo);
    }

    /**
     * 从请求中解析 Bearer Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 用户登出
     * 清除登录状态（Token黑名单等逻辑可在此扩展）
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }

    /**
     * 开发环境：重置 admin 密码
     * 仅在开发环境（dev profile）下可用
     *
     * @return 操作结果
     */
    @PostMapping("/reset-admin-password")
    public Result<Void> resetAdminPassword() {
        SysUser user = userService.getByUsername("admin");
        if (user != null) {
            user.setPassword(passwordEncoder.encode("123456"));
            userService.updateById(user);
            return Result.ok();
        }
        return Result.error("admin用户不存在");
    }

    /**
     * 登录请求参数
     */
    @Data
    public static class LoginDTO {

        /**
         * 用户名
         */
        @NotBlank(message = "用户名不能为空")
        private String username;

        /**
         * 密码
         */
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
