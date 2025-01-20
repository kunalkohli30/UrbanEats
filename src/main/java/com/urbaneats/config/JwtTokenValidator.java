//package com.urbaneats.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.util.List;
//
////@Component
//public class JwtTokenValidator
//        extends OncePerRequestFilter
//{
//
//    @Autowired
//    @Qualifier("handlerExceptionResolver")
//    private HandlerExceptionResolver resolver;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws BadCredentialsException, ServletException, IOException {
//        String jwt = request.getHeader(JwtConstant.JWT_HEADER);
//        if(jwt != null) {
//            jwt = jwt.substring(7);
//
//            try {
//                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
//                Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
//
//                String email = String.valueOf(claims.get("email"));
//                String authorities = String.valueOf(claims.get("authorities"));
////                We will get all the roles like ROLE_CUSTOMER, ROLE_ADMIN which would be converted to list below
//                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
//                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                filterChain.doFilter(request, response);
//            } catch(ExpiredJwtException e) {
////                throw new BadCredentialsException("JWT Token expired, please enter a valid JWT token");
////                throw new ResponseStatusException(
////                        HttpStatus.NOT_FOUND, "Foo Not Found", e);
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("The provided JWT Token has expired. Kindly re-authenticate and provide a fresh token");
//            }
//            catch (Exception e) {
//                throw new BadCredentialsException("Invalid token.....");
//            }
//        }
//        else
//            filterChain.doFilter(request, response);
//
//    }
//}
