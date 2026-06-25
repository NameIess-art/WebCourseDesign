package com.mall.mapper.support;

import java.util.Map;

public class GenericSqlProvider {

    public String sql(Map<String, Object> parameters) {
        return (String) parameters.get("sql");
    }
}
