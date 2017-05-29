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

}
