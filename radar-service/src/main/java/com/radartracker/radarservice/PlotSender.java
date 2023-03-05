package com.radartracker.radarservice;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlotSender {
	private static final Logger log = LoggerFactory.getLogger(PlotSender.class);

	@Scheduled(fixedRate = 1000)
	public void reportCurrentTime() {
		log.info("The time is now {}", LocalTime.now());
	}
}
