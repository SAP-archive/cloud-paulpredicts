package com.sap.pto.startup;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.joda.time.DateTimeZone;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.User;
import com.sap.pto.jobs.ImporterJob;
import com.sap.pto.jobs.PaulPredictionJob;
import com.sap.pto.util.Consts;
import com.sap.pto.util.SecurityUtil;
import com.sap.pto.util.configuration.ConfigDBAdapter;
import com.sap.pto.util.configuration.ConfigUtil;

public class AppInitializer implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(AppInitializer.class);
    private static Scheduler scheduler;

    public AppInitializer() {
    }

    @Override
    public void contextInitialized(ServletContextEvent context) {
        logger.info("Initializing Application");

        DateTimeZone.setDefault(DateTimeZone.UTC);
        initScheduler();
        initDefaultUsers();

        // trigger data importing and predicting
        triggerJob("Importer");
        triggerJob("PaulPrediction");

        logger.info("Initialization Done");
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                logger.error("Job scheduler could not be shut down.", e);
            }
        }
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public static void initConfiguration() {
        ConfigUtil.setDBAdapter(new ConfigDBAdapter());
    }

    /**
     * Schedules all background jobs
     * 
     * Important: Group names must be equal to the job name currently for
     * manually triggering jobs via REST API
     */
    private void initScheduler() {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            scheduler = sf.getScheduler();
            scheduler.start();

            JobDetail job = newJob(ImporterJob.class).withIdentity("Importer", "Importer").build();
            CronTrigger trigger = newTrigger().withIdentity("cronTrigger", "Importer").withSchedule(cronSchedule("0 0 * * * ?")).build();
            scheduler.scheduleJob(job, trigger);

            job = newJob(PaulPredictionJob.class).withIdentity("PaulPrediction", "PaulPrediction").build();
            trigger = newTrigger().withIdentity("cronTrigger", "PaulPrediction").withSchedule(cronSchedule("0 */15 * * * ?")).build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.error("Job scheduler could not be started.", e);
        }
    }

    private static void triggerJob(String name) {
        try {
            scheduler.triggerJob(new JobKey(name, name));
            logger.info("Job '" + name + "' has been triggered.");
        } catch (SchedulerException e) {
            logger.error("Could not trigger job '" + name + "'.", e);
        }
    }

    public static void initDefaultUsers() {
        if (new UserDAO().getCount() == 0) {
            logger.info("Creating default users: admin, user, paul, hana");

            UserDAO.save(new User("admin", "admin@test.com", SecurityUtil.getPasswordHash("admin", "admin"), "user,admin"));
            UserDAO.save(new User("user", "user@test.com", SecurityUtil.getPasswordHash("user", "user"), "user"));
            UserDAO.save(new User(Consts.PAUL, "paul@test.com", SecurityUtil.getPasswordHash(Consts.PAUL, "paul"), "user"));
            UserDAO.save(new User(Consts.HANA, "hana@test.com", SecurityUtil.getPasswordHash(Consts.HANA, "hana"), "user"));
        }
    }
}
