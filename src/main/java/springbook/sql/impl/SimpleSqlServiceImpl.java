package springbook.sql.impl;

import java.util.Map;

import springbook.sql.SqlService;
import springbook.sql.exception.SqlRetrievalFailtureException;

public class SimpleSqlServiceImpl implements SqlService {

    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailtureException {
        String sql = this.sqlMap.get(key);

        if (sql == null)
            throw new SqlRetrievalFailtureException(key + "에 대한 메시지를 찾을 수 없습니다.");
        return sql;
    }

}
