package ktb3.full.community.user.repository;

import ktb3.full.community.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    List<User> findAll();

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    void deleteById(Long id);

}
