/**
 * TerminalServerManager
 *    BasesofusersRepo.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.repo;

import org.springframework.data.domain.Pageable;
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

    @Query(value = "select * from basesofusers a inner  join bases b on a.baseid = b.id order by basename asc",
            nativeQuery = true)
    List<Basesofusers> findPaginatedBaseAsc(Pageable pageable);

    @Query(value = "select * from basesofusers a inner  join bases b on a.baseid = b.id order by basename desc",
            nativeQuery = true)
    List<Basesofusers> findPaginatedBaseDesc(Pageable pageable);

    @Query(value = "select * from basesofusers a inner join users u on a.userid = u.id order by username asc",
            nativeQuery = true)
    List<Basesofusers> findPaginatedUserAsc(Pageable pageable);

    @Query(value = "select * from basesofusers a inner join users u on a.userid = u.id order by username desc",
            nativeQuery = true)
    List<Basesofusers> findPaginatedUserDesc(Pageable pageable);

    @Query(value = "SELECT * FROM Basesofusers a inner  join bases b on a.baseid = b.id WHERE a.userid=:userid" +
            " order by basename asc", nativeQuery = true)
    List<Basesofusers> findBasesByUserPaginatedBaseAsc(Pageable pageable, @Param("userid") Integer userid);

    @Query(value = "SELECT * FROM Basesofusers a inner  join bases b on a.baseid = b.id WHERE a.userid=:userid" +
            " order by basename desc", nativeQuery = true)
    List<Basesofusers> findBasesByUserPaginatedBaseDesc(Pageable pageable, @Param("userid") Integer userid);

}