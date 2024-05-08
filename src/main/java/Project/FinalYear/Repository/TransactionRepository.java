package Project.FinalYear.Repository;

import Project.FinalYear.Entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    @Query(value = "SELECT User_id FROM Blacklisted_Users", nativeQuery = true)
    List<String> findBlacklistedUsers();
    // You can add custom query methods if needed
}
