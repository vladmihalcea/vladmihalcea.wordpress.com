package org.vladmihalcea.sql.exception;

import net.ttddyy.dsproxy.QueryCount;
import org.vladmihalcea.sql.SQLStatementCount;

/**
 * SQLStatementCountHolderAlreadyInitializedException - Thrown whenever the QueryCountHolder is already initialized.
 *
 * @author Vlad Mihalcea
 */
public class SQLStatementCountHolderAlreadyInitializedException extends RuntimeException {

    private final SQLStatementCount sqlStatementCount;
    private final QueryCount queryCount;

    public SQLStatementCountHolderAlreadyInitializedException(SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCountHolderAlreadyInitializedException(String message, SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        super(message);
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCountHolderAlreadyInitializedException(String message, Throwable cause, SQLStatementCount sqlStatementCount, QueryCount queryCount) {
        super(message, cause);
        this.sqlStatementCount = sqlStatementCount;
        this.queryCount = queryCount;
    }

    public SQLStatementCountHolderAlreadyInitializedException(Throwable cause, SQLStatementCount sqlStatementCount, QueryCount queryCount) {
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
