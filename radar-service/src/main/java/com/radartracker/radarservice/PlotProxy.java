package com.radartracker.radarservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="plot-listener-service")
public interface PlotProxy {

	@PostMapping("/plots")
	public Plot sendPlot(@RequestBody Plot plot);
	
}
