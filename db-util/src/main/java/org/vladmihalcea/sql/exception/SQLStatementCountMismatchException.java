package org.vladmihalcea.sql.exception;

import net.ttddyy.dsproxy.QueryCount;
import org.vladmihalcea.sql.SQLStatementCount;

/**
 * SQLStatementCountMismatchException - Thrown whenever there is a mismatch between expected statements count and
 *                                      the ones being executed.
 *
 * @author Vlad Mihalcea
 */
public class SQLStatementCountMismatchException extends RuntimeException {

    private final SQLStatementCount sqlStatementCount;
    private final QueryCount queryCount;

    public SQLStatementCountMismatchException(SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCountMismatchException(String message, SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        super(message);
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCountMismatchException(String message, Throwable cause, SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        super(message, cause);
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCountMismatchException(Throwable cause, SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        super(cause);
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCount getSqlStatementCount() {
        return sqlStatementCount;
    }

    public QueryCount getQueryCount() {
        return queryCount;
    }
}
