package com.mall.mapper;

import com.mall.entity.Category;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper extends MyBatisMapperSupport<Category> {

    public CategoryMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, Category.class);
    }

    public List<Category> findAllByOrderBySortOrderAscIdAsc() {
        return selectList(null, params(), "t.sort_order asc, t.id asc");
    }

    public Page<Category> findAllByOrderBySortOrderAscIdAsc(Pageable pageable) {
        return selectPage(null, params(), "t.sort_order asc, t.id asc", pageable);
    }
}
