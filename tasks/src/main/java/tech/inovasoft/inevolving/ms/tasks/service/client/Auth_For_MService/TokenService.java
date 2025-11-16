package tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.MicroServices;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private final RSAPublicKey publicKey;

    public TokenService(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public TokenValidateResponse validateToken(String token) {
        try {

            Algorithm algorithm = Algorithm.RSA256(publicKey);
            return new TokenValidateResponse(
                    JWT.require(algorithm)
                            .withIssuer("AuthForMService")
                            .withAudience(MicroServices.TASKS_SERVICE.getValue())
                            .build()
                            .verify(token)
                            .getSubject(),
                    JWT.require(algorithm)
                            .withIssuer("AuthForMService")
                            .withAudience(MicroServices.TASKS_SERVICE.getValue())
                            .build()
                            .verify(token)
                            .getAudience().getFirst()
            );
        } catch (IncorrectClaimException e) {
            throw new RuntimeException("Invalid token");
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant createExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }


}
