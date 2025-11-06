package org.mhejaju.digitalwalletchallenge.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;

    @Value("${jwt.expiration}")
    private int expiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Objects> extraClaims,
                                UserDetails userDetails) {
        return Jwts.builder().issuer("Digital Wallet")
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();

    }


    public SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateRefresh(Map<String, Objects> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSignInKey())
                .compact();

    }

    public String getEmailFromToken(String token) {
        return extractUsername(token);
    }

    public Boolean validateToken(String token) {
        String userEmail = extractUsername(token);
        if (userEmail != null && !isTokenExpired(token)) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            return isTokenValid(token, userDetails);
        }

        return false;
    }







}
