package com.radartracker.plotlistenerservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlotController {

	@GetMapping("/plots")
	public String getPlots() {
		
		return "get plots is called!!";
		
	}
	
}
