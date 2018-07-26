package ru.trustsoft.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.UsersEntity;

import java.util.List;

@Transactional
public interface UsersRepo extends CrudRepository<UsersEntity, Long> {

    UsersEntity findByUsername(String username);

    UsersEntity findByUserid(int userid);

    List<UsersEntity> findByIslocked(boolean islocked);

}