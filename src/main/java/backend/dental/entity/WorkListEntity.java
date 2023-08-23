package backend.dental.entity;

import backend.dental.enums.WorkStatus;
import backend.dental.worker.Worker;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "work_list")
public class WorkListEntity {

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<WorkEntity> workEntities;


    @Enumerated(EnumType.STRING)
    @Column(name = "work_status")
    private WorkStatus workStatus;

    @ManyToOne
    private Worker worker;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<WorkEntity> getWorkEntities() {
        return workEntities;
    }

    public void setWorkEntities(List<WorkEntity> workEntities) {
        this.workEntities = workEntities;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }


    public void setWorkStatus(WorkStatus workStatus) {
        this.workStatus = workStatus;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
