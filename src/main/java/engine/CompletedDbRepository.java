package engine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface CompletedDbRepository extends PagingAndSortingRepository<CompletedDb, Long> {
 
    Optional<CompletedDb> findByUserAndId(String user, long id);

    Page<CompletedDb> findAllByUser(String user, Pageable paging);

    List<CompletedDb> findAllByUser(String user);

    Long countByUser(String user);
}