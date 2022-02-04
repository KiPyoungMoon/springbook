package springbook.sql;

import springbook.sql.exception.SqlRetrievalFailtureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailtureException;
}
