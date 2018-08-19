package ru.trustsoft.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Users;

import java.util.List;

@Transactional
public interface UsersRepo extends CrudRepository<Users, Long> {

    Users findByUsername(String username);

    Users findById(int id);

    @Query(value = "select * from Users a order by username asc" , nativeQuery = true)
    List<Users> findAllUserAsc();

    @Query(value = "select * from Users a order by username asc" , nativeQuery = true)
    List<Users> findPaginatedUserAsc(Pageable pageable);

    @Query(value = "select * from Users a order by username desc" , nativeQuery = true)
    List<Users> findPaginatedUserDesc(Pageable pageable);

    @Query(value = "select * from Users a  inner join contragents b on a.contragentid = b.id" +
            " order by b.contragentname asc" , nativeQuery = true)
    List<Users> findPaginatedContragentAsc(Pageable pageable);

    @Query(value = "select * from Users a  inner join contragents b on a.contragentid = b.id" +
            " order by b.contragentname desc" , nativeQuery = true)
    List<Users> findPaginatedContragentDesc(Pageable pageable);

}