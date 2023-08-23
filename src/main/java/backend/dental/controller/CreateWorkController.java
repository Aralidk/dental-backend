package backend.dental.controller;

import backend.dental.entity.WorkEntity;
import backend.dental.entity.WorkListEntity;
import backend.dental.enums.Role;
import backend.dental.enums.WorkStatus;
import backend.dental.repository.WorkListRepository;
import backend.dental.repository.WorkRepository;
import backend.dental.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/create-work")
public class CreateWorkController {
    private final WorkRepository workRepository;
    private final WorkListRepository workListRepository;


    @Autowired
    public CreateWorkController(WorkRepository workRepository, WorkListRepository workListRepository) {
        this.workRepository = workRepository;
        this.workListRepository = workListRepository;
    }

    @GetMapping("/work-list/not-delivered")
    public ResponseEntity<List<WorkListEntity>> getNotDeliveredWorks() {
        List<WorkListEntity> notDeliveredWorks = workListRepository.findByWorkStatusNot(WorkStatus.Teslim);
        notDeliveredWorks.sort(Comparator.comparing(WorkListEntity::getCreatedDate).reversed());
        return ResponseEntity.ok(notDeliveredWorks);
    }


    @GetMapping("/work-list/filter-by-month")
    public ResponseEntity<List<WorkListEntity>> filterWorkListByMonth(@RequestParam int month) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().build();
        }

        List<WorkListEntity> filteredList = workListRepository.findAll()
                .stream()
                .filter(work -> work.getCreatedDate().getMonthValue() == month)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredList);
    }

    @GetMapping("/work-list/filter-by-date")
    public ResponseEntity<List<WorkListEntity>> filterWorkListByDate(@RequestParam String sortBy) {
        List<WorkListEntity> filteredList;

        if (sortBy.equals("asc")) {
            filteredList = workListRepository.findAllByOrderByCreatedDateAsc(); // Use the correct date property
        } else if (sortBy.equals("desc")) {
            filteredList = workListRepository.findAllByOrderByCreatedDateDesc(); // Use the correct date property
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (!filteredList.isEmpty()) {
            return ResponseEntity.ok(filteredList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//fiyat öncesi
//    @PostMapping("/create")
//    public ResponseEntity<String> createWorkEntity(@RequestBody WorkListEntity workListEntity) {
//        try {
//            List<WorkEntity> workEntities = workListEntity.getWorkEntities();
//            List<WorkEntity> savedEntities = workRepository.saveAll(workEntities);
//            workListEntity.setCreatedDate(LocalDateTime.now());
//            createWorkList(savedEntities);
//            return ResponseEntity.status(HttpStatus.CREATED).body("WorkEntities created with IDs: " + savedEntities);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating WorkEntity.");
//        }
//    }


    //fiyat eklendikten sonra

    @PostMapping("/create")
    public ResponseEntity<String> createWorkEntity(@RequestBody WorkListEntity workListEntity) {
        try {
            List<WorkEntity> workEntities = workListEntity.getWorkEntities();
            List<WorkEntity> savedEntities = new ArrayList<>();

            // Her bir iş nesnesi için ücret hesapla ve kaydet
            for (WorkEntity workEntity : workEntities) {
                BigDecimal calculatedPrice = workEntity.calculatePrice();
                workEntity.setPrice(calculatedPrice);

                savedEntities.add(workRepository.save(workEntity));
            }

            workListEntity.setCreatedDate(LocalDateTime.now());
            createWorkList(savedEntities);

            return ResponseEntity.status(HttpStatus.CREATED).body("WorkEntities created with IDs: " + savedEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating WorkEntity.");
        }
    }

    @PutMapping("/update-price/{workListId}")
    public ResponseEntity<String> updateWorkListPrice(@PathVariable Long workListId) {
        try {
            // İş listesini veritabanından alın
            Optional<WorkListEntity> workListOptional = workListRepository.findById(workListId);

            if (workListOptional.isPresent()) {
                WorkListEntity workList = workListOptional.get();
                List<WorkEntity> workEntities = workList.getWorkEntities();

                // Her bir iş nesnesi için ücreti hesapla ve güncelle
                for (WorkEntity workEntity : workEntities) {
                    BigDecimal calculatedPrice = workEntity.calculatePrice();
                    workEntity.setPrice(calculatedPrice);
                }

                // İş listesini güncelle
                workListRepository.save(workList);

                return ResponseEntity.ok("WorkListEntity with ID " + workListId + " prices updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("WorkListEntity with ID " + workListId + " not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating WorkListEntity prices.");
        }
    }



    @PutMapping("/update/{workListId}")
    public ResponseEntity<String> updateWorkList(@PathVariable Long workListId, @RequestBody WorkListEntity updatedWorkListEntity) {
        try {
            // İlk olarak, veritabanından mevcut iş listesini alın
            Optional<WorkListEntity> existingWorkListOptional = workListRepository.findById(workListId);

            if (existingWorkListOptional.isPresent()) {
                WorkListEntity existingWorkList = existingWorkListOptional.get();

                // Güncellenmiş iş listesi bilgilerini alın
                List<WorkEntity> updatedWorkEntities = updatedWorkListEntity.getWorkEntities();

                // Mevcut iş listesinin işlerini güncellemek için önceki işleri silin
                existingWorkList.getWorkEntities().clear();

                // Güncellenmiş işleri ekleyin
                existingWorkList.getWorkEntities().addAll(updatedWorkEntities);

                // İş listesinin oluşturulma tarihini güncelleyin (isteğe bağlı)
                existingWorkList.setCreatedDate(LocalDateTime.now());

                // İş listesini güncelleyin
                workListRepository.save(existingWorkList);

                return ResponseEntity.ok("WorkListEntity with ID " + workListId + " updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("WorkListEntity with ID " + workListId + " not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating WorkListEntity.");
        }
    }


    @GetMapping("/work-list-byuser")
    public ResponseEntity<List<WorkListEntity>> getWorkList(int currentUserId, Role userRole) {
        List<WorkListEntity> workList;

        if (userRole == Role.ADMIN) {
            workList = workListRepository.findAll();
        } else if (userRole == Role.CUSTOMER) {
            workList = workListRepository.findByWorkerId(currentUserId);
        } else if (userRole == Role.LABWORKER) {
            workList = workListRepository.findByWorkerIdAndWorkStatusNot(currentUserId, WorkStatus.Teslim);
        } else {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(workList);
    }

    @PostMapping("/create-work-list")
    public ResponseEntity<String> createWorkList(@RequestBody List<WorkEntity> workEntities) {
        try {
            WorkListEntity workList = new WorkListEntity();
            workList.setWorkEntities(workEntities);

            WorkListEntity savedWorkList = workListRepository.save(workList);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("WorkList created with ID: " + savedWorkList.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while creating WorkList.");
        }
    }

    @GetMapping("/work-list/{id}")
    public ResponseEntity<WorkListEntity> getWorkListById(@PathVariable Long id) {
        Optional<WorkListEntity> workList = workListRepository.findById(id);

        return workList.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/work-list")
    public ResponseEntity<List<WorkListEntity>> getAllWorkLists() {
        List<WorkListEntity> workLists = workListRepository.findAll();

        if (!workLists.isEmpty()) {
            return ResponseEntity.ok(workLists);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/work-list/filter")
    public ResponseEntity<List<WorkListEntity>> filterWorkListByStatus(@RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            WorkStatus workStatus = WorkStatus.valueOf(status);
            List<WorkListEntity> filteredList = workListRepository.findByWorkStatus(workStatus);
            return ResponseEntity.ok(filteredList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/work-list/assign/{id}")
    public ResponseEntity<String> assignWork(@PathVariable Long id, @RequestBody Worker assignedUser) {
        Optional<WorkListEntity> optionalWork = workListRepository.findById(id);

        if (optionalWork.isPresent()) {
            WorkListEntity work = optionalWork.get();
            work.setWorker(assignedUser); // Update the worker property
            workListRepository.save(work);
            return ResponseEntity.ok("Work assigned successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/work-list/assign-and-filter/{id}")
    public ResponseEntity<String> assignWorkAndFilter(@PathVariable Long id, @RequestBody Worker assignedUser, @RequestParam String newStatus) {
        Optional<WorkListEntity> optionalWork = workListRepository.findById(id);

        if (optionalWork.isPresent()) {
            WorkListEntity work = optionalWork.get();
            work.setWorker(assignedUser);

            try {
                WorkStatus workStatus = WorkStatus.valueOf(newStatus);
                work.setWorkStatus(workStatus);
                workListRepository.save(work);
                List<WorkListEntity> filteredList = workListRepository.findByWorkStatus(workStatus);
                return ResponseEntity.ok("Work assigned and filtered successfully.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
