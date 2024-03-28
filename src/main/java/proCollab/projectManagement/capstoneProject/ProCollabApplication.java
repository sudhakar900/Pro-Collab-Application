package proCollab.projectManagement.capstoneProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "proCollab.projectManagement.capstoneProject" })
public class ProCollabApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProCollabApplication.class, args);
    }

}