package com.vladmihalcea.service.impl;

import com.vladmihalcea.jooq.schema.routines.FormatTimestamp;
import com.vladmihalcea.service.TimeService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * SpringSqlFunctionTimeService - SpringJdbc TimeService
 *
 * @author Vlad Mihalcea
 */
@Repository
public class JooqSqlFunctionTimeService implements TimeService {

    @Autowired
    private DSLContext localTransactionJooqContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatTimestamp() {
        FormatTimestamp sqlFunction = new FormatTimestamp();
        sqlFunction.setInTime(new Timestamp(System.currentTimeMillis()));
        sqlFunction.execute(localTransactionJooqContext.configuration());
        return sqlFunction.getReturnValue();
    }
}
