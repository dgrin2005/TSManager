/**
 * TerminalServerManager
 *    BasesRepo.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.0 dated 2018-08-23
 */

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

    @Query(value = "SELECT * FROM Bases a INNER JOIN basesofusers b ON a.id = b.baseid WHERE b.userid=:userid",
            nativeQuery = true)
    List<Bases> findByUser(@Param("userid") Integer userid);

    @Query(value = "select * from Bases a order by basename asc" , nativeQuery = true)
    List<Bases> findAllBaseAsc();

    @Query(value = "SELECT * FROM Bases a INNER JOIN basesofusers b ON a.id = b.baseid WHERE b.userid=:userid" +
            " ORDER BY basename ASC", nativeQuery = true)
    List<Bases> findAllBaseAscByUser(@Param("userid") Integer userid);

    @Query(value = "select * from Bases a order by basename asc" , nativeQuery = true)
    List<Bases> findPaginatedBaseAsc(Pageable pageable);

    @Query(value = "select * from Bases a order by basename desc" , nativeQuery = true)
    List<Bases> findPaginatedBaseDesc(Pageable pageable);

    @Query(value = "select * from Bases a  inner join contragents b on a.contragentid = b.id" +
            " order by b.contragentname asc" , nativeQuery = true)
    List<Bases> findPaginatedContragentAsc(Pageable pageable);

    @Query(value = "select * from Bases a  inner join contragents b on a.contragentid = b.id" +
            " order by b.contragentname desc" , nativeQuery = true)
    List<Bases> findPaginatedContragentDesc(Pageable pageable);

}