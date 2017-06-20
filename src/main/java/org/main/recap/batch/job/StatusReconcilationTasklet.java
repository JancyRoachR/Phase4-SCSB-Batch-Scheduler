package org.main.recap.batch.job;

import org.main.recap.batch.service.StatusReconciliationService;
import org.main.recap.batch.service.UpdateJobDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * Created by hemalathas on 1/6/17.
 */
public class StatusReconcilationTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(AccessionReconcilationTasklet.class);


    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Value("${scsb.solr.client.url}")
    String solrClientUrl;

    @Autowired
    private UpdateJobDetailsService updateJobDetailsService;

    @Autowired
    private StatusReconciliationService statusReconciliationService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Executing Status Reconciliation");
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        long jobInstanceId = jobExecution.getJobInstance().getInstanceId();
        String jobName = jobExecution.getJobInstance().getJobName();
        Date createdDate = jobExecution.getCreateTime();
        updateJobDetailsService.updateJob(solrClientUrl, jobName, createdDate, jobInstanceId);

        String status = statusReconciliationService.statusReconcilation(scsbCircUrl);
        logger.info("Periodic LAS item status reconciliation status : {}", status);
        return RepeatStatus.FINISHED;
    }
}
