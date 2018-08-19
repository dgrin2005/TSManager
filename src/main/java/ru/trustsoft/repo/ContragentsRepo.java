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

    @Query(value = "select * from Contragents a order by contragentname asc" , nativeQuery = true)
    List<Contragents> findAllContragentAsc();

    @Query(value = "select * from Contragents a order by contragentname asc" , nativeQuery = true)
    List<Contragents> findPaginatedContragentAsc(Pageable pageable);

    @Query(value = "select * from Contragents a order by contragentname desc" , nativeQuery = true)
    List<Contragents> findPaginatedContragentDesc(Pageable pageable);

}