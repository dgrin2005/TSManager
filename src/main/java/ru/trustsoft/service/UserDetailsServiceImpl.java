/**
 * TerminalServerManager
 *    UserDetailsService.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.0 dated 2018-08-23
 */

package ru.trustsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trustsoft.model.Users;
import ru.trustsoft.repo.UsersRepo;

import java.util.ArrayList;
import java.util.Collection;


@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepo userRepo;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException, DataAccessException {

        Users userEntity = userRepo.findByUsername(userName);
        if (userEntity == null)
            throw new UsernameNotFoundException("User " + userName + "not found");

        String username = userEntity.getUsername();
        String password = userEntity.getUserpassword();
        boolean enabled = !userEntity.isLocked();
        boolean accountNonExpired = !userEntity.isLocked();
        boolean credentialsNonExpired = !userEntity.isLocked();
        boolean accountNonLocked = !userEntity.isLocked();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (userEntity.isAdm()) {
            authorities.add((GrantedAuthority) () -> "ADMIN");
        } else {
            if (username.toUpperCase().startsWith("DEMO")) {
                authorities.add((GrantedAuthority) () -> "DEMO");
            } else {
                authorities.add((GrantedAuthority) () -> "USER");
            }
        }

        return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities);

    }
}