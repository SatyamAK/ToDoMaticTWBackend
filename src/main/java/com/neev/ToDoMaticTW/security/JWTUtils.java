package com.neev.ToDoMaticTW.security;

import com.neev.ToDoMaticTW.models.User;
import com.neev.ToDoMaticTW.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JWTUtils {

    @Value("${todomaticTW.secret}")
    private String secret;
    @Value("${todomaticTW.tokenExpiration}")
    private Integer expiration;

    @Autowired
    private UserService userService;

    private byte[] secretBytesGenerator(){
        String base64Secret = Encoders.BASE64.encode(secret.getBytes());
        byte[] secretBytes = Decoders.BASE64.decode(base64Secret);

        return secretBytes;
    }
    public String generateToken(Authentication authentication){

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime()) + expiration))
                .signWith(Keys.hmacShaKeyFor(secretBytesGenerator()), SignatureAlgorithm.HS512)
                .compact();
    }

    private String getTrimmedToken(String token){
        return token.substring(7, token.length());
    }
    public String getUsernameFromToken(String token){

        if(token != null && isValidToken(token)){
            String username = Jwts.parserBuilder()
                    .setSigningKey(secretBytesGenerator())
                    .build()
                    .parseClaimsJws(getTrimmedToken(token))
                    .getBody().getSubject();

            return username;
        }

        return null;
    }

    public boolean isValidToken(String token){
        try {
            String base64Secret = Encoders.BASE64.encode(secret.getBytes());
            byte[] secretBytes = Decoders.BASE64.decode(base64Secret);

            Jwts.parserBuilder().setSigningKey(secretBytesGenerator()).build().parse(getTrimmedToken(token));
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
