package backend.dental.repository;
import backend.dental.entity.WorkListEntity;
import backend.dental.enums.WorkStatus;
import backend.dental.worker.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkListRepository extends JpaRepository<WorkListEntity, Long> {
    List<WorkListEntity> findByWorkStatus(WorkStatus workStatus);
    List<WorkListEntity> findByWorker(Worker assignedUser);
    List<WorkListEntity> findAllByOrderByCreatedDateAsc();
    List<WorkListEntity> findAllByOrderByCreatedDateDesc();
    List<WorkListEntity> findByWorkStatusNot(WorkStatus workStatus);

    List<WorkListEntity> findByWorkerId(int currentUserId);

    List<WorkListEntity> findByWorkerIdAndWorkStatusNot(int currentUserId, WorkStatus teslim);
}