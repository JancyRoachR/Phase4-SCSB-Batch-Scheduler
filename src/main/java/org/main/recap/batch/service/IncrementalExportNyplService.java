package org.main.recap.batch.service;

import org.apache.commons.lang.StringUtils;
import org.main.recap.util.JobDataParameterUtil;
import org.main.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

/**
 * Created by rajeshbabuk on 23/6/17.
 */
@Service
public class IncrementalExportNyplService {

    private static final Logger logger = LoggerFactory.getLogger(IncrementalExportCulService.class);

    @Value("${data.dump.email.nypl.to}")
    private String dataDumpEmailNyplTo;

    @Autowired
    JobDataParameterUtil jobDataParameterUtil;

    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * This method makes a rest call to scsb etl microservice to initiate the process of incremental export for NYPL.
     *
     * @param scsbEtlUrl    the scsb etl url
     * @return status of incremental export for NYPL
     */
    public String incrementalExportNypl(String scsbEtlUrl, String jobName, Date createdDate, String exportStringDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
        HttpEntity httpEntity = new HttpEntity<>(headers);
        Map<String, String> requestParameterMap = jobDataParameterUtil.buildJobRequestParameterMap(jobName);
        requestParameterMap.put(RecapConstants.EMAIL_TO_ADDRESS, dataDumpEmailNyplTo);
        if (StringUtils.isBlank(exportStringDate)) {
            requestParameterMap.put(RecapConstants.DATE, jobDataParameterUtil.getDateFormatStringForExport(createdDate));
        } else {
            requestParameterMap.put(RecapConstants.DATE, exportStringDate);
        }
        ResponseEntity<String> responseEntity = getRestTemplate().exchange(scsbEtlUrl + RecapConstants.DATA_EXPORT_ETL_URL, HttpMethod.GET, httpEntity, String.class, requestParameterMap);
        return responseEntity.getBody();
    }
}
