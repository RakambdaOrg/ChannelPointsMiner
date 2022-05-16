package fr.raksrinana.channelpointsminer.viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.raksrinana.channelpointsminer")
public class ViewerApplication{
    public static void main(String[] args){
        SpringApplication.run(ViewerApplication.class, args);
    }
}
