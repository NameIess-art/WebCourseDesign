package com.mall.mapper;

import com.mall.entity.Product;
import com.mall.mapper.support.GenericSqlMapper;
import com.mall.mapper.support.MyBatisMapperSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper extends MyBatisMapperSupport<Product> {

    public ProductMapper(GenericSqlMapper sqlMapper) {
        super(sqlMapper, Product.class);
    }

    public List<Product> findByActiveTrueOrderByIdDesc() {
        return selectList("t.active = true", params(), "t.id desc");
    }

    public List<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(String keyword) {
        return selectList("t.active = true and lower(t.name) like lower(#{params.keyword})", params("keyword", like(keyword)), "t.id desc");
    }

    public List<Product> findByActiveTrueAndCategoryIdOrderByIdDesc(Long categoryId) {
        return selectList("t.active = true and t.category_id = #{params.categoryId}", params("categoryId", categoryId), "t.id desc");
    }

    public List<Product> findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(Long categoryId, String keyword) {
        return selectList("t.active = true and t.category_id = #{params.categoryId} and lower(t.name) like lower(#{params.keyword})", params("categoryId", categoryId, "keyword", like(keyword)), "t.id desc");
    }

    public Page<Product> findByActiveTrueOrderByIdDesc(Pageable pageable) {
        return selectPage("t.active = true", params(), "t.id desc", pageable);
    }

    public Page<Product> findByActiveTrueAndCategoryIdOrderByIdDesc(Long categoryId, Pageable pageable) {
        return selectPage("t.active = true and t.category_id = #{params.categoryId}", params("categoryId", categoryId), "t.id desc", pageable);
    }

    public Page<Product> findByActiveTrueAndNameContainingIgnoreCaseOrderByIdDesc(String keyword, Pageable pageable) {
        return selectPage("t.active = true and lower(t.name) like lower(#{params.keyword})", params("keyword", like(keyword)), "t.id desc", pageable);
    }

    public Page<Product> findByActiveTrueAndCategoryIdAndNameContainingIgnoreCaseOrderByIdDesc(Long categoryId, String keyword, Pageable pageable) {
        return selectPage("t.active = true and t.category_id = #{params.categoryId} and lower(t.name) like lower(#{params.keyword})", params("categoryId", categoryId, "keyword", like(keyword)), "t.id desc", pageable);
    }

    public Page<Product> findAllByOrderByIdDesc(Pageable pageable) {
        return selectPage(null, params(), "t.id desc", pageable);
    }

    public Page<Product> findByMerchantIdOrderByIdDesc(Long merchantId, Pageable pageable) {
        return selectPage("t.merchant_id = #{params.merchantId}", params("merchantId", merchantId), "t.id desc", pageable);
    }

    public long countByStockLessThanEqual(Integer stock) {
        return countWhere("t.stock <= #{params.stock}", params("stock", stock));
    }

    public long countByMerchantId(Long merchantId) {
        return countWhere("t.merchant_id = #{params.merchantId}", params("merchantId", merchantId));
    }

    public int decreaseStock(Long productId, Integer quantity) {
        return executeUpdate("update products set stock = stock - #{params.quantity}, sales = sales + #{params.quantity} where id = #{params.productId} and active = true and stock >= #{params.quantity}", params("productId", productId, "quantity", quantity));
    }

    public int restoreStock(Long productId, Integer quantity) {
        return executeUpdate("update products set stock = stock + #{params.quantity}, sales = case when sales >= #{params.quantity} then sales - #{params.quantity} else 0 end where id = #{params.productId}", params("productId", productId, "quantity", quantity));
    }
}
