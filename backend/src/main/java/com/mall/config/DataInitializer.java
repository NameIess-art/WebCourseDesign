package com.mall.config;

import com.mall.entity.Announcement;
import com.mall.entity.Category;
import com.mall.entity.ContentAuditItem;
import com.mall.entity.Coupon;
import com.mall.entity.DictionaryItem;
import com.mall.entity.MarketingActivity;
import com.mall.entity.MarketingFlowRecord;
import com.mall.entity.MemberMessage;
import com.mall.entity.Merchant;
import com.mall.entity.PermissionItem;
import com.mall.entity.PlatformRiskItem;
import com.mall.entity.Product;
import com.mall.entity.ProductDetailBlock;
import com.mall.entity.ProductSku;
import com.mall.entity.PromotionRule;
import com.mall.entity.RoleDefinition;
import com.mall.entity.SeckillEvent;
import com.mall.entity.SystemConfigItem;
import com.mall.entity.UserAccount;
import com.mall.enums.UserRole;
import com.mall.repository.AnnouncementRepository;
import com.mall.repository.CategoryRepository;
import com.mall.repository.ContentAuditItemRepository;
import com.mall.repository.CouponRepository;
import com.mall.repository.DictionaryItemRepository;
import com.mall.repository.MarketingActivityRepository;
import com.mall.repository.MarketingFlowRecordRepository;
import com.mall.repository.MemberMessageRepository;
import com.mall.repository.MerchantRepository;
import com.mall.repository.PermissionItemRepository;
import com.mall.repository.PlatformRiskItemRepository;
import com.mall.repository.ProductDetailBlockRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.ProductSkuRepository;
import com.mall.repository.PromotionRuleRepository;
import com.mall.repository.RoleDefinitionRepository;
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
                               ProductSkuRepository productSkuRepository,
                               ProductDetailBlockRepository productDetailBlockRepository,
                               CouponRepository couponRepository,
                               MemberMessageRepository memberMessageRepository,
                               MarketingActivityRepository marketingActivityRepository,
                               SystemConfigItemRepository systemConfigItemRepository,
                               PlatformRiskItemRepository platformRiskItemRepository,
                               SeckillEventRepository seckillEventRepository,
                               DictionaryItemRepository dictionaryItemRepository,
                               AnnouncementRepository announcementRepository,
                               MerchantRepository merchantRepository,
                               RoleDefinitionRepository roleDefinitionRepository,
                               PermissionItemRepository permissionItemRepository,
                               ContentAuditItemRepository contentAuditItemRepository,
                               PromotionRuleRepository promotionRuleRepository,
                               MarketingFlowRecordRepository marketingFlowRecordRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                UserAccount admin = user("admin", "admin123", "admin@mall.local", "Mall Admin", UserRole.ADMIN, 1000, "ADMIN");
                UserAccount merchant = user("merchant", "merchant123", "merchant@mall.local", "Demo Merchant", UserRole.MERCHANT, 300, "MERCHANT");
                UserAccount user = user("demo", "demo123", "demo@mall.local", "Demo User", UserRole.USER, 260, "GOLD");
                userRepository.saveAll(List.of(admin, merchant, user));
            } else {
                userRepository.findAll().forEach(item -> {
                    if (item.getPoints() == null) {
                        item.setPoints(0);
                    }
                    if (item.getMemberLevel() == null || item.getMemberLevel().isBlank()) {
                        item.setMemberLevel(item.getRole() == UserRole.ADMIN ? "ADMIN" : "BASIC");
                    }
                });
            }

            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(category("Digital", 1), category("Fashion", 2), category("Home", 3)));
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
                                new BigDecimal("799.00"), 50, 24, digital, "Hot Sale"),
                        product("4K Smart Monitor", "Crisp panel for work and entertainment",
                                "27-inch monitor designed for productivity, gaming, and media.",
                                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("1699.00"), 20, 15, digital, "Presale"),
                        product("Minimal Sneaker", "Clean everyday design with soft cushioning",
                                "Modern sneaker with breathable upper and flexible sole.",
                                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("359.00"), 80, 41, fashion, "Group Buy"),
                        product("Ceramic Coffee Set", "Warm-toned tableware for calm mornings",
                                "Handcrafted feel with durable glazed finish for daily use.",
                                "https://images.unsplash.com/photo-1517705008128-361805f42e86?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("229.00"), 65, 12, home, "Bundle")
                ));
            }

            productRepository.findAll().forEach(product -> {
                boolean changed = false;
                if (product.getSkuCode() == null) {
                    product.setSkuCode("SKU-" + product.getId());
                    changed = true;
                }
                if (product.getSpec() == null) {
                    product.setSpec("Default / Standard");
                    changed = true;
                }
                if (product.getPromotionTag() == null) {
                    product.setPromotionTag("Home Recommendation");
                    changed = true;
                }
                if (product.getFavoriteCount() == null) {
                    product.setFavoriteCount(0);
                    changed = true;
                }
                if (product.getQuestionCount() == null) {
                    product.setQuestionCount(0);
                    changed = true;
                }
                if (product.getRating() == null) {
                    product.setRating(new BigDecimal("4.80"));
                    changed = true;
                }
                if (changed) {
                    productRepository.save(product);
                }
            });

            if (productSkuRepository.count() == 0) {
                productRepository.findAll().forEach(product -> productSkuRepository.saveAll(List.of(
                        sku(product, product.getSkuCode() + "-STD", product.getSpec(), product.getPrice(), product.getStock()),
                        sku(product, product.getSkuCode() + "-PLUS", product.getSpec() + " Plus",
                                product.getPrice().add(new BigDecimal("80.00")), Math.max(5, product.getStock() / 2))
                )));
            }

            if (productDetailBlockRepository.count() == 0) {
                productRepository.findAll().forEach(product -> productDetailBlockRepository.saveAll(List.of(
                        detail(product, "TEXT", product.getDescription(), 1),
                        detail(product, "TEXT", "Warranty, service and after-sale support are available in the order center.", 2)
                )));
            }

            if (couponRepository.count() == 0) {
                couponRepository.saveAll(List.of(
                        coupon("New User Coupon 30", new BigDecimal("199.00"), new BigDecimal("30.00"), 100),
                        coupon("Full Reduction 80", new BigDecimal("699.00"), new BigDecimal("80.00"), 50),
                        coupon("Member Exclusive 120", new BigDecimal("1299.00"), new BigDecimal("120.00"), 30)
                ));
            }

            if (memberMessageRepository.count() == 0) {
                UserAccount user = userRepository.findByUsername("demo").orElseThrow();
                memberMessageRepository.saveAll(List.of(
                        message(user, "New user gift is ready", "Coupons, points and member rights are ready for demo."),
                        message(user, "Order fulfillment reminder", "After payment you can view logistics and after-sale entry.")
                ));
            }

            if (marketingActivityRepository.count() == 0) {
                marketingActivityRepository.saveAll(List.of(
                        activity("618 Full Reduction Venue", "FULL_REDUCTION", "Spend 699 save 80, can stack member points.", "APPROVED"),
                        activity("Digital Flash Sale", "SECKILL", "Limited stock, independent seckill stock pool, user rate limit.", "RUNNING"),
                        activity("Group Buy Growth Campaign", "GROUP_BUY", "Two members form a group, failed group can be refunded.", "PENDING_REVIEW")
                ));
            }

            if (systemConfigItemRepository.count() == 0) {
                systemConfigItemRepository.saveAll(List.of(
                        config("home_popup", "New user coupon popup", "Home popup configuration"),
                        config("channel_wechat", "enabled", "WeChat mock payment switch"),
                        config("content_audit", "manual+keyword", "Content audit strategy")
                ));
            }

            if (platformRiskItemRepository.count() == 0) {
                platformRiskItemRepository.saveAll(List.of(
                        risk("merchant-demo", "PRICE_ABNORMAL", "Product price is below platform risk threshold."),
                        risk("review-10001", "CONTENT_AUDIT", "Review hit sensitive keyword and requires manual review.")
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

            if (dictionaryItemRepository.count() == 0) {
                dictionaryItemRepository.saveAll(List.of(
                        dictionary("ORDER_STATUS", "PAID", "Paid"),
                        dictionary("PAY_CHANNEL", "ALIPAY", "Alipay Mock"),
                        dictionary("PAY_CHANNEL", "WECHAT", "WeChat Mock")
                ));
            }

            if (announcementRepository.count() == 0) {
                announcementRepository.saveAll(List.of(
                        announcement("Platform Maintenance", "Demo announcement for configuration center.", false),
                        announcement("Coupon Popup", "Claim coupons before checkout.", true)
                ));
            }

            if (merchantRepository.count() == 0) {
                merchantRepository.saveAll(List.of(
                        merchant("Digital Flagship Store", "18800000001", "ACTIVE"),
                        merchant("Lifestyle Store", "18800000002", "ACTIVE")
                ));
            }

            if (roleDefinitionRepository.count() == 0) {
                roleDefinitionRepository.saveAll(List.of(
                        role("ADMIN", "Platform Admin", "all", "all"),
                        role("MERCHANT", "Merchant", "products,orders,marketing,reports", "create,update,ship,audit"),
                        role("USER", "Customer", "home,cart,orders,member", "favorite,checkout,pay")
                ));
            }

            if (permissionItemRepository.count() == 0) {
                permissionItemRepository.saveAll(List.of(
                        permission("product:create", "Create Product", "BUTTON"),
                        permission("order:ship", "Ship Order", "BUTTON"),
                        permission("config:manage", "Manage Config", "MENU")
                ));
            }

            if (contentAuditItemRepository.count() == 0) {
                contentAuditItemRepository.saveAll(List.of(
                        audit("REVIEW", "review-10001", "This review waits for manual approval."),
                        audit("PRODUCT_DETAIL", "product-demo", "Product rich text content waits for platform audit.")
                ));
            }

            if (promotionRuleRepository.count() == 0) {
                promotionRuleRepository.saveAll(List.of(
                        promotion("Full 699 Save 80", "FULL_REDUCTION", new BigDecimal("699.00"), new BigDecimal("80.00"),
                                "Applies in checkout when threshold is met."),
                        promotion("Presale Deposit", "PRESALE", new BigDecimal("100.00"), BigDecimal.ZERO,
                                "Deposit order first, final payment reminder later.")
                ));
            }

            if (marketingFlowRecordRepository.count() == 0) {
                marketingFlowRecordRepository.saveAll(List.of(
                        flow("GROUP_BUY", "Sneaker 2-person group", new BigDecimal("299.00"), "RUNNING",
                                "Create group, join group, full group succeeds."),
                        flow("SECKILL", "Digital flash sale", new BigDecimal("599.00"), "RUNNING",
                                "Redis pre-deduct stock and async order creation in microservice mode.")
                ));
            }
        };
    }

    private UserAccount user(String username, String password, String email, String displayName,
                             UserRole role, int points, String level) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setPoints(points);
        user.setMemberLevel(level);
        return user;
    }

    private Category category(String name, int sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setSortOrder(sortOrder);
        return category;
    }

    private Product product(String name, String subtitle, String description, String imageUrl,
                            BigDecimal price, int stock, int sales, Category category, String promotionTag) {
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
        product.setPromotionTag(promotionTag);
        product.setFavoriteCount(0);
        product.setQuestionCount(0);
        product.setRating(new BigDecimal("4.80"));
        product.setCategory(category);
        return product;
    }

    private ProductSku sku(Product product, String skuCode, String specName, BigDecimal price, int stock) {
        ProductSku sku = new ProductSku();
        sku.setProduct(product);
        sku.setSkuCode(skuCode);
        sku.setSpecName(specName);
        sku.setPrice(price);
        sku.setStock(stock);
        sku.setActive(true);
        return sku;
    }

    private ProductDetailBlock detail(Product product, String type, String content, int sortOrder) {
        ProductDetailBlock block = new ProductDetailBlock();
        block.setProduct(product);
        block.setBlockType(type);
        block.setContent(content);
        block.setSortOrder(sortOrder);
        return block;
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

    private DictionaryItem dictionary(String type, String key, String value) {
        DictionaryItem item = new DictionaryItem();
        item.setDictType(type);
        item.setDictKey(key);
        item.setDictValue(value);
        return item;
    }

    private Announcement announcement(String title, String content, boolean popup) {
        Announcement item = new Announcement();
        item.setTitle(title);
        item.setContent(content);
        item.setPopup(popup);
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }

    private Merchant merchant(String name, String phone, String status) {
        Merchant item = new Merchant();
        item.setName(name);
        item.setContactPhone(phone);
        item.setStatus(status);
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }

    private RoleDefinition role(String code, String name, String menus, String buttons) {
        RoleDefinition item = new RoleDefinition();
        item.setCode(code);
        item.setName(name);
        item.setMenuPermissions(menus);
        item.setButtonPermissions(buttons);
        return item;
    }

    private PermissionItem permission(String code, String name, String type) {
        PermissionItem item = new PermissionItem();
        item.setCode(code);
        item.setName(name);
        item.setPermissionType(type);
        return item;
    }

    private ContentAuditItem audit(String type, String target, String content) {
        ContentAuditItem item = new ContentAuditItem();
        item.setContentType(type);
        item.setTarget(target);
        item.setContent(content);
        item.setStatus("PENDING");
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }

    private PromotionRule promotion(String title, String type, BigDecimal threshold, BigDecimal discount, String ruleText) {
        PromotionRule item = new PromotionRule();
        item.setTitle(title);
        item.setPromotionType(type);
        item.setThresholdAmount(threshold);
        item.setDiscountAmount(discount);
        item.setRuleText(ruleText);
        item.setStatus("ACTIVE");
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }

    private MarketingFlowRecord flow(String type, String title, BigDecimal amount, String status, String description) {
        MarketingFlowRecord item = new MarketingFlowRecord();
        item.setFlowType(type);
        item.setTitle(title);
        item.setAmount(amount);
        item.setStatus(status);
        item.setDescription(description);
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }
}
