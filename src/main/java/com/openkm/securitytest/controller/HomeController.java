package com.openkm.securitytest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping({"/"})
	public String index() {
		return "redirect:index.html";
	}
}
