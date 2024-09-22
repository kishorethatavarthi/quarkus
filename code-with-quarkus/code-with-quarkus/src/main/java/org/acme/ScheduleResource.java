package org.acme;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Path("/schedule")
public class ScheduleResource {

    @Inject
    Scheduler scheduler;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String scheduleData(ScheduledData data) {

        String environmentName = data.getEnvName();
        String scheduledTime = data.getScheduledTime();

        // Generate a unique job key using UUID
        String jobName = "myJob_" + environmentName + "_" + UUID.randomUUID().toString();
        JobKey jobKey = new JobKey(jobName, "myGroup");

        // Create JobDetail
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                .withIdentity(jobKey)
                .build();

        // Create Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .startAt(Date.from(Instant.parse(data.getScheduledTime())))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        // Set scheduledTime in JobDataMap (optional)
        jobDetail.getJobDataMap().put("scheduledTime", data.getScheduledTime());

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            return "Data scheduled successfully";
        } catch (Exception e) {
            return "Error scheduling data: " + e.getMessage();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScheduledTaskInfo> getScheduledTasks() {
        List<ScheduledTaskInfo> scheduledTasks = new ArrayList<>();

        try {
            List<JobKey> jobKeys = (List<JobKey>) scheduler.getJobKeys(GroupMatcher.anyGroup());
            for (JobKey jobKey : jobKeys) {
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    ScheduledTaskInfo taskInfo = new ScheduledTaskInfo();
                    taskInfo.setJobName(jobKey.getName());
                    taskInfo.setGroupName(jobKey.getGroup());
                    taskInfo.setTriggerName(trigger.getKey().getName());
                    taskInfo.setTriggerGroup(trigger.getKey().getGroup());
                    taskInfo.setScheduledTime(trigger.getStartTime().toString()); // Adjust as needed
                    //taskInfo.setState(scheduler.getJobDetail(jobKey).getState().name());
                    scheduledTasks.add(taskInfo);
                }
            }
        } catch (SchedulerException e) {
            // Handle exceptions
        }

        return scheduledTasks;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScheduledTaskInfo> getScheduledTasksByGroup(String groupName) {
        List<ScheduledTaskInfo> scheduledTasks = new ArrayList<>();

        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                String jobName = jobDetail.getKey().getName();
                //String jobState = jobDetail.getState().name();

                // Retrieve triggers associated with the job
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    ScheduledTaskInfo taskInfo = new ScheduledTaskInfo();
                    taskInfo.setJobName(jobName);
                    taskInfo.setGroupName(jobKey.getGroup());
                    taskInfo.setTriggerName(trigger.getKey().getName());
                    taskInfo.setTriggerGroup(trigger.getKey().getGroup());
                    taskInfo.setScheduledTime(trigger.getStartTime().toString());
                    //taskInfo.setState(jobState);
                    scheduledTasks.add(taskInfo);
                }
            }
        } catch (SchedulerException e) {
            // Handle exceptions
        }

        return scheduledTasks;
    }

    public void deleteJob(String jobName, String groupName) {
        try {
            JobKey jobKey = new JobKey(jobName, groupName);
            scheduler.deleteJob(jobKey);
            System.out.println("Job deleted successfully");
        } catch (SchedulerException e) {
            // Handle exceptions
        }
    }

    public void updateJob(String jobName, String groupName, String newScheduledTime) {
        try {
            // Retrieve the existing job
            JobKey jobKey = new JobKey(jobName, groupName);
            JobDetail existingJobDetail = scheduler.getJobDetail(jobKey);

            // Delete the existing job
            scheduler.deleteJob(jobKey);

            // Create a new JobDetail with updated data
            JobDetail newJobDetail = JobBuilder.newJob(MyJob.class)
                    .withIdentity(jobName, groupName)
                    .build();

            // Create a new Trigger with the updated scheduled time
            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, groupName)
                    .startAt(Date.from(Instant.parse(newScheduledTime)))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow())
                    .build();

            // Schedule the new job
            scheduler.scheduleJob(newJobDetail, newTrigger);
        } catch (Exception e) {
            // Handle exceptions
        }
    }
}
