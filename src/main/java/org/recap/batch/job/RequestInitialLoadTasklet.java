package org.recap.batch.job;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.batch.service.RequestInitialLoadService;
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

/**
 * Created by hemalathas on 16/6/17.
 */
public class RequestInitialLoadTasklet extends JobCommonTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(RequestInitialLoadTasklet.class);

    @Autowired
    private RequestInitialLoadService requestInitialLoadService;

    /**
     * This method starts the execution of Request Initial Loas.
     *
     * @param contribution StepContribution
     * @param chunkContext ChunkContext
     * @return RepeatStatus
     * @throws Exception Exception Class
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Executing Request Initial Load..........");
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        try {
            updateJob(jobExecution, "RequestInitialLoadTasklet", Boolean.FALSE);
            String resultStatus = requestInitialLoadService.requestInitialLoad(scsbCircUrl);
            logger.info("Request Initial Load status : {}", resultStatus);
            setExecutionContext(executionContext, stepExecution, resultStatus);
        } catch (Exception ex) {
            logger.error("{} {}", ScsbCommonConstants.LOG_ERROR, ExceptionUtils.getMessage(ex));
            executionContext.put(ScsbConstants.JOB_STATUS, ScsbConstants.FAILURE);
            executionContext.put(ScsbConstants.JOB_STATUS_MESSAGE, ExceptionUtils.getMessage(ex));
            stepExecution.setExitStatus(new ExitStatus(ScsbConstants.FAILURE, ExceptionUtils.getFullStackTrace(ex)));
        }
        return RepeatStatus.FINISHED;
    }

}
