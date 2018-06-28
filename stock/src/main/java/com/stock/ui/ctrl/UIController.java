package com.stock.ui.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UIController {
	
	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("/main/index");
	}
	
}
