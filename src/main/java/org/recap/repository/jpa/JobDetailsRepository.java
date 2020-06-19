package org.recap.repository.jpa;

import org.recap.model.jpa.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 4/4/17.
 */
public interface JobDetailsRepository extends BaseRepository<JobEntity> {

    /**
     * To find the job entity for the given job name.
     *
     * @param jobName the job name
     * @return the job entity
     */
    JobEntity findByJobName(String jobName);
}
