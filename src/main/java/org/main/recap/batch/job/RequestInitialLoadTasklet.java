package org.main.recap.batch.job;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.main.recap.RecapConstants;
import org.main.recap.batch.service.RequestInitialLoadService;
import org.main.recap.batch.service.UpdateJobDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * Created by hemalathas on 16/6/17.
 */
public class RequestInitialLoadTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(RequestInitialLoadTasklet.class);

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Value("${scsb.solr.client.url}")
    String solrClientUrl;

    @Autowired
    private RequestInitialLoadService requestInitialLoadService;

    @Autowired
    private UpdateJobDetailsService updateJobDetailsService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Executing Request Initial Load..........");
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        try {
            long jobInstanceId = jobExecution.getJobInstance().getInstanceId();
            String jobName = jobExecution.getJobInstance().getJobName();
            Date createdDate = jobExecution.getCreateTime();
            updateJobDetailsService.updateJob(solrClientUrl, jobName, createdDate, jobInstanceId);
            String resultStatus = requestInitialLoadService.requestInitialLoad(scsbCircUrl);
            logger.info("Request Initial Load status : {}", resultStatus);
            if (!StringUtils.containsIgnoreCase(resultStatus, RecapConstants.SUCCESS)) {
                executionContext.put(RecapConstants.JOB_STATUS, RecapConstants.FAILURE);
                executionContext.put(RecapConstants.JOB_STATUS_MESSAGE, resultStatus);
                stepExecution.setExitStatus(new ExitStatus(RecapConstants.FAILURE, resultStatus));
            } else {
                executionContext.put(RecapConstants.JOB_STATUS, RecapConstants.SUCCESS);
                executionContext.put(RecapConstants.JOB_STATUS_MESSAGE, resultStatus);
                stepExecution.setExitStatus(new ExitStatus(RecapConstants.SUCCESS, resultStatus));
            }
        } catch (Exception ex) {
            logger.error(RecapConstants.LOG_ERROR, ExceptionUtils.getMessage(ex));
            executionContext.put(RecapConstants.JOB_STATUS, RecapConstants.FAILURE);
            executionContext.put(RecapConstants.JOB_STATUS_MESSAGE, ExceptionUtils.getMessage(ex));
            stepExecution.setExitStatus(new ExitStatus(RecapConstants.FAILURE, ExceptionUtils.getFullStackTrace(ex)));
        }
        return RepeatStatus.FINISHED;
    }

}
