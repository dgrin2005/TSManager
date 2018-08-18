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

    @Query(getAllUsersByRank)
    List<Users> findPaginated(Pageable pageable);

    String getAllUsersByRank= "from Users order by username ASC";

}