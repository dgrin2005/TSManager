package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Users;

import java.util.List;

@Transactional
public interface UsersRepo extends CrudRepository<Users, Long> {

    Users findByUsername(String username);

    Users findById(int id);

    List<Users> findByLocked(boolean locked);

    List<Users> findByAdmin(boolean admin);

}