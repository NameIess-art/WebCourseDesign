package com.mall.service;

import com.mall.dto.CartItemRequest;
import com.mall.dto.CheckoutRequest;
import com.mall.entity.Product;
import com.mall.entity.UserAccount;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import com.mall.vo.OrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mall-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class MallServiceIntegrationTest {

    @Autowired
    private MallService mallService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void completesTheCustomerOrderLifecycle() {
        UserAccount user = userRepository.findByUsername("demo").orElseThrow();
        Product product = productRepository.findByActiveTrueOrderByIdDesc().getFirst();
        int originalStock = product.getStock();

        mallService.addToCart(user, new CartItemRequest(product.getId(), 2));
        assertThat(mallService.cart(user).totalAmount()).isPositive();

        OrderResponse order = mallService.checkout(user, new CheckoutRequest("Shanghai Demo Road 1001"));
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING_PAYMENT.name());
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(originalStock - 2);
        assertThatThrownBy(() -> mallService.confirmReceived(user, order.id()))
                .isInstanceOf(BusinessException.class);

        mallService.pay(user, order.id());
        assertThat(adminService.dashboard().totalRevenue()).isEqualByComparingTo(order.totalAmount());
        adminService.shipOrder(order.id());
        assertThat(mallService.confirmReceived(user, order.id()).status()).isEqualTo(OrderStatus.COMPLETED.name());
    }

    @Test
    void cancellingRestoresStockAndProductDeletionIsSoft() {
        UserAccount user = userRepository.findByUsername("demo").orElseThrow();
        Product product = productRepository.findByActiveTrueOrderByIdDesc().getFirst();
        int originalStock = product.getStock();

        mallService.addToCart(user, new CartItemRequest(product.getId(), 1));
        OrderResponse order = mallService.checkout(user, new CheckoutRequest("Shanghai Demo Road 1001"));
        mallService.cancel(user, order.id());

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(originalStock);
        adminService.deleteProduct(product.getId());
        assertThat(productRepository.findById(product.getId()).orElseThrow().getActive()).isFalse();
        assertThat(mallService.products(null, null)).noneMatch(item -> item.id().equals(product.getId()));
    }
}
