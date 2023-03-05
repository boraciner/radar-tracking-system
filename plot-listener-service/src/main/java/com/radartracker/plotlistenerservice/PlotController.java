package com.radartracker.plotlistenerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlotController {

	@Autowired
	private PlotRepository plotRepository;
	
	@GetMapping("/plots")
	public String getPlots(Plot plot) {
		plotRepository.save(plot);
		return "get plots is called :"+plot;
	}
	
}
