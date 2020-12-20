package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RegisterDbService {

    @Autowired
    private RegisterDbRepository registerDbRepository;

    public Optional<RegisterDb> findRegisterDb(String email){
        return registerDbRepository.findById(email);
    }

    public List<RegisterDb> findRegisterDb(){
        return registerDbRepository.findAll();
    }

    public RegisterDb save(RegisterDb registerDb) {
        return registerDbRepository.save(registerDb);
    }

    public Long count() {
        return registerDbRepository.count();
    }

    public void delete(String email) {
        registerDbRepository.deleteById(email);
    }
}