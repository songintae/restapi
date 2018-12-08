package restapi.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmailAndPassword(@NonNull String email, @NonNull String password);
    Optional<Account> findByEmail(String email);
}
