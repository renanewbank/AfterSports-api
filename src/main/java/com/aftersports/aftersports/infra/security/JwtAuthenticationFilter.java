package com.aftersports.aftersports.infra.security;

import com.aftersports.aftersports.infra.security.JwtService.JwtPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);

    if (auth != null && auth.startsWith("Bearer ")) {
      try {
        JwtPayload p = jwtService.parseAndValidate(auth.substring(7));
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + p.role()));
        var authToken = new UsernamePasswordAuthenticationToken(p.email(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } catch (Exception ignored) {
      
      }
    }

    chain.doFilter(req, res);
  }
}
