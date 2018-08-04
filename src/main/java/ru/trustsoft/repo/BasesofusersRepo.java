package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Basesofusers;

@Transactional
public interface BasesofusersRepo extends CrudRepository<Basesofusers, Long> {

    Basesofusers findById(int id);

//    @Query("SELECT a FROM Basesofusers a WHERE a.userid=:userid")
//    List<Basesofusers> findBasesByUser(@Param("userid") Integer userid);



}