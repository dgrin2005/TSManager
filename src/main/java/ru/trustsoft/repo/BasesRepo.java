package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.BasesEntity;

@Transactional
public interface BasesRepo extends CrudRepository<BasesEntity, Long> {

    BasesEntity findByBasename(String basename);

    BasesEntity findByBaseid(int baseid);

}