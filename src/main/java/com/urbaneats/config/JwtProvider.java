package com.urbaneats.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        String jwt = Jwts.builder()
                .issuedAt(new Date())
                .expiration(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        return jwt;
    }

    public String getEmailFromJwtToken(String jwt) {
        jwt = jwt.substring(7);
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        return claims.get("email").toString();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<String> auths = new HashSet<String>();

        for(GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
