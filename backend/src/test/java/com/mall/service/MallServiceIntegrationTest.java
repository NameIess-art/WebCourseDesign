package com.mall.service;

import com.mall.dto.CartItemRequest;
import com.mall.dto.CheckoutRequest;
import com.mall.dto.LoginRequest;
import com.mall.dto.MarketingActivityRequest;
import com.mall.dto.PaymentCallbackRequest;
import com.mall.dto.PaymentRequest;
import com.mall.entity.Product;
import com.mall.entity.ProductSku;
import com.mall.entity.UserAccount;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.repository.ProductRepository;
import com.mall.repository.ProductSkuRepository;
import com.mall.repository.UserRepository;
import com.mall.vo.OrderResponse;
import com.mall.vo.PaymentResponse;
import jakarta.persistence.EntityManager;
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
    private AuthService authService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSkuRepository productSkuRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void merchantDemoAccountCanLogin() {
        assertThat(userRepository.findByUsername("merchant")).isPresent();

        var response = authService.login(new LoginRequest("merchant", "merchant123"));

        assertThat(response.role()).isEqualTo("MERCHANT");
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void completesTheCustomerOrderLifecycle() {
        UserAccount user = userRepository.findByUsername("demo").orElseThrow();
        Product product = productRepository.findByActiveTrueOrderByIdDesc().getFirst();
        int originalStock = product.getStock();

        mallService.addToCart(user, new CartItemRequest(product.getId(), null, 2));
        assertThat(mallService.cart(user).totalAmount()).isPositive();

        OrderResponse order = mallService.checkout(user, new CheckoutRequest("Shanghai Demo Road 1001",
                null, 0, null, "test-order-lifecycle"));
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

        mallService.addToCart(user, new CartItemRequest(product.getId(), null, 1));
        OrderResponse order = mallService.checkout(user, new CheckoutRequest("Shanghai Demo Road 1001",
                null, 0, null, "test-cancel-restore"));
        mallService.cancel(user, order.id());

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(originalStock);
        adminService.deleteProduct(product.getId());
        assertThat(productRepository.findById(product.getId()).orElseThrow().getActive()).isFalse();
        assertThat(mallService.products(null, null)).noneMatch(item -> item.id().equals(product.getId()));
    }

    @Test
    void productSearchIsPagedAndSkuStockIsUsedDuringCheckout() {
        UserAccount user = userRepository.findByUsername("demo").orElseThrow();
        Product product = productRepository.findByActiveTrueOrderByIdDesc().getFirst();
        ProductSku sku = productSkuRepository.findByProductIdOrderByIdAsc(product.getId()).getFirst();
        int originalSkuStock = sku.getStock();

        var page = mallService.products(null, null, 0, 2);
        assertThat(page.content()).hasSizeLessThanOrEqualTo(2);
        assertThat(page.totalElements()).isPositive();

        mallService.addToCart(user, new CartItemRequest(product.getId(), sku.getId(), 1));
        assertThat(mallService.cart(user).items().getFirst().skuId()).isEqualTo(sku.getId());

        OrderResponse order = mallService.checkout(user, new CheckoutRequest("Shanghai Demo Road 1001",
                null, 0, sku.getId(), "test-sku-checkout"));
        assertThat(order.items().getFirst().skuId()).isEqualTo(sku.getId());
        assertThat(productSkuRepository.findById(sku.getId()).orElseThrow().getStock()).isEqualTo(originalSkuStock - 1);

        mallService.cancel(user, order.id());
        assertThat(productSkuRepository.findById(sku.getId()).orElseThrow().getStock()).isEqualTo(originalSkuStock);
    }

    @Test
    void paymentCallbackIsIdempotentAndRewardsPointsOnce() {
        UserAccount user = userRepository.findByUsername("demo").orElseThrow();
        Product product = productRepository.findByActiveTrueOrderByIdDesc().getFirst();
        int originalPoints = user.getPoints() == null ? 0 : user.getPoints();

        mallService.addToCart(user, new CartItemRequest(product.getId(), null, 1));
        OrderResponse order = mallService.checkout(user, new CheckoutRequest("Shanghai Demo Road 1001",
                null, 0, null, "test-payment-idempotent"));
        PaymentResponse created = paymentService.createPayment(user,
                new PaymentRequest(order.id(), "ALIPAY", "test-payment-create"));

        PaymentResponse first = paymentService.mockCallback(
                new PaymentCallbackRequest(created.paymentNo(), "ALIPAY-001"));
        PaymentResponse replay = paymentService.mockCallback(
                new PaymentCallbackRequest(created.paymentNo(), "ALIPAY-001-REPLAY"));

        assertThat(first.status()).isEqualTo("PAID");
        assertThat(replay.status()).isEqualTo("PAID");
        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.findById(user.getId()).orElseThrow().getPoints())
                .isEqualTo(originalPoints + order.totalAmount().intValue());
    }

    @Test
    void adminCreatedActivityAppearsInPublicVenueImmediately() {
        var merchantActivity = operationService.createActivity(
                new MarketingActivityRequest("Merchant Draft Campaign", "GROUP_BUY", "Pending platform review"),
                false);
        var adminActivity = operationService.createActivity(
                new MarketingActivityRequest("Platform Approved Campaign", "FULL_REDUCTION", "Visible in venue"),
                true);

        assertThat(merchantActivity.status()).isEqualTo("PENDING_REVIEW");
        assertThat(adminActivity.status()).isEqualTo("APPROVED");
        assertThat(operationService.publicActivities())
                .extracting("title")
                .contains("Platform Approved Campaign")
                .doesNotContain("Merchant Draft Campaign");
    }
}
