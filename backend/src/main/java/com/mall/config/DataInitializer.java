package com.mall.config;

import com.mall.entity.Category;
import com.mall.entity.Coupon;
import com.mall.entity.MarketingActivity;
import com.mall.entity.MemberMessage;
import com.mall.entity.PlatformRiskItem;
import com.mall.entity.Product;
import com.mall.entity.SeckillEvent;
import com.mall.entity.SystemConfigItem;
import com.mall.entity.UserAccount;
import com.mall.enums.UserRole;
import com.mall.repository.CategoryRepository;
import com.mall.repository.CouponRepository;
import com.mall.repository.MarketingActivityRepository;
import com.mall.repository.MemberMessageRepository;
import com.mall.repository.PlatformRiskItemRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.SeckillEventRepository;
import com.mall.repository.SystemConfigItemRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               CouponRepository couponRepository,
                               MemberMessageRepository memberMessageRepository,
                               MarketingActivityRepository marketingActivityRepository,
                               SystemConfigItemRepository systemConfigItemRepository,
                               PlatformRiskItemRepository platformRiskItemRepository,
                               SeckillEventRepository seckillEventRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@mall.local");
                admin.setDisplayName("Mall Admin");
                admin.setRole(UserRole.ADMIN);

                UserAccount user = new UserAccount();
                user.setUsername("demo");
                user.setPassword(passwordEncoder.encode("demo123"));
                user.setEmail("demo@mall.local");
                user.setDisplayName("Demo User");
                user.setRole(UserRole.USER);

                userRepository.saveAll(List.of(admin, user));
            }

            if (categoryRepository.count() == 0) {
                Category digital = new Category();
                digital.setName("Digital");
                digital.setSortOrder(1);

                Category fashion = new Category();
                fashion.setName("Fashion");
                fashion.setSortOrder(2);

                Category home = new Category();
                home.setName("Home");
                home.setSortOrder(3);

                categoryRepository.saveAll(List.of(digital, fashion, home));
            }

            if (productRepository.count() == 0) {
                List<Category> categories = categoryRepository.findAllByOrderBySortOrderAscIdAsc();
                Category digital = categories.get(0);
                Category fashion = categories.get(1);
                Category home = categories.get(2);

                productRepository.saveAll(List.of(
                        product("Noise Cancelling Headphones", "Comfortable studio sound for daily listening",
                                "A demo product with long battery life and balanced sound signature.",
                                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("799.00"), 50, 24, digital),
                        product("4K Smart Monitor", "Crisp panel for work and entertainment",
                                "27-inch monitor designed for productivity, gaming, and media.",
                                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("1699.00"), 20, 15, digital),
                        product("Minimal Sneaker", "Clean everyday design with soft cushioning",
                                "Modern sneaker with breathable upper and flexible sole.",
                                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("359.00"), 80, 41, fashion),
                        product("Ceramic Coffee Set", "Warm-toned tableware for calm mornings",
                                "Handcrafted feel with durable glazed finish for daily use.",
                                "https://images.unsplash.com/photo-1517705008128-361805f42e86?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("229.00"), 65, 12, home)
                ));
            }

            productRepository.findAll().forEach(product -> {
                if (product.getSkuCode() == null) {
                    product.setSkuCode("SKU-" + product.getId());
                }
                if (product.getSpec() == null) {
                    product.setSpec("Default / Standard");
                }
                if (product.getPromotionTag() == null) {
                    product.setPromotionTag("今日推荐");
                }
                if (product.getFavoriteCount() == null) {
                    product.setFavoriteCount(0);
                }
                if (product.getQuestionCount() == null) {
                    product.setQuestionCount(0);
                }
                if (product.getRating() == null) {
                    product.setRating(new BigDecimal("4.80"));
                }
            });

            if (couponRepository.count() == 0) {
                couponRepository.saveAll(List.of(
                        coupon("新人红包 30 元", new BigDecimal("199.00"), new BigDecimal("30.00"), 100),
                        coupon("满减券 80 元", new BigDecimal("699.00"), new BigDecimal("80.00"), 50),
                        coupon("会员专享券 120 元", new BigDecimal("1299.00"), new BigDecimal("120.00"), 30)
                ));
            }

            if (memberMessageRepository.count() == 0) {
                UserAccount user = userRepository.findByUsername("demo").orElseThrow();
                memberMessageRepository.saveAll(List.of(
                        message(user, "新人礼包已到账", "优惠券、积分和会员权益已为你准备好。"),
                        message(user, "订单履约提醒", "支付后可在订单详情查看物流轨迹与售后入口。")
                ));
            }

            if (marketingActivityRepository.count() == 0) {
                marketingActivityRepository.saveAll(List.of(
                        activity("618 全域大促会场", "FULL_REDUCTION", "满 699 减 80，叠加会员积分", "APPROVED"),
                        activity("数码尖货秒杀", "SECKILL", "限量库存、独立秒杀库存池、用户级限流", "RUNNING"),
                        activity("拼团拉新活动", "GROUP_BUY", "2 人成团，失败自动退款", "PENDING_REVIEW")
                ));
            }

            if (systemConfigItemRepository.count() == 0) {
                systemConfigItemRepository.saveAll(List.of(
                        config("home_popup", "新人礼包弹窗", "首页弹窗配置"),
                        config("channel_wechat", "enabled", "微信渠道支付开关"),
                        config("content_audit", "manual+keyword", "内容审核策略")
                ));
            }

            if (platformRiskItemRepository.count() == 0) {
                platformRiskItemRepository.saveAll(List.of(
                        risk("merchant-demo", "PRICE_ABNORMAL", "商品价格低于平台风控阈值，等待运营审核"),
                        risk("review-10001", "CONTENT_AUDIT", "评价命中敏感词，需人工复核")
                ));
            }

            if (seckillEventRepository.count() == 0) {
                Product product = productRepository.findByActiveTrueOrderByIdDesc().getFirst();
                SeckillEvent event = new SeckillEvent();
                event.setProduct(product);
                event.setSeckillPrice(product.getPrice().multiply(new BigDecimal("0.80")));
                event.setStock(Math.min(10, product.getStock()));
                event.setSold(0);
                event.setActive(true);
                event.setStartAt(LocalDateTime.now().minusHours(1));
                event.setEndAt(LocalDateTime.now().plusDays(1));
                seckillEventRepository.save(event);
            }
        };
    }

    private Product product(String name,
                            String subtitle,
                            String description,
                            String imageUrl,
                            BigDecimal price,
                            int stock,
                            int sales,
                            Category category) {
        Product product = new Product();
        product.setName(name);
        product.setSubtitle(subtitle);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setPrice(price);
        product.setStock(stock);
        product.setSales(sales);
        product.setActive(true);
        product.setSkuCode("SKU-" + name.toUpperCase().replaceAll("[^A-Z0-9]", "-"));
        product.setSpec("Standard / Demo");
        product.setPromotionTag("首页推荐");
        product.setFavoriteCount(0);
        product.setQuestionCount(0);
        product.setRating(new BigDecimal("4.80"));
        product.setCategory(category);
        return product;
    }

    private Coupon coupon(String name, BigDecimal threshold, BigDecimal discount, int stock) {
        Coupon coupon = new Coupon();
        coupon.setName(name);
        coupon.setThresholdAmount(threshold);
        coupon.setDiscountAmount(discount);
        coupon.setStock(stock);
        coupon.setActive(true);
        coupon.setValidUntil(LocalDateTime.now().plusDays(30));
        return coupon;
    }

    private MemberMessage message(UserAccount user, String title, String content) {
        MemberMessage message = new MemberMessage();
        message.setUser(user);
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(false);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    private MarketingActivity activity(String title, String type, String rule, String status) {
        MarketingActivity activity = new MarketingActivity();
        activity.setTitle(title);
        activity.setType(type);
        activity.setRuleText(rule);
        activity.setStatus(status);
        activity.setStartAt(LocalDateTime.now().minusHours(2));
        activity.setEndAt(LocalDateTime.now().plusDays(7));
        return activity;
    }

    private SystemConfigItem config(String key, String value, String description) {
        SystemConfigItem item = new SystemConfigItem();
        item.setConfigKey(key);
        item.setConfigValue(value);
        item.setDescription(description);
        return item;
    }

    private PlatformRiskItem risk(String target, String type, String description) {
        PlatformRiskItem item = new PlatformRiskItem();
        item.setTarget(target);
        item.setRiskType(type);
        item.setDescription(description);
        item.setStatus("PENDING");
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }
}
