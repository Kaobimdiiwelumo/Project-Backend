package Project.FinalYear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "Project.FinalYear.Repository") // Provide the correct package name
public class  FinalYearApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalYearApplication.class, args);
	}

}
