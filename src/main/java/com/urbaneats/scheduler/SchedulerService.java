package com.urbaneats.scheduler;

import com.urbaneats.service.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Service
public class SchedulerService {

    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher eventPublisher;

    public SchedulerService( ApplicationEventPublisher eventPublisher) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        this.taskScheduler = scheduler;
        this.eventPublisher = eventPublisher;
    }

    public ScheduledFuture<?> scheduleTask(Long orderId, int delayInMinutes) {
        return taskScheduler.schedule(() -> {
            System.out.println("Order status updated after " + delayInMinutes + " minutes at: " + Instant.now());
//            orderService.updateOrderStatus(orderId);
            eventPublisher.publishEvent(new OrderStatusUpdateEvent(this, orderId));
        }, Instant.now().plusSeconds(delayInMinutes * 60L));
    }
}
