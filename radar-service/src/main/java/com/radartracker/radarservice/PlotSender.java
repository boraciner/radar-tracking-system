package com.radartracker.radarservice;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlotSender {
	private static final Logger log = LoggerFactory.getLogger(PlotSender.class);

	
	@Autowired
	private PlotProxy plotProxy;
	private double x = 0;
	
	@Scheduled(fixedRate = 1000)
	public void reportCurrentTime() {
		plotProxy.sendPlot(new Plot(x,
				0.3 * Math.pow((x-1.6),2) + 0.7, LocalTime.now().toSecondOfDay()));
		x++;
	}
}
