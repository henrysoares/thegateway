package com.meteorinc.thegateway.application.user;

import com.meteorinc.thegateway.application.user.exceptions.UserNotFoundException;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.UserRepository;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppUserService {

    UserRepository userRepository;

    TokenService tokenService;

    /**
     * Busca um usuario na tabela de user, caso não encontre uma exception é retornada.
     * @param userCode Codigo do usuario.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AppUser findUser(@NonNull final UUID userCode){
        log.info(userCode.toString());
        return userRepository.findByUserCode(userCode).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Metodo que retorna a instacia de um usuario encontrado.
     * @param rawToken O token informado.
     */
    public AppUser findUserByToken(@NonNull final String rawToken){
        final UUID userCode = UUID.fromString(tokenService.getUserCodeOnToken(TokenService.formatToken(rawToken)));
        return findUser(userCode);
    }
}