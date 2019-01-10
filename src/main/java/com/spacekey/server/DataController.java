package com.spacekey.server;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.spacekey.algorithm.Methods;
import com.spacekey.algorithm.global.Point;
import com.spacekey.algorithm.Link;

@RestController
@RequestMapping("data")
public class DataController {

	@GetMapping("")
	String mck() {
		return "Yes";
	}
}