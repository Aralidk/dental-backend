package backend.dental.controller;
import backend.dental.enums.Role;
import backend.dental.worker.Worker;
import backend.dental.worker.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final WorkerRepository workerRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PutMapping("/user/update-role/{id}")
    public ResponseEntity<Worker> updateUserRole(@PathVariable Long id, @RequestParam("role") Role role) {
        Optional<Worker> userOptional = workerRepository.findById(Math.toIntExact(id));
        if (userOptional.isPresent()) {
            Worker user = userOptional.get();
            user.setRole(role);
            Worker updatedUser = workerRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/update-confirm/{id}")
    public ResponseEntity<String> updateConfirmStatus(@PathVariable int id, @RequestBody boolean confirmStatus) {
        Optional<Worker> userOptional = workerRepository.findById(id);

        if (userOptional.isPresent()) {
            Worker user = userOptional.get();
            user.setConfirm(confirmStatus);

            if (!confirmStatus) {
                boolean deleted = updateConfirmStatusAndDeleteIfFalse(id, confirmStatus);
                if (!deleted) {
                    return ResponseEntity.badRequest().body("Failed to delete unconfirmed user.");
                }
            }

            workerRepository.save(user);

            return ResponseEntity.ok("Confirm status updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Transactional
    public boolean updateConfirmStatusAndDeleteIfFalse(int id, boolean confirmStatus) {
        if (!confirmStatus) {
            int deletedRows = workerRepository.deleteByConfirmStatusIsFalseAndId(id);
            return deletedRows > 0;
        } else {
            Optional<Worker> userOptional = workerRepository.findById(id);
            if (userOptional.isPresent()) {
                Worker user = userOptional.get();
                user.setConfirm(confirmStatus); // Assuming there's a setter for 'confirm' property
                workerRepository.save(user);
                return true;
            }
            return false;
        }
    }



    @GetMapping("/user/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = Arrays.stream(Role.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Worker>> getAllUsers() {
        List<Worker> users = workerRepository.findAll();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/users/unconfirmed")
    public ResponseEntity<List<Worker>> getUnconfirmedUsers() {
        List<Worker> unconfirmedUsers = workerRepository.findByConfirmStatusFalse();

        if (!unconfirmedUsers.isEmpty()) {
            return ResponseEntity.ok(unconfirmedUsers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
