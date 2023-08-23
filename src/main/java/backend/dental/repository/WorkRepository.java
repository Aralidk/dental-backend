package backend.dental.repository;
import backend.dental.entity.WorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<WorkEntity, Long> {
}
