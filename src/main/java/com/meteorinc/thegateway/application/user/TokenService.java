package com.meteorinc.thegateway.application.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.Role;
import com.meteorinc.thegateway.domain.user.RoleType;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    public static final String BEARER_PREFIX = "Bearer ";

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00");

    private static final String ISSUER = "Events";

    Algorithm algorithm;

    int expirationMinutes;

    @Autowired
    public TokenService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-minutes}") int expirationMinutes) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMinutes = expirationMinutes;
    }


    public String generateToken(@NonNull final AppUser user){
        final String roles = StringUtils.join(user.getRoles().stream().map(Role::getName).map(RoleType::name).collect(Collectors.toList()), ',');

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withClaim("user_code", user.getUserCode().toString())
                .withClaim("user_roles", roles)
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(expirationMinutes)
                        .toInstant(ZONE_OFFSET)
                ).sign(algorithm);
    }

    public String getEmailOnToken(@NonNull final String token) {
        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build().verify(token).getSubject();
    }

    public String getUserCodeOnToken(@NonNull final String rawToken) {
        return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build().verify(formatToken(rawToken)).getClaim("user_code").asString();
    }

    public static String formatToken(@NonNull final String rawToken){
        return rawToken.replace(BEARER_PREFIX, "");
    }
}
