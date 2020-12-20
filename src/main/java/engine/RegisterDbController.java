package engine;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@RestController
public class RegisterDbController{

    @Autowired
    private RegisterDbService rdbs;

    boolean isValidRegister(Register register) {
        String email = register.getEmail();
        String password = register.getPassword();
        if (email == null || password == null) {
            return false;            
        }
        int i = email.indexOf('@');
        if (i == -1) {
            return false;   
        }
        i = email.indexOf('.', i);
        if (i == -1) {
            return false;
        }

        if (password.length() < 5) {
            return false;
        }
        return true;
    }

    @PostMapping(value = "/api/register") 
    public String createRegister(@RequestBody Register register) {

        Optional<RegisterDb> registerDbNullable = rdbs.findRegisterDb(register.getEmail());
        if (registerDbNullable.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");   
        }
        if (!isValidRegister(register)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String cryptPassword = encoder.encode(register.getPassword());
        RegisterDb registerDb = new RegisterDb(register.getEmail(), cryptPassword);
        rdbs.save(registerDb);

        return "";

    }

    RegisterDb getRegisterDb(String email) {
        Optional<RegisterDb> registerDbNullable = rdbs.findRegisterDb(email);
        if (!registerDbNullable.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "");   
        }
        return registerDbNullable.get();
    }

    
}

class Register {
/**
    {
        "email": "test@gmail.com",
        "password": "secret"
      }
*/
    private String email;
    private String password;

    public Register() {}

    public Register(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}