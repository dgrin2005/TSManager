package ru.trustsoft.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.ContragentsEntity;

@Transactional
public interface ContragentssDao extends CrudRepository<ContragentsEntity, Long> {

    ContragentsEntity findByContragentname(String contragentname);

    public ContragentsEntity findByContragentid(int contragentid);

}