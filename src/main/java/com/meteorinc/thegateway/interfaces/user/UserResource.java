package com.meteorinc.thegateway.interfaces.user;

import com.meteorinc.thegateway.application.user.TokenService;
import com.meteorinc.thegateway.application.user.UserService;
import com.meteorinc.thegateway.domain.user.AppUser;
import com.meteorinc.thegateway.domain.user.RoleType;
import com.meteorinc.thegateway.interfaces.user.dto.LoginResponse;
import com.meteorinc.thegateway.interfaces.user.requests.LoginRequest;
import com.meteorinc.thegateway.interfaces.user.requests.UserCreationRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/gateway/user")
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserResource {

    AuthenticationManager authenticationManager;

    TokenService tokenService;

    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @NonNull LoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        var auth = authenticationManager.authenticate(authenticationToken);

        var user = (AppUser) auth.getPrincipal();

        return ResponseEntity.ok(LoginResponse.builder().token(tokenService.generateToken(user)).build());
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @NonNull UserCreationRequest request){
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/add-role/{userCode}")
    public void register(@PathVariable @NonNull UUID userCode, @RequestBody @NonNull final RoleType roleType){
        userService.addRole(userCode, roleType);
    }


}
