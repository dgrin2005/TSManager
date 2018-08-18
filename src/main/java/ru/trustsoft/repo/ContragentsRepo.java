package ru.trustsoft.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Contragents;

import java.util.List;

@Transactional
public interface ContragentsRepo extends CrudRepository<Contragents, Long> {

    Contragents findByContragentname(String contragentname);

    Contragents findByInn(String inn);

    Contragents findById(int id);

    @Query(getAllContragentsByRank)
    List<Contragents> findPaginated(Pageable pageable);

    String getAllContragentsByRank= "from Contragents order by contragentname ASC";
}