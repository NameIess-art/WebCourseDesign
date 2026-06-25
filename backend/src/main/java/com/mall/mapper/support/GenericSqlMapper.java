package com.mall.mapper.support;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface GenericSqlMapper {

    @SelectProvider(type = GenericSqlProvider.class, method = "sql")
    List<Map<String, Object>> selectList(@Param("sql") String sql, @Param("params") Map<String, Object> params);

    @SelectProvider(type = GenericSqlProvider.class, method = "sql")
    Map<String, Object> selectOne(@Param("sql") String sql, @Param("params") Map<String, Object> params);

    @SelectProvider(type = GenericSqlProvider.class, method = "sql")
    Long selectLong(@Param("sql") String sql, @Param("params") Map<String, Object> params);

    @InsertProvider(type = GenericSqlProvider.class, method = "sql")
    @Options(useGeneratedKeys = true, keyProperty = "params.id", keyColumn = "id")
    int insert(@Param("sql") String sql, @Param("params") Map<String, Object> params);

    @UpdateProvider(type = GenericSqlProvider.class, method = "sql")
    int update(@Param("sql") String sql, @Param("params") Map<String, Object> params);
}
