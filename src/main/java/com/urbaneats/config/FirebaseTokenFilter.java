package com.urbaneats.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.urbaneats.dto.error.Error;
import com.urbaneats.dto.error.ErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FirebaseTokenFilter extends OncePerRequestFilter {
    /**
     * Authenticating user via fireBase authorizer verify fireBase token and extract
     * Uid and Email from token
     */

    private static final AntPathRequestMatcher[] EXCLUDED_PATHS = {
            new AntPathRequestMatcher("/api/public/**"),
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher("/error")
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        for (AntPathRequestMatcher matcher : EXCLUDED_PATHS) {
            if (matcher.matches(request)) {
                chain.doFilter(request, response);
                return;
            }
        }

        String requestURI = request.getRequestURI();
        Cookie[] cookies = request.getCookies();
        logger.error("cookies received:" + Arrays.toString(cookies));
        if(cookies == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        List<Cookie> accessToken = Arrays.stream(cookies).filter(cookie -> cookie.getName().equalsIgnoreCase("access_token")).toList();

        if (accessToken.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String token = accessToken.get(0).getValue();

        try{
            if(token != null && !token.isBlank()) {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

                //if token is invalid
                if (decodedToken==null){
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid token!");
                }

                //Extract Uid and Email
                String uid= decodedToken.getUid();
                String email = decodedToken.getEmail();
                decodedToken.getClaims();
                String authorities = "";
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("uid", uid);
                request.setAttribute("email",email);
                request.setAttribute("accessToken", token);
            }
        } catch (FirebaseAuthException e) {
            ObjectMapper mapper = new ObjectMapper();
            Error error = Error.builder()
                    .errorType(e.getAuthErrorCode().toString().equalsIgnoreCase("EXPIRED_ID_TOKEN") ?
                            ErrorType.AUTH_TOKEN_EXPIRED : ErrorType.AUTH_TOKEN_INVALID)
                    .errorMessage(e.getAuthErrorCode().toString().equalsIgnoreCase("EXPIRED_ID_TOKEN") ?
                            "Auth ID token has expired. Get a fresh ID token and try again." :
                            "Failed to parse Auth ID token. Make sure you passed a valid token"
                    )
                    .build();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(mapper.writeValueAsString(error));
            return;
        }

        chain.doFilter(request,response);
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//
//        //Extracts token from header
//        String token = request.getHeader("Authorization");
//
//        //checks if token is there
//        if (token == null ) {
////            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Missing token!");
//            chain.doFilter(request, response);
//            return;
//        }
//
//
//        token = token.substring(7);
//        FirebaseToken decodedToken = null;
//        try {
//            //verifies token to firebase server
//            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
//        } catch (FirebaseAuthException e) {
//
//            ObjectMapper mapper = new ObjectMapper();
//            Error error = Error.builder()
//                    .errorType(e.getAuthErrorCode().toString().equalsIgnoreCase("EXPIRED_ID_TOKEN") ?
//                            ErrorType.AUTH_TOKEN_EXPIRED : ErrorType.AUTH_TOKEN_INVALID)
//                    .errorMessage(e.getAuthErrorCode().toString().equalsIgnoreCase("EXPIRED_ID_TOKEN") ?
//                            "Auth ID token has expired. Get a fresh ID token and try again." :
//                            "Failed to parse Auth ID token. Make sure you passed a valid token"
//                    )
//                    .build();
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.getWriter().write(mapper.writeValueAsString(error));
//            return;
//        }
//        //if token is invalid
//        if (decodedToken==null){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid token!");
//        }
//
//        //Extract Uid and Email
//        String uid= decodedToken.getUid();
//        String email = decodedToken.getEmail();
//        decodedToken.getClaims();
//        String authorities = "";
//        List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//
//    /*
//    //set Uid and Email to request
//    void setAttribute(java.lang.String name, java.lang.Object o)
//    */
//
//        request.setAttribute("uid", uid);
//        request.setAttribute("email",email);
//
//        chain.doFilter(request,response);
//    }
}
