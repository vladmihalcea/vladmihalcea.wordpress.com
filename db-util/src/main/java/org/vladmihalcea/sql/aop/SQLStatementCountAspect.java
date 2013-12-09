/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vladmihalcea.sql.aop;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.Assert;
import org.vladmihalcea.sql.SQLStatementCount;
import org.vladmihalcea.sql.exception.SQLStatementCountHolderAlreadyInitializedException;
import org.vladmihalcea.sql.exception.SQLStatementCountMismatchException;
import org.vladmihalcea.util.ReflectionUtils;

/**
 * SQLStatementCountAspect - Aspect to check executed SQL statements.
 *
 * @author Vlad Mihalcea
 */
@Aspect
public class SQLStatementCountAspect {

    @Around("@annotation(org.vladmihalcea.sql.SQLStatementCount)")
    public Object count(ProceedingJoinPoint pjp) throws Throwable {
        SQLStatementCount countAnnotation = ReflectionUtils.getAnnotation(pjp, SQLStatementCount.class);
        return (countAnnotation != null) ? proceed(pjp, countAnnotation) : pjp.proceed();
    }

    private Object proceed(ProceedingJoinPoint pjp, SQLStatementCount countAnnotation) throws Throwable {
        int select = countAnnotation.select();
        int insert = countAnnotation.insert();
        int update = countAnnotation.update();
        int delete = countAnnotation.delete();
        Assert.isTrue(select >= 0, "@SQLStatementCount{select} should be greater or equal to 0!");
        Assert.isTrue(insert >= 0, "@SQLStatementCount{insert} should be greater or equal to 0!");
        Assert.isTrue(update >= 0, "@SQLStatementCount{update} should be greater or equal to 0!");
        Assert.isTrue(delete >= 0, "@SQLStatementCount{delete} should be greater or equal to 0!");
        Object result;
        try {
            if(!QueryCountHolder.getDataSourceNames().isEmpty()) {
                throw new SQLStatementCountHolderAlreadyInitializedException("QueryCountHolder shouldn't have been activated!", countAnnotation, QueryCountHolder.getGrandTotal());
            }
            result = pjp.proceed();
            QueryCount queryCount = QueryCountHolder.getGrandTotal();
            if(select != queryCount.getSelect()) {
                throw new SQLStatementCountMismatchException("Expected " + select + " selects but recorded " + queryCount.getSelect() + " instead!", countAnnotation, queryCount);
            }
            if(insert != queryCount.getInsert()) {
                throw new SQLStatementCountMismatchException("Expected " + insert + " inserts but recorded " + queryCount.getInsert() + " instead!", countAnnotation, queryCount);
            }
            if(update != queryCount.getUpdate()) {
                throw new SQLStatementCountMismatchException("Expected " + update + " updates but recorded " + queryCount.getUpdate() + " instead!", countAnnotation, queryCount);
            }
            if(delete != queryCount.getDelete()) {
                throw new SQLStatementCountMismatchException("Expected " + delete + " deletes but recorded " + queryCount.getDelete() + " instead!", countAnnotation, queryCount);
            }
        } finally {
            QueryCountHolder.clear();
        }
        return result;
    }
}
