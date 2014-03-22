package com.arunpjohny.webapp.birt.config.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import freemarker.template.TemplateException;

@Controller
public class HomeController {

	@RequestMapping(value = { "/", "/home" })
	public ModelAndView test1(HttpServletRequest request) throws IOException,
			TemplateException {
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView("/WEB-INF/views/home", model);
	}

}

