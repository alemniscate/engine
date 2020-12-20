package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

@Service
@Transactional
public class QuizDbService {

    @Autowired
    private QuizDbRepository quizDbRepository;

    public Optional<QuizDb> findQuizDb(long id){
        return quizDbRepository.findById(id);
    }

    public List<QuizDb> findQuizDb(Integer pageNo) {
        Integer pageSize = 10;
        String sortBy = "id";
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<QuizDb> pagedResult = quizDbRepository.findAll(paging);
         
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<QuizDb>();
        }
    }

    public QuizDb save(QuizDb quizDb) {
        return quizDbRepository.save(quizDb);
    }

    public Long count() {
        return quizDbRepository.count();
    }

    public void delete(long id) {
        quizDbRepository.deleteById(id);
    }
}