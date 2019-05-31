package com.zsx.service.impl;

import org.springframework.stereotype.Service;

import com.zsx.service.TestService;

@Service
public class TestServiceImpl implements TestService {

	@Override
	public String getName(String name) {
//		Integer.valueOf(name);
		return name;
	}

}
