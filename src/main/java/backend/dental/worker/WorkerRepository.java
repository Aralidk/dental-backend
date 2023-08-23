package backend.dental.worker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    Optional<Worker> findByEmail(String email);

    @Modifying
    @Query("UPDATE Worker w SET w.confirmStatus = :confirmStatus WHERE w.id = :id")
    int setConfirm(@Param("id") int id, @Param("confirmStatus") boolean confirmStatus);

    List<Worker> findByConfirmStatusFalse(); // Changed method name

    @Modifying
    @Query("DELETE FROM Worker w WHERE w.confirmStatus = false AND w.id = ?1")
    int deleteByConfirmStatusIsFalseAndId(int id); // Updated method name

    // 'setConfirmStatus' method removed as it's not related to querying
}
