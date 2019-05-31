package com.zsx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zsx.service.TestService;

@Controller
public class TestController {
	
	@Autowired
	private TestService testService;
	
	
	@RequestMapping("/home")
	public String aaaa(){
		return "index";
	}
	
	@RequestMapping("/demo")
	@ResponseBody
	public String test(String name){
		testService.getName(name);
		return "fsdf" + name;
	}

}
