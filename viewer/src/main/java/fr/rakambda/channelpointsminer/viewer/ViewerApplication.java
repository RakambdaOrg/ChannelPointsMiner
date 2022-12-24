package fr.rakambda.channelpointsminer.viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.rakambda.channelpointsminer")
public class ViewerApplication{
    public static void main(String[] args){
        SpringApplication.run(ViewerApplication.class, args);
    }
}
