package ru.trustsoft.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Bases;

import java.util.List;

@Transactional
public interface BasesRepo extends CrudRepository<Bases, Long> {

    Bases findByBasename(String basename);

    Bases findById(int id);

    @Query(value = "SELECT * FROM Bases a INNER JOIN basesofusers b ON a.id = b.baseid WHERE b.userid=:userid", nativeQuery = true)
    List<Bases> findByUser(@Param("userid") Integer userid);

    @Query(getAllBasesByRank)
    List<Bases> findPaginated(Pageable pageable);

    String getAllBasesByRank= "from Bases order by basename ASC";

}