package com.meteorinc.thegateway.application.jobs;

import com.meteorinc.thegateway.application.event.EventService;
import com.meteorinc.thegateway.domain.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventSchedulerJob {

    EventService eventService;

    @Scheduled(fixedDelay = 30000)
    public void startEvent(){
        log.info("Starting event starter job...");

        final var eligibleEvents =
                eventService.findEventsByStatusAndStartingDateLessThan(EventStatus.SCHEDULED,LocalDateTime.now());

        if(!eligibleEvents.isEmpty()){

            log.info("Was found {} eligible for start up", eligibleEvents.size());

            eligibleEvents.forEach( event -> {
                event.setStatus(EventStatus.IN_PROGRESS);
            });

            eventService.save(eligibleEvents);
        }

        log.info("Finishing event starter job...");
    }

    @Scheduled(fixedDelay = 30000)
    public void startFinisher(){
        log.info("Starting event finisher job...");

        final var eligibleEvents =
                eventService.findEventsByStatusAndFinishingDateLessThan(EventStatus.IN_PROGRESS,LocalDateTime.now());

        if(!eligibleEvents.isEmpty()){

            log.info("Was found {} eligible for finishing", eligibleEvents.size());
            eligibleEvents.forEach( event -> {
                event.setStatus(EventStatus.FINISHED);
            });

            eventService.save(eligibleEvents);
        }
        log.info("Finishing event finisher job...");
    }
}
