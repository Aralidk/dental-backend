package backend.dental.controller;

import backend.dental.entity.WorkEntity;
import backend.dental.entity.WorkListEntity;
import backend.dental.enums.WorkStatus;
import backend.dental.repository.WorkListRepository;
import backend.dental.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/status")
public class WorkStatusController {
    private final WorkRepository workRepository;
    private final WorkListRepository workListRepository;

    @Autowired // Added this annotation to inject WorkListRepository
    public WorkStatusController(WorkRepository workRepository, WorkListRepository workListRepository) {
        this.workRepository = workRepository;
        this.workListRepository = workListRepository; // Initialized the workListRepository
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateWorkStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Optional<WorkListEntity> optionalWork = workListRepository.findById(id);

        if (optionalWork.isPresent() && request.containsKey("status")) {
            String status = request.get("status");
            try {
                WorkStatus workStatus = WorkStatus.valueOf(status);
                WorkListEntity work = optionalWork.get();
                work.setWorkStatus(workStatus);
                workListRepository.save(work);
                return ResponseEntity.ok("Work status updated successfully.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid work status value.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/status/{id}")
    public ResponseEntity<String> getWorkStatus(@PathVariable Long id) {
        Optional<WorkEntity> optionalWork = workRepository.findById(id);

        if (optionalWork.isPresent()) {
            WorkEntity work = optionalWork.get();
            WorkStatus status = work.getWorkStatus();
            return ResponseEntity.ok(status.toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/work-status")
    public ResponseEntity<List<WorkStatus>> getAllWorkStatuses() {
        List<WorkStatus> workStatuses = Arrays.asList(WorkStatus.values());
        if (!workStatuses.isEmpty()) {
            return ResponseEntity.ok(workStatuses);
        } else {

            return ResponseEntity.noContent().build();
        }
    }

}
