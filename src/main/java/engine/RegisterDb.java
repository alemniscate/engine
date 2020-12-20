package engine;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RegisterDb {
    @Id
    private String email;
    private String criptPassword;

    public RegisterDb() {}

    public RegisterDb(String email, String criptPassword) {
        this.email = email;
        this.criptPassword = criptPassword;
    } 

    public String getEmail() {
        return email;
    }

    public String getCriptPassword() {
        return criptPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCriptPassword(String criptPassword) {
        this.criptPassword = criptPassword;
    }
}
