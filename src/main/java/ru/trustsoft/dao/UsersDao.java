package ru.trustsoft.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.UsersEntity;

import java.util.List;

@Transactional
public interface UsersDao extends CrudRepository<UsersEntity, Long> {

    UsersEntity findByUsername(String username);

    public UsersEntity findByUserid(int userid);

    public List<UsersEntity> findByIslocked(boolean islocked);

}