package com.mall.service;

import com.mall.dto.AddressRequest;
import com.mall.dto.AfterSaleRequest;
import com.mall.dto.ProductQuestionRequest;
import com.mall.dto.ProductReviewRequest;
import com.mall.entity.AddressBook;
import com.mall.entity.AfterSaleRecord;
import com.mall.entity.Coupon;
import com.mall.entity.FavoriteProduct;
import com.mall.entity.MemberMessage;
import com.mall.entity.OrderEntity;
import com.mall.entity.Product;
import com.mall.entity.ProductQuestion;
import com.mall.entity.ProductReview;
import com.mall.entity.UserAccount;
import com.mall.entity.UserCoupon;
import com.mall.enums.OrderStatus;
import com.mall.exception.BusinessException;
import com.mall.mapper.AddressBookMapper;
import com.mall.mapper.AfterSaleRecordMapper;
import com.mall.mapper.CouponMapper;
import com.mall.mapper.FavoriteProductMapper;
import com.mall.mapper.MemberMessageMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductQuestionMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.ProductReviewMapper;
import com.mall.mapper.UserCouponMapper;
import com.mall.vo.AddressResponse;
import com.mall.vo.AfterSaleResponse;
import com.mall.vo.CouponResponse;
import com.mall.vo.FavoriteResponse;
import com.mall.vo.LogisticsResponse;
import com.mall.vo.MemberMessageResponse;
import com.mall.vo.MemberProfileResponse;
import com.mall.vo.ProductQuestionResponse;
import com.mall.vo.ProductReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.mall.vo.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerExperienceService {

    private final AddressBookMapper addressBookMapper;
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final FavoriteProductMapper favoriteProductMapper;
    private final ProductReviewMapper productReviewMapper;
    private final ProductQuestionMapper productQuestionMapper;
    private final MemberMessageMapper memberMessageMapper;
    private final AfterSaleRecordMapper afterSaleRecordMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> addresses(UserAccount user, int page, int size) {
        Page<AddressResponse> result = addressBookMapper.findByUserIdOrderByDefaultAddressDescIdDesc(user.getId(), PageRequest.of(page, size)).map(this::toAddressResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public AddressResponse saveAddress(UserAccount user, AddressRequest request) {
        if (Boolean.TRUE.equals(request.defaultAddress())) {
            clearDefaultAddress(user);
        }
        AddressBook address = new AddressBook();
        address.setUser(user);
        applyAddress(address, request);
        return toAddressResponse(addressBookMapper.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(UserAccount user, Long id, AddressRequest request) {
        AddressBook address = addressBookMapper.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Address not found"));
        if (Boolean.TRUE.equals(request.defaultAddress())) {
            clearDefaultAddress(user);
        }
        applyAddress(address, request);
        return toAddressResponse(addressBookMapper.save(address));
    }

    @Transactional
    public void deleteAddress(UserAccount user, Long id) {
        AddressBook address = addressBookMapper.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Address not found"));
        addressBookMapper.delete(address);
    }

    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> coupons(UserAccount user, int page, int size) {
        Map<Long, UserCoupon> claimed = userCouponMapper.findByUserIdOrderByClaimedAtDesc(user.getId()).stream().collect(Collectors.toMap(item -> item.getCoupon().getId(), item -> item, (a, b) -> a));
        Page<CouponResponse> result = couponMapper.findByActiveTrueOrderByIdDesc(PageRequest.of(page, size)).map(coupon -> {
            UserCoupon userCoupon = claimed.get(coupon.getId());
            return new CouponResponse(coupon.getId(), coupon.getName(), coupon.getThresholdAmount(), coupon.getDiscountAmount(), coupon.getStock(), userCoupon != null, userCoupon != null && Boolean.TRUE.equals(userCoupon.getUsed()), coupon.getValidUntil());
        });
        return PageResponse.of(result);
    }

    @Transactional
    public CouponResponse claimCoupon(UserAccount user, Long couponId) {
        if (userCouponMapper.existsByUserIdAndCouponId(user.getId(), couponId)) {
            throw new BusinessException("Coupon already claimed");
        }
        Coupon coupon = couponMapper.findById(couponId)
                .orElseThrow(() -> new BusinessException("Coupon not found"));
        if (coupon.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Coupon expired");
        }
        if (couponMapper.decreaseStock(couponId) == 0) {
            throw new BusinessException("Coupon has been fully claimed");
        }
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setClaimedAt(LocalDateTime.now());
        userCouponMapper.save(userCoupon);
        Coupon latest = couponMapper.findById(couponId).orElseThrow();
        return new CouponResponse(latest.getId(), latest.getName(), latest.getThresholdAmount(),
                latest.getDiscountAmount(), latest.getStock(), true, false, latest.getValidUntil());
    }

    @Transactional(readOnly = true)
    public PageResponse<FavoriteResponse> favorites(UserAccount user, int page, int size) {
        Page<FavoriteResponse> result = favoriteProductMapper.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size)).map(this::toFavoriteResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public void favorite(UserAccount user, Long productId) {
        if (favoriteProductMapper.existsByUserIdAndProductId(user.getId(), productId)) {
            return;
        }
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new BusinessException("Product not found"));
        FavoriteProduct favorite = new FavoriteProduct();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteProductMapper.save(favorite);
        product.setFavoriteCount((product.getFavoriteCount() == null ? 0 : product.getFavoriteCount()) + 1);
    }

    @Transactional
    public void unfavorite(UserAccount user, Long productId) {
        favoriteProductMapper.findByUserIdAndProductId(user.getId(), productId).ifPresent(favorite -> {
            Product product = favorite.getProduct();
            product.setFavoriteCount(Math.max(0,
                    (product.getFavoriteCount() == null ? 0 : product.getFavoriteCount()) - 1));
            favoriteProductMapper.delete(favorite);
        });
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductReviewResponse> reviews(Long productId, int page, int size) {
        Page<ProductReviewResponse> result = productReviewMapper.findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(page, size)).map(item -> new ProductReviewResponse(item.getId(), item.getUser().getUsername(), item.getRating(), item.getContent(), item.getCreatedAt()));
        return PageResponse.of(result);
    }

    @Transactional
    public ProductReviewResponse addReview(UserAccount user, Long productId, ProductReviewRequest request) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new BusinessException("Product not found"));
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setContent(request.content());
        review.setCreatedAt(LocalDateTime.now());
        ProductReview saved = productReviewMapper.save(review);

        List<ProductReview> reviews = productReviewMapper.findByProductIdOrderByCreatedAtDesc(productId);
        BigDecimal average = BigDecimal.valueOf(reviews.stream().mapToInt(ProductReview::getRating).average().orElse(5.0))
                .setScale(2, RoundingMode.HALF_UP);
        product.setRating(average);
        return new ProductReviewResponse(saved.getId(), user.getUsername(), saved.getRating(),
                saved.getContent(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductQuestionResponse> questions(Long productId, int page, int size) {
        Page<ProductQuestionResponse> result = productQuestionMapper.findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(page, size)).map(item -> new ProductQuestionResponse(item.getId(), item.getUser().getUsername(), item.getQuestion(), item.getAnswer(), item.getCreatedAt()));
        return PageResponse.of(result);
    }

    @Transactional
    public ProductQuestionResponse askQuestion(UserAccount user, Long productId, ProductQuestionRequest request) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new BusinessException("Product not found"));
        ProductQuestion question = new ProductQuestion();
        question.setProduct(product);
        question.setUser(user);
        question.setQuestion(request.question());
        question.setCreatedAt(LocalDateTime.now());
        ProductQuestion saved = productQuestionMapper.save(question);
        product.setQuestionCount((product.getQuestionCount() == null ? 0 : product.getQuestionCount()) + 1);
        return new ProductQuestionResponse(saved.getId(), user.getUsername(), saved.getQuestion(),
                saved.getAnswer(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse profile(UserAccount user) {
        int points = user.getPoints() == null ? 0 : user.getPoints();
        String level = user.getMemberLevel() == null || user.getMemberLevel().isBlank()
                ? levelByPoints(points) : user.getMemberLevel();
        return new MemberProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                level,
                points,
                "Points deduction, member price, birthday coupon, priority after-sale",
                memberMessageMapper.countByUserIdAndReadFlagFalse(user.getId())
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberMessageResponse> messages(UserAccount user, int page, int size) {
        Page<MemberMessageResponse> result = memberMessageMapper.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size)).map(this::toMessageResponse);
        return PageResponse.of(result);
    }

    @Transactional
    public void readMessage(UserAccount user, Long id) {
        MemberMessage message = memberMessageMapper.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Message not found"));
        message.setReadFlag(true);
    }

    @Transactional(readOnly = true)
    public LogisticsResponse logistics(UserAccount user, Long orderId) {
        OrderEntity order = getUserOrder(user, orderId);
        String trackingNo = order.getStatus() == OrderStatus.PENDING_PAYMENT
                ? "WAIT_PAY" : "YT" + order.getOrderNo().substring(Math.max(0, order.getOrderNo().length() - 8));
        List<String> traces = switch (order.getStatus()) {
            case PENDING_PAYMENT -> List.of("Order submitted, waiting for payment");
            case PAID -> List.of("Payment successful", "Merchant is reviewing and preparing shipment");
            case SHIPPED -> List.of("Merchant shipped", "Parcel arrived at sorting center", "Estimated delivery tomorrow");
            case COMPLETED -> List.of("Merchant shipped", "Courier delivered", "User confirmed receipt");
            case CANCELLED -> List.of("Order cancelled, logistics closed");
        };
        return new LogisticsResponse(order.getId(), order.getOrderNo(), "YTO Express", trackingNo, traces);
    }

    @Transactional
    public AfterSaleResponse requestAfterSale(UserAccount user, Long orderId, AfterSaleRequest request) {
        OrderEntity order = getUserOrder(user, orderId);
        Set<OrderStatus> allowed = Set.of(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.COMPLETED);
        if (!allowed.contains(order.getStatus())) {
            throw new BusinessException("Current order status does not support after-sale request");
        }
        AfterSaleRecord record = new AfterSaleRecord();
        record.setOrder(order);
        record.setUser(user);
        record.setType(request.type());
        record.setReason(request.reason());
        record.setCreatedAt(LocalDateTime.now());
        return toAfterSaleResponse(afterSaleRecordMapper.save(record));
    }

    @Transactional(readOnly = true)
    public PageResponse<AfterSaleResponse> afterSales(UserAccount user, int page, int size) {
        Page<AfterSaleResponse> result = afterSaleRecordMapper.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size)).map(this::toAfterSaleResponse);
        return PageResponse.of(result);
    }

    private void clearDefaultAddress(UserAccount user) {
        addressBookMapper.findByUserIdOrderByDefaultAddressDescIdDesc(user.getId())
                .forEach(address -> address.setDefaultAddress(false));
    }

    private void applyAddress(AddressBook address, AddressRequest request) {
        address.setReceiver(request.receiver());
        address.setPhone(request.phone());
        address.setRegion(request.region());
        address.setDetail(request.detail());
        address.setDefaultAddress(Boolean.TRUE.equals(request.defaultAddress()));
    }

    private String levelByPoints(int points) {
        if (points >= 500) {
            return "PLATINUM";
        }
        if (points >= 200) {
            return "GOLD";
        }
        return "BASIC";
    }

    private OrderEntity getUserOrder(UserAccount user, Long orderId) {
        return orderMapper.findById(orderId)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException("Order not found"));
    }

    private AddressResponse toAddressResponse(AddressBook address) {
        return new AddressResponse(address.getId(), address.getReceiver(), address.getPhone(),
                address.getRegion(), address.getDetail(), address.getDefaultAddress());
    }

    private FavoriteResponse toFavoriteResponse(FavoriteProduct favorite) {
        Product product = favorite.getProduct();
        return new FavoriteResponse(favorite.getId(), product.getId(), product.getName(),
                product.getImageUrl(), product.getPrice(), favorite.getCreatedAt());
    }

    private MemberMessageResponse toMessageResponse(MemberMessage message) {
        return new MemberMessageResponse(message.getId(), message.getTitle(), message.getContent(),
                message.getReadFlag(), message.getCreatedAt());
    }

    private AfterSaleResponse toAfterSaleResponse(AfterSaleRecord record) {
        return new AfterSaleResponse(record.getId(), record.getOrder().getOrderNo(), record.getType(),
                record.getReason(), record.getStatus(), record.getCreatedAt());
    }
}
