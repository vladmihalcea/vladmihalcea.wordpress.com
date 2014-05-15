package com.vladmihalcea.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * SchedulerTest -
 *
 * @author Vlad Mihalcea
 */
@Service
public class CompanyScheduler implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyScheduler.class);

    @Autowired
    private CompanyManager companyManager;

    private volatile boolean enabled = true;

    @Override
    public void destroy() throws Exception {
        enabled = false;
    }

    @Scheduled(fixedRate = 100)
    public void run() {
        if (enabled) {
            LOG.info("Run scheduler");
            companyManager.updateAllUsers();
        }
    }
}
