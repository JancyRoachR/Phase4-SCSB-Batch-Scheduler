package org.recap.batch.job;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.batch.service.DailyReconcilationService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anitha V on 14/7/20.
 */

public class DailyReconcilationTaskletUT extends BaseTestCase {

    @Mock
    DailyReconcilationService dailyReconcilationService;

    @Value("${" + PropertyKeyConstants.SCSB_CIRC_URL + "}")
    String scsbCircUrl;

    @Mock
    DailyReconcilationTasklet dailyReconcilationTasklet;

    @Test
    public void testexecute_Exception() throws Exception {
        StepContribution contribution = new StepContribution(new StepExecution("DailyReconcilationStep", new JobExecution(new JobInstance(123L, "DailyLASTransactionReconciliation"),new JobParameters())));
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.setCommitCount(2);
        ChunkContext context = new ChunkContext(new StepContext(execution));
        Mockito.when(dailyReconcilationService.dailyReconcilation(scsbCircUrl)).thenThrow(new NullPointerException());
        Mockito.when(dailyReconcilationTasklet.execute(contribution,context)).thenCallRealMethod();
        RepeatStatus status = dailyReconcilationTasklet.execute(contribution,context);
        assertNotNull(status);
        assertEquals(RepeatStatus.FINISHED,status);
    }

    @Test
    public void testexecute() throws Exception {
        StepContribution contribution = new StepContribution(new StepExecution("DailyReconcilationStep", new JobExecution(new JobInstance(25L, "DailyLASTransactionReconciliation"),new JobParameters())));
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.setCommitCount(2);
        ChunkContext context = new ChunkContext(new StepContext(execution));
        ReflectionTestUtils.setField(dailyReconcilationTasklet,"dailyReconcilationService",dailyReconcilationService);
        Mockito.when(dailyReconcilationService.dailyReconcilation(scsbCircUrl)).thenReturn(ScsbConstants.SUCCESS);
        Mockito.when(dailyReconcilationTasklet.execute(contribution,context)).thenCallRealMethod();
        RepeatStatus status = dailyReconcilationTasklet.execute(contribution,context);
        assertNotNull(status);
        assertEquals(RepeatStatus.FINISHED,status);
    }


}
