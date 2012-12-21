package com.sap.pto.jobs;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.ConnectivityAdapter;
import com.sap.pto.importers.OptaMatchImporter;
import com.sap.pto.importers.OptaPreviewImporter;
import com.sap.pto.importers.OptaStatsImporter;
import com.sap.pto.importers.OptaTeamImporter;

@DisallowConcurrentExecution
public class ImporterJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(ImporterJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Starting data import.");

        try {
            String content = ConnectivityAdapter.fetchContent("opta", "filelist");

            String[] files = StringUtils.split(content);
            for (int i = 0; i < files.length; i++) {
                content = ConnectivityAdapter.fetchContent("opta", files[i]);

                try {
                    if (files[i].endsWith("-results.xml")) {
                        new OptaMatchImporter().importData(content);
                    } else if (files[i].endsWith("-squads.xml")) {
                        new OptaTeamImporter().importData(content);
                    } else if (files[i].endsWith("-matchpreview.xml")) {
                        new OptaPreviewImporter().importData(content);
                    } else if (files[i].contains("seasonstats-")) {
                        new OptaStatsImporter().importData(content);
                    }
                } catch (IOException e) {
                    logger.error("Could not import file '" + files[i] + "'.", e);
                }
            }
        } catch (ClientProtocolException e) {
            logger.error("Could not get connect to server.", e);
        } catch (IOException e) {
            logger.error("Could not connect to server.", e);
        }

        logger.info("Data import is done.");
    }
}
