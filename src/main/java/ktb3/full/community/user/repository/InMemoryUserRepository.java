package ktb3.full.community.user.repository;

import ktb3.full.community.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static Map<Long, User> userMap = new LinkedHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

/*    public UserRepository() {
        if(usersMap.isEmpty()){
        }
    }*/

    public User save(User user) {
        if(user.getId() == null) {
            user.assignId(sequence.incrementAndGet());
        }
        userMap.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String normalized = email.toLowerCase();
        return userMap.values().stream().filter(user -> normalized.equals(user.getEmail().toLowerCase())).findFirst();
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        if (nickname == null) return Optional.empty();
        return userMap.values().stream().filter(user -> nickname.equalsIgnoreCase(user.getNickname())).findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return findByNickname(nickname).isPresent();

    }

    @Override
    public void deleteById(Long id) {
        userMap.remove(id);

    }

}
