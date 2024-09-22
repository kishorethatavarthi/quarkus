package org.acme;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.scheduler.Scheduled;
import org.quartz.*;

@ApplicationScoped
public class ScheduledApp {

    @Inject
    org.quartz.Scheduler quartz;

//    void onStart(@Observes StartupEvent event) throws SchedulerException {
//        JobDetail job = JobBuilder.newJob(MyJob.class)
//                .withIdentity("myJob", "myGroup")
//                .build();
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("myTrigger", "myGroup")
//                .startNow()
//                .withSchedule(
//                        SimpleScheduleBuilder.simpleSchedule()
//                                .withIntervalInSeconds(10)
//                                .repeatForever())
//                .build();
//        quartz.scheduleJob(job, trigger);
//    }

    @Transactional
    void performTask() {
        Tasks task = new Tasks();
        task.persist();
    }

    // A new instance of MyJob is created by Quartz for every job execution
//    public static class MyJob implements Job {
//
//        @Inject
//        ScheduledApp scheduledApp;
//
//        public void execute(JobExecutionContext context) throws JobExecutionException {
//            scheduledApp.performTask();
//        }
//
//    }
}
