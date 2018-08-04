package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.ContragentsEntity;

@Transactional
public interface ContragentssRepo extends CrudRepository<ContragentsEntity, Long> {

    ContragentsEntity findByContragentname(String contragentname);

    ContragentsEntity findByContragentid(int contragentid);

}