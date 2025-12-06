package com.aftersports.aftersports.infra.security;

import com.aftersports.aftersports.domain.model.User;
import com.aftersports.aftersports.domain.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public JwtAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");

        try {
            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                // Delega para o AuthService: valida JWT e busca o usuário
                User user = authService.resolveUserFromHeader(auth);

                var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
                var authentication = new UsernamePasswordAuthenticationToken(
                        user.getEmail(), null, java.util.List.of(authority));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token inválido: limpa o contexto e segue o fluxo (retornará 401 se a rota exigir auth)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
