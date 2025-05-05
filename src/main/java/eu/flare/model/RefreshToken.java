package eu.flare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Enumerated(EnumType.ORDINAL)
    private RefreshTokenStatus refreshTokenStatus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RefreshTokenStatus getRefreshTokenStatus() {
        return refreshTokenStatus;
    }

    public void setRefreshTokenStatus(RefreshTokenStatus refreshTokenStatus) {
        this.refreshTokenStatus = refreshTokenStatus;
    }
}
