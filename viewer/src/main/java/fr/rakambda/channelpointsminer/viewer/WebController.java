package fr.rakambda.channelpointsminer.viewer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class WebController{
	@GetMapping("/")
	public ModelAndView socialForm(){
		return new ModelAndView("home");
	}
}
