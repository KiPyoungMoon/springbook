package springbook.sql.exception;

public class SqlRetrievalFailtureException extends RuntimeException {
    public SqlRetrievalFailtureException(String message) {
        super(message);
    }

    public SqlRetrievalFailtureException(String message, Throwable cause) {
        super(message, cause);
    }
}
