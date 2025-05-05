package eu.flare.service;

import eu.flare.exceptions.notfound.RefreshTokenNotFoundException;
import eu.flare.model.RefreshToken;
import eu.flare.model.RefreshTokenStatus;
import eu.flare.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Autowired
    public JwtService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateToken(String username, List<String> userRoles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", userRoles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(jwtSecret)
                .compact();
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException exception) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractUserRoles(String token) {
        return (List<String>) Jwts.parserBuilder().setSigningKey(jwtSecret).build()
                .parseClaimsJws(token).getBody().get("roles");
    }

    public boolean isTokenRevoked(String token) throws RefreshTokenNotFoundException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));
        return refreshToken.getRefreshTokenStatus() == RefreshTokenStatus.REVOKED;
    }
}
