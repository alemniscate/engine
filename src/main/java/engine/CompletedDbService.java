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
public class CompletedDbService {

    @Autowired
    private CompletedDbRepository completedDbRepository;

    public Optional<CompletedDb> findCompletedDb(String user, long id){
        return completedDbRepository.findByUserAndId(user, id);
    }

    public List<CompletedDb> findCompletedDb(Integer pageNo, String user) {
        Integer pageSize = 10;
        String sortBy = "completedAt";
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, sortBy));
        Page<CompletedDb> pagedResult = completedDbRepository.findAllByUser(user, paging);
         
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<CompletedDb>();
        }
    }

    public CompletedDb save(CompletedDb completedDb) {
        return completedDbRepository.save(completedDb);
    }

    public Long count() {
        return completedDbRepository.count();
    }

    public Long count(String user) {
        return completedDbRepository.countByUser(user);
    }
}