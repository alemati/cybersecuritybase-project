package sec.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.User;
import sec.project.domain.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findByUser(User user);
}
