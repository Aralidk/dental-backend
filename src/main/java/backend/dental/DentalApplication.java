package backend.dental;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentalApplication.class, args);
	}

}
