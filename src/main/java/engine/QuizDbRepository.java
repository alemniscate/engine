package engine;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDbRepository extends PagingAndSortingRepository<QuizDb, Long> {}