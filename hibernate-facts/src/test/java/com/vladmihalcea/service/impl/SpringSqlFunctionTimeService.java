package com.vladmihalcea.service.impl;

import com.vladmihalcea.service.TimeService;
import org.springframework.jdbc.object.SqlFunction;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;

/**
 * SpringSqlFunctionTimeService - SpringJdbc TimeService
 *
 * @author Vlad Mihalcea
 */
@Repository
public class SpringSqlFunctionTimeService implements TimeService {

    @Resource
    private DataSource localTransactionDataSource;

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatTimestamp() {
        SqlFunction<String> sqlFunction =
                new SqlFunction<String>(localTransactionDataSource, "{ ? = call FORMAT_TIMESTAMP(?) }", new int[]{Types.TIMESTAMP});
        return (String) sqlFunction.runGeneric(new Date[]{new Date()});
    }
}
