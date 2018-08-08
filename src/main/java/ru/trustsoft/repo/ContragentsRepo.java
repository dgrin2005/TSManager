package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Contragents;

@Transactional
public interface ContragentsRepo extends CrudRepository<Contragents, Long> {

    Contragents findByContragentname(String contragentname);

    Contragents findByInn(String inn);

    Contragents findById(int id);

}