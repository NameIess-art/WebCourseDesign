package com.mall.mapper.support;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class MyBatisMapperSupport<T> {

    private final GenericSqlMapper sqlMapper;
    private final Class<T> entityType;
    private final String tableName;
    private final List<FieldBinding> bindings;
    private final FieldBinding idBinding;

    protected MyBatisMapperSupport(GenericSqlMapper sqlMapper, Class<T> entityType) {
        this.sqlMapper = sqlMapper;
        this.entityType = entityType;
        this.tableName = tableName(entityType);
        this.bindings = bindings(entityType);
        this.idBinding = bindings.stream()
                .filter(FieldBinding::id)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("实体缺少主键字段：" + entityType.getSimpleName()));
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(selectOne("t." + idBinding.column() + " = #{params.id}", params("id", id)));
    }

    public List<T> findAll() {
        return selectList(null, params(), null);
    }

    public Page<T> findAll(Pageable pageable) {
        return selectPage(null, params(), null, pageable);
    }

    public long count() {
        return countWhere(null, params());
    }

    public T save(T entity) {
        if (getField(entity, idBinding.field()) == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    public List<T> saveAll(Collection<T> entities) {
        List<T> saved = new ArrayList<>();
        for (T entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    public void delete(T entity) {
        Object id = getField(entity, idBinding.field());
        if (id != null) {
            deleteById(((Number) id).longValue());
        }
    }

    public void deleteAll(Collection<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    public void deleteById(Long id) {
        sqlMapper.update("delete from " + tableName + " where " + idBinding.column() + " = #{params.id}", params("id", id));
    }

    protected T selectOne(String whereSql, Map<String, Object> params) {
        List<T> result = selectList(whereSql, params, null, 1, 0);
        return result.isEmpty() ? null : result.getFirst();
    }

    protected List<T> selectList(String whereSql, Map<String, Object> params, String orderSql) {
        return selectList(whereSql, params, orderSql, null, null);
    }

    protected List<T> selectList(String whereSql, Map<String, Object> params, String orderSql, Integer limit, Integer offset) {
        Map<String, Object> sqlParams = new LinkedHashMap<>(params);
        if (limit != null) {
            sqlParams.put("limit", limit);
            sqlParams.put("offset", offset == null ? 0 : offset);
        }
        String sql = selectSql() + where(whereSql) + order(orderSql) + page(limit, offset);
        return mapRows(sqlMapper.selectList(sql, sqlParams));
    }

    protected Page<T> selectPage(String whereSql, Map<String, Object> params, String orderSql, Pageable pageable) {
        Map<String, Object> pageParams = new LinkedHashMap<>(params);
        pageParams.put("limit", pageable.getPageSize());
        pageParams.put("offset", pageable.getOffset());
        List<T> content = selectList(whereSql, pageParams, orderSql, pageable.getPageSize(), Math.toIntExact(pageable.getOffset()));
        long total = countWhere(whereSql, params);
        return new PageImpl<>(content, pageable, total);
    }

    protected long countWhere(String whereSql, Map<String, Object> params) {
        Long count = sqlMapper.selectLong("select count(*) from " + tableName + " t" + where(whereSql), params);
        return count == null ? 0 : count;
    }

    protected BigDecimal sumWhere(String column, String whereSql, Map<String, Object> params) {
        Map<String, Object> row = sqlMapper.selectOne(
                "select coalesce(sum(" + column + "), 0) as total from " + tableName + " t" + where(whereSql), params);
        Object value = row == null ? BigDecimal.ZERO : normalized(row).get("total");
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(String.valueOf(value));
    }

    protected int executeUpdate(String sql, Map<String, Object> params) {
        return sqlMapper.update(sql, params);
    }

    public T selectRawOne(String sql, Map<String, Object> params) {
        Map<String, Object> row = sqlMapper.selectOne(sql, params);
        return row == null ? null : mapRow(row);
    }

    public List<T> selectRawList(String sql, Map<String, Object> params) {
        return mapRows(sqlMapper.selectList(sql, params));
    }

    public Page<T> selectRawPage(String sqlWithoutPaging, String countSql, Map<String, Object> params, Pageable pageable) {
        Map<String, Object> pageParams = new LinkedHashMap<>(params);
        pageParams.put("limit", pageable.getPageSize());
        pageParams.put("offset", pageable.getOffset());
        List<T> content = selectRawList(sqlWithoutPaging + " limit #{params.limit} offset #{params.offset}", pageParams);
        Long total = sqlMapper.selectLong(countSql, params);
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    public String selectSql() {
        String rootColumns = bindings.stream()
                .filter(binding -> !binding.association() && !binding.collection())
                .map(binding -> "t." + binding.column() + " as " + binding.column())
                .collect(Collectors.joining(", "));
        String joins = bindings.stream()
                .filter(FieldBinding::association)
                .map(binding -> " left join " + tableName(binding.field().getType()) + " " + joinAlias(binding)
                        + " on " + joinAlias(binding) + ".id = t." + binding.column())
                .collect(Collectors.joining());
        String associationColumns = bindings.stream()
                .filter(FieldBinding::association)
                .flatMap(binding -> bindings(binding.field().getType()).stream()
                        .filter(child -> !child.association() && !child.collection())
                        .map(child -> joinAlias(binding) + "." + child.column() + " as " + binding.field().getName() + "__" + child.column()))
                .collect(Collectors.joining(", "));
        return "select " + rootColumns + (associationColumns.isBlank() ? "" : ", " + associationColumns)
                + " from " + tableName + " t" + joins;
    }

    protected Map<String, Object> params(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            result.put((String) values[i], values[i + 1]);
        }
        return result;
    }

    protected String inClause(String name, Collection<?> values, Map<String, Object> params) {
        int index = 0;
        List<String> placeholders = new ArrayList<>();
        for (Object value : values) {
            String key = name + index++;
            params.put(key, value);
            placeholders.add("#{params." + key + "}");
        }
        return "(" + String.join(", ", placeholders) + ")";
    }

    protected String like(String keyword) {
        return "%" + keyword + "%";
    }

    protected List<T> mapRows(List<Map<String, Object>> rows) {
        return rows.stream().map(this::mapRow).toList();
    }

    protected T mapRow(Map<String, Object> row) {
        try {
            T entity = entityType.getDeclaredConstructor().newInstance();
            Map<String, Object> normalized = normalized(row);
            for (FieldBinding binding : bindings) {
                if (!binding.association() && !binding.collection() && normalized.containsKey(binding.column())) {
                    setField(entity, binding.field(), normalized.get(binding.column()));
                }
            }
            for (FieldBinding binding : bindings) {
                if (binding.association()) {
                    mapAssociation(entity, binding, normalized);
                }
            }
            return entity;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("实体映射失败：" + entityType.getSimpleName(), ex);
        }
    }

    private void insert(T entity) {
        Map<String, Object> values = columnValues(entity, false);
        String columns = String.join(", ", values.keySet());
        String placeholders = values.keySet().stream()
                .map(key -> "#{params." + key + "}")
                .collect(Collectors.joining(", "));
        String sql = "insert into " + tableName + " (" + columns + ") values (" + placeholders + ")";
        sqlMapper.insert(sql, values);
        if (values.containsKey("id")) {
            setField(entity, idBinding.field(), values.get("id"));
        }
    }

    private void update(T entity) {
        Map<String, Object> values = columnValues(entity, true);
        String assignments = values.keySet().stream()
                .filter(key -> !key.equals(idBinding.column()))
                .map(key -> key + " = #{params." + key + "}")
                .collect(Collectors.joining(", "));
        String sql = "update " + tableName + " set " + assignments + " where " + idBinding.column()
                + " = #{params." + idBinding.column() + "}";
        sqlMapper.update(sql, values);
    }

    private Map<String, Object> columnValues(T entity, boolean includeId) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (FieldBinding binding : bindings) {
            if (binding.collection() || binding.id() && !includeId) {
                continue;
            }
            Object value = getField(entity, binding.field());
            if (binding.association()) {
                value = value == null ? null : getField(value, idField(binding.field().getType()));
            }
            if (value instanceof Enum<?> enumValue) {
                value = enumValue.name();
            }
            values.put(binding.column(), value);
        }
        return values;
    }

    private void mapAssociation(T entity, FieldBinding binding, Map<String, Object> row) throws ReflectiveOperationException {
        String prefix = binding.field().getName() + "__";
        boolean hasValue = row.keySet().stream().anyMatch(key -> key.startsWith(prefix) && row.get(key) != null);
        if (!hasValue && row.get(binding.column()) == null) {
            return;
        }
        Object associated = binding.field().getType().getDeclaredConstructor().newInstance();
        Field associatedId = idField(binding.field().getType());
        if (row.get(prefix + "id") != null) {
            setField(associated, associatedId, row.get(prefix + "id"));
        } else {
            setField(associated, associatedId, row.get(binding.column()));
        }
        for (FieldBinding child : bindings(binding.field().getType())) {
            String key = prefix + child.column();
            if (!child.association() && row.containsKey(key)) {
                setField(associated, child.field(), row.get(key));
            }
        }
        setField(entity, binding.field(), associated);
    }

    private static Map<String, Object> normalized(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        row.forEach((key, value) -> result.put(key.toLowerCase(Locale.ROOT), value));
        return result;
    }

    private static String where(String whereSql) {
        return whereSql == null || whereSql.isBlank() ? "" : " where " + whereSql;
    }

    private static String order(String orderSql) {
        return orderSql == null || orderSql.isBlank() ? "" : " order by " + orderSql;
    }

    private static String page(Integer limit, Integer offset) {
        return limit == null ? "" : " limit #{params.limit} offset #{params.offset}";
    }

    private static String joinAlias(FieldBinding binding) {
        return "j_" + binding.field().getName();
    }

    private static String tableName(Class<?> type) {
        Table table = type.getAnnotation(Table.class);
        return table != null && !table.name().isBlank() ? table.name() : toSnake(type.getSimpleName());
    }

    private static List<FieldBinding> bindings(Class<?> type) {
        List<FieldBinding> result = new ArrayList<>();
        for (Field field : fields(type)) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                result.add(new FieldBinding(field, field.getName(), false, false, true, false));
                continue;
            }
            boolean id = field.isAnnotationPresent(Id.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            boolean association = field.isAnnotationPresent(ManyToOne.class);
            String column = joinColumn != null && !joinColumn.name().isBlank() ? joinColumn.name() : columnName(field);
            result.add(new FieldBinding(field, column, id, association, false, false));
        }
        return result;
    }

    private static String columnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column != null && !column.name().isBlank() ? column.name() : toSnake(field.getName());
    }

    private static List<Field> fields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                result.add(field);
            }
            current = current.getSuperclass();
        }
        return result;
    }

    private static Field idField(Class<?> type) {
        return fields(type).stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("实体缺少主键字段：" + type.getSimpleName()));
    }

    private static Object getField(Object target, Field field) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("读取字段失败：" + field.getName(), ex);
        }
    }

    private static void setField(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, convertValue(value, field.getType()));
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("写入字段失败：" + field.getName(), ex);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        if (targetType == Long.class) {
            return ((Number) value).longValue();
        }
        if (targetType == Integer.class) {
            return ((Number) value).intValue();
        }
        if (targetType == BigDecimal.class) {
            return value instanceof BigDecimal decimal ? decimal : new BigDecimal(String.valueOf(value));
        }
        if (targetType == Boolean.class) {
            return value instanceof Boolean bool ? bool : ((Number) value).intValue() != 0;
        }
        if (targetType == LocalDateTime.class && value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (targetType == LocalDate.class && value instanceof Date date) {
            return date.toLocalDate();
        }
        if (targetType.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetType, String.valueOf(value));
        }
        return value;
    }

    private static String toSnake(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    private record FieldBinding(Field field, String column, boolean id, boolean association, boolean collection,
                                boolean ignored) {
    }
}
