package com.urbaneats.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseInitialization {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseInitialization.class);

    @Value("classpath:/private-key.json")
    private Resource privateKey;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream credentials = new ByteArrayInputStream(privateKey.getContentAsByteArray());
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentials))
                .build();
        return FirebaseApp.initializeApp(firebaseOptions);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}

