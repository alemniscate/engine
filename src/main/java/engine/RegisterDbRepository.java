package engine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterDbRepository extends JpaRepository<RegisterDb, String> {}