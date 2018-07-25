package ru.trustsoft.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.BasesEntity;

@Transactional
public interface BasesDao extends CrudRepository<BasesEntity, Long> {

    BasesEntity findByBasename(String basename);

    public BasesEntity findByBaseid(int baseid);

}