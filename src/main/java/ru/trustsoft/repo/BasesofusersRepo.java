package ru.trustsoft.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Basesofusers;

import java.util.List;

@Transactional
public interface BasesofusersRepo extends CrudRepository<Basesofusers, Long> {

    Basesofusers findById(int id);

    @Query(value = "SELECT * FROM Basesofusers u WHERE u.userid=:userid", nativeQuery = true)
    List<Basesofusers> findBasesByUser(@Param("userid") Integer userid);



}