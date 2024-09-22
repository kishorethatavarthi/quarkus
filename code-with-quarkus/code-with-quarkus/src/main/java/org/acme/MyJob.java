package org.acme;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MyJob implements Job{

@PersistenceContext
private EntityManager em;

@Override
@Transactional
public void execute(JobExecutionContext context) throws JobExecutionException {
    // Insert data into the table using EntityManager
    ScheduledData scheduledData = new ScheduledData();
    scheduledData.setEnvName(context.getMergedJobDataMap().getString("envName"));
    scheduledData.setScheduledTime(context.getMergedJobDataMap().getString("scheduledTime"));
    em.persist(scheduledData);
}
}
