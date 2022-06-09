package com.vp.chatroom.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
@ResponseStatus(HttpStatus.OK)
public class RootEndpoint {

	public String getRoot(Model model, HttpServletRequest request) {
		return "index";
	}
}
