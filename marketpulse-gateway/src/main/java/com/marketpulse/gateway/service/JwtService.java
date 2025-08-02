package com.marketpulse.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service  // This tells Spring: "Hey, this is a service class that can be used by other parts of the application"
public class JwtService {
    
    // These values come from application.properties file
    // @Value means: "Get the value from properties file and put it in this variable"
    @Value("${jwt.secret}")
    private String secretKey;  // This will be: "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;  // This will be: 86400000 (24 hours in milliseconds)
    
    // Simple method to generate a JWT token for a username
    // This is what we call when user logs in successfully
    public String generateToken(String username) {
        // We create an empty map for extra information (we'll add more later)
        return generateToken(new HashMap<>(), username);
    }
    
    // More advanced method to generate JWT token with extra information
    // extraClaims = additional information we want to store in the token (like user role, name, etc.)
    // username = the main identifier for the user
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts  // Jwts is the main class for creating JWT tokens
                .builder()  // Start building a new JWT token
                .setClaims(extraClaims)  // Add extra information (like user role, name)
                .setSubject(username)  // Set the main subject (username) of the token
                .setIssuedAt(new Date(System.currentTimeMillis()))  // When was this token created? (now!)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))  // When does this token expire? (24 hours from now)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Sign the token with our secret key using HS256 algorithm
                .compact();  // Create the final JWT token string
    }
    
    // Check if a JWT token is valid (not expired, properly signed)
    // This is what we call when user makes an API request
    public boolean isTokenValid(String token) {
        try {
            // If token is expired, this will throw an exception
            // If token is malformed, this will throw an exception
            // If everything is OK, return true
            return !isTokenExpired(token);
        } catch (Exception e) {
            // If anything goes wrong, the token is invalid
            return false;
        }
    }
    
    // Extract username from JWT token
    // This is what we call to get the username from a valid token
    public String extractUsername(String token) {
        // The "subject" of a JWT token is usually the username
        return extractClaim(token, Claims::getSubject);
    }
    
    // Check if JWT token has expired
    private boolean isTokenExpired(String token) {
        // Get the expiration date from token
        Date expirationDate = extractExpiration(token);
        // Compare with current date
        return expirationDate.before(new Date());  // If expiration date is before now, token is expired
    }
    
    // Extract expiration date from JWT token
    private Date extractExpiration(String token) {
        // Get the expiration claim from the token
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Generic method to extract any claim (piece of information) from JWT token
    // claimsResolver = a function that tells us which claim to extract
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // First, extract all claims from the token
        final Claims claims = extractAllClaims(token);
        // Then, apply the function to get the specific claim we want
        return claimsResolver.apply(claims);
    }
    
    // Extract all claims (all information) from JWT token
    private Claims extractAllClaims(String token) {
        return Jwts  // Jwts is also used to read/parse JWT tokens
                .parserBuilder()  // Start building a parser
                .setSigningKey(getSignInKey())  // Use our secret key to verify the token signature
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the token and verify its signature
                .getBody();  // Get all the claims (information) from the token
    }
    
    // Create the secret key used to sign and verify JWT tokens
    private Key getSignInKey() {
        // Our secret is stored as a Base64 string, so we need to decode it first
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        // Create a cryptographic key from the decoded bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }
}