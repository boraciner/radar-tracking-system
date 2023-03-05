package com.radartracker.plotlistenerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlotController {

	@Autowired
	private PlotRepository plotRepository;
	
	@PostMapping("/plots")
	public void getPlots(@RequestBody Plot plot) {
		System.out.println("Post plots is called :"+plot);
		
		plotRepository.save(plot);
	}
	
}
