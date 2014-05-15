package com.vladmihalcea.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DefaultUserRatingManager -
 *
 * @author Vlad Mihalcea
 */
@Service
public class DefaultCompanyManager implements CompanyManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCompanyManager.class);

    @Autowired
    private CompanyDAO companyDAO;

    @Override
    @Transactional
    public void updateAllUsers() {
        List<String> names = companyDAO.getNames();
        for (String name : names) {
            LOG.info("Company {}", name);
        }
    }
}
