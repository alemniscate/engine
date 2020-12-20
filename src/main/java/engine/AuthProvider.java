package engine;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    RegisterDbService rdbs;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String inputName = authentication.getName();
        String inputPass = authentication.getCredentials().toString();

        if (isValidLogin(inputName, inputPass)) {
            return new UsernamePasswordAuthenticationToken(inputName, inputPass, authentication.getAuthorities());
        } else {
            throw new BadCredentialsException("wrong userid or password");
        }
    }

    boolean isValidLogin(String email, String password) {
        Optional<RegisterDb> registerDbNullable = rdbs.findRegisterDb(email);
        if (!registerDbNullable.isPresent()) {
            return false;
            //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");                  
        }
        RegisterDb registerDb = registerDbNullable.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, registerDb.getCriptPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}