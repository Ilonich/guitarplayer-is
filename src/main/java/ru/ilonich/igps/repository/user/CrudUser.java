package ru.ilonich.igps.repository.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.model.User;

@Transactional(readOnly = true)
public interface CrudUser extends JpaRepository<User, Integer> {

    @EntityGraph(attributePaths={"roles"})
    @Query("SELECT u FROM User u WHERE u.email=?1")
    User findByEmail(String email);

    @EntityGraph(attributePaths={"roles"})
    @Query("SELECT u FROM User u WHERE u.id=?1")
    User getById(Integer id);

    @Override
    @Transactional
    User save(User user);

    @Query("SELECT count(u)>0 FROM User u WHERE u.email=?1")
    boolean existsByEmail(String email);

    @Query("SELECT count(u)>0 FROM User u WHERE u.username=?1")
    boolean existsByUsername(String username);
}
