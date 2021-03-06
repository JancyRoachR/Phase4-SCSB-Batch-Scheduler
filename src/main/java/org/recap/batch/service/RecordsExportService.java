package org.recap.batch.service;

import org.recap.ScsbConstants;
import org.recap.spring.ApplicationContextProvider;
import org.recap.spring.PropertyValueProvider;
import org.recap.util.JobDataParameterUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service
public class RecordsExportService {

    @Autowired
    JobDataParameterUtil jobDataParameterUtil;
    
    @Autowired
    private CommonService commonService;

    @Autowired
    private PropertyUtil propertyUtil;

    
    /**
     * This method makes a rest call to scsb etl microservice to initiate the process of deleted records export for Columbia.
     *
     * @param scsbEtlUrl    the scsb etl url
     * @return status of deleted records export for CUL
     */
    public String exportRecords(String scsbEtlUrl, String jobName, Date createdDate, String exportStringDate, String exportInstitution) {
        HttpEntity httpEntity = commonService.getHttpEntity();
        Map<String, String> requestParameterMap = jobDataParameterUtil.buildJobRequestParameterMap(jobName);

        ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();
        String emailTo = applicationContext.getBean(PropertyValueProvider.class).getProperty(exportInstitution.toLowerCase() + ".email.data.dump.to");

        requestParameterMap.put(ScsbConstants.EMAIL_TO_ADDRESS, emailTo);
        commonService.setRequestParameterMap(requestParameterMap, exportStringDate, jobDataParameterUtil, createdDate);
        ResponseEntity<String> responseEntity = commonService.getRestTemplate().exchange(scsbEtlUrl + ScsbConstants.DATA_EXPORT_ETL_URL, HttpMethod.GET, httpEntity, String.class, requestParameterMap);
        return responseEntity.getBody();
    }
}
