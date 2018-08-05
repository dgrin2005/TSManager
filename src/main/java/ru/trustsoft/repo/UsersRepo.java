package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Users;

@Transactional
public interface UsersRepo extends CrudRepository<Users, Long> {

    Users findByUsername(String username);

    Users findById(int id);

}