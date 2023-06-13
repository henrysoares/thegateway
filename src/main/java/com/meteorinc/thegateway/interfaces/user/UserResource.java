package com.meteorinc.thegateway.interfaces.user;

import com.meteorinc.thegateway.application.user.TokenService;
import com.meteorinc.thegateway.application.user.UserService;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.AppUserDTO;
import com.meteorinc.thegateway.domain.user.RoleType;

import com.meteorinc.thegateway.interfaces.user.dto.LoginResponse;
import com.meteorinc.thegateway.interfaces.user.requests.LoginRequest;
import com.meteorinc.thegateway.interfaces.user.requests.UserRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.meteorinc.thegateway.interfaces.RestConstants.*;

@RequestMapping("/api/gateway/user")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserResource {

    AuthenticationManager authenticationManager;

    TokenService tokenService;

    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @NonNull final LoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        var auth = authenticationManager.authenticate(authenticationToken);

        var user = (AppUser) auth.getPrincipal();

        return ResponseEntity.ok(LoginResponse.builder().token(tokenService.generateToken(user)).build());
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @NonNull UserRequest request){
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<AppUserDTO> findUserDetails(@PathVariable(USER_CODE_PATH_VARIABLE) @NonNull UUID userCode){
        final var user = userService.findUserDetails(userCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PatchMapping
    public ResponseEntity<AppUserDTO> updateUser(@NonNull @RequestHeader(AUTHORIZATION_HEADER) final String token,
                                         @RequestBody @NonNull UserRequest request){
        final var user = userService.updateUser(request, token).toDTO();
        return ResponseEntity.status(HttpStatus.OK)
                .body(user);
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<Void> deleteUser(@PathVariable(USER_CODE_PATH_VARIABLE) @NonNull UUID userCode){
        userService.deleteUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/add-role/{userCode}")
    public void addRole(@PathVariable @NonNull UUID userCode,
                         @RequestBody @NonNull final RoleType roleType){
        userService.addRole(userCode, roleType);
    }




}
