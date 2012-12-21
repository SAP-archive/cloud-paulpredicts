package com.sap.pto.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.paul.AveragePaul;

/**
 * This class is used for executing a job which sets predictions for fixtures on behalf of Paul.
 */
@DisallowConcurrentExecution
public class PaulPredictionJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AveragePaul.predictFixtures(FixtureDAO.getFuture());
    }
}
