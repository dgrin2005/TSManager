package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Bases;

@Transactional
public interface BasesRepo extends CrudRepository<Bases, Long> {

    Bases findByBasename(String basename);

    Bases findById(int id);

}