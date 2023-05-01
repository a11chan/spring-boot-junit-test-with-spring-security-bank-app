package shop.mtcoding.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
//        ConfigurableApplicationContext ac = SpringApplication.run(BankApplication.class, args);
//        String[] namesInIoc = ac.getBeanDefinitionNames();
//        for (String name : namesInIoc) {
//            System.out.println("name = " + name);
//        }
    }

}
