package engine;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.*;

@Entity
public class CompletedDb {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long recodeKey;
    private String user;  //email
    private long id;
    private LocalDateTime completedAt;

    public CompletedDb() {}

    public CompletedDb(String user, long id, LocalDateTime completedAt) {
        this.user = user;
        this.id = id;
        this.completedAt = completedAt;
    } 

    CompletedDb(String user, long id) {
        this.user = user;
        this.id = id;
        this.completedAt = LocalDateTime.now();
    } 

    public String getUser() {
        return user;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    String getCompletedAtString() {
        return completedAt.toString();      
    }
}
