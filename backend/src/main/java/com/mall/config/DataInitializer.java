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
import com.mall.mapper.AnnouncementMapper;
import com.mall.mapper.CategoryMapper;
import com.mall.mapper.ContentAuditItemMapper;
import com.mall.mapper.CouponMapper;
import com.mall.mapper.DictionaryItemMapper;
import com.mall.mapper.MarketingActivityMapper;
import com.mall.mapper.MarketingFlowRecordMapper;
import com.mall.mapper.MemberMessageMapper;
import com.mall.mapper.MerchantMapper;
import com.mall.mapper.PermissionItemMapper;
import com.mall.mapper.PlatformRiskItemMapper;
import com.mall.mapper.ProductDetailBlockMapper;
import com.mall.mapper.ProductMapper;
import com.mall.mapper.ProductSkuMapper;
import com.mall.mapper.PromotionRuleMapper;
import com.mall.mapper.RoleDefinitionMapper;
import com.mall.mapper.SeckillEventMapper;
import com.mall.mapper.SystemConfigItemMapper;
import com.mall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
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
    CommandLineRunner initData(UserMapper userMapper,
                               JdbcTemplate jdbcTemplate,
                               CategoryMapper categoryMapper,
                               ProductMapper productMapper,
                               ProductSkuMapper productSkuMapper,
                               ProductDetailBlockMapper productDetailBlockMapper,
                               CouponMapper couponMapper,
                               MemberMessageMapper memberMessageMapper,
                               MarketingActivityMapper marketingActivityMapper,
                               SystemConfigItemMapper systemConfigItemMapper,
                               PlatformRiskItemMapper platformRiskItemMapper,
                               SeckillEventMapper seckillEventMapper,
                               DictionaryItemMapper dictionaryItemMapper,
                               AnnouncementMapper announcementMapper,
                               MerchantMapper merchantMapper,
                               RoleDefinitionMapper roleDefinitionMapper,
                               PermissionItemMapper permissionItemMapper,
                               ContentAuditItemMapper contentAuditItemMapper,
                               PromotionRuleMapper promotionRuleMapper,
                               MarketingFlowRecordMapper marketingFlowRecordMapper) {
        return args -> {
            
            if (merchantMapper.count() == 0) {
                merchantMapper.saveAll(List.of(
                        merchant("商家1", "18800000001", "ACTIVE"),
                        merchant("商家2", "18800000002", "ACTIVE"),
                        merchant("商家3", "18800000003", "ACTIVE"),
                        merchant("商家4", "18800000004", "ACTIVE"),
                        merchant("商家5", "18800000005", "ACTIVE"),
                        merchant("商家6", "18800000006", "ACTIVE")
                ));
            }
            List<Merchant> allMerchants = merchantMapper.findAll();
            Merchant digital = allMerchants.get(0);
            Merchant lifestyle = allMerchants.get(1);
            Merchant apple = allMerchants.get(2);
            Merchant sony = allMerchants.get(3);
            Merchant nike = allMerchants.get(4);
            Merchant fresh = allMerchants.get(5);

            relaxLegacyUserRoleConstraint(jdbcTemplate);
            ensureUser(userMapper, "admin", "123", "admin@mall.local", "平台管理员", UserRole.ADMIN, 1000, "ADMIN", null);
            ensureUser(userMapper, "merchant1", "123", "m1@mall.local", "商家1管理员", UserRole.MERCHANT, 300, "MERCHANT", digital);
            ensureUser(userMapper, "merchant", "merchant123", "merchant@mall.local", "演示商家", UserRole.MERCHANT, 300, "MERCHANT", digital);
            ensureUser(userMapper, "merchant2", "123", "m2@mall.local", "商家2管理员", UserRole.MERCHANT, 300, "MERCHANT", lifestyle);
            ensureUser(userMapper, "merchant3", "123", "m3@mall.local", "商家3管理员", UserRole.MERCHANT, 300, "MERCHANT", apple);
            ensureUser(userMapper, "merchant4", "123", "m4@mall.local", "商家4管理员", UserRole.MERCHANT, 300, "MERCHANT", sony);
            ensureUser(userMapper, "merchant5", "123", "m5@mall.local", "商家5管理员", UserRole.MERCHANT, 300, "MERCHANT", nike);
            ensureUser(userMapper, "merchant6", "123", "m6@mall.local", "商家6管理员", UserRole.MERCHANT, 300, "MERCHANT", fresh);
            ensureUser(userMapper, "demo", "123", "demo@mall.local", "演示用户", UserRole.USER, 260, "GOLD", digital);
            ensureUser(userMapper, "user_alice", "123", "alice@mall.local", "爱丽丝", UserRole.USER, 100, "SILVER", apple);
            ensureUser(userMapper, "user_bob", "123", "bob@mall.local", "鲍勃", UserRole.USER, 50, "BASIC", sony);
            ensureUser(userMapper, "user_charlie", "123", "charlie@mall.local", "查理", UserRole.USER, 500, "PLATINUM", nike);
            userMapper.findAll().forEach(item -> {
                boolean changed = false;
                if (item.getPoints() == null) {
                    item.setPoints(0);
                    changed = true;
                }
                if (item.getMemberLevel() == null || item.getMemberLevel().isBlank()) {
                    item.setMemberLevel(item.getRole() == UserRole.ADMIN ? "ADMIN" : "BASIC");
                    changed = true;
                }
                if (changed) {
                    userMapper.save(item);
                }
            });

            if (categoryMapper.count() == 0) {
                categoryMapper.saveAll(List.of(category("数码", 1), category("服饰", 2), category("家居", 3)));
            }

            if (productMapper.count() == 0) {
                List<Category> categories = categoryMapper.findAllByOrderBySortOrderAscIdAsc();
                Category categoryDigital = categories.get(0);
                Category categoryFashion = categories.get(1);
                Category categoryHome = categories.get(2);

                productMapper.saveAll(List.of(
                        product("降噪耳机", "Comfortable studio sound for daily listening",
                                "A demo product with long battery life and balanced sound signature.",
                                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("799.00"), 50, 24, categoryDigital, "Hot Sale", sony),
                        product("4K Smart Monitor", "Crisp panel for work and entertainment",
                                "27-inch monitor designed for productivity, gaming, and media.",
                                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("1699.00"), 20, 15, categoryDigital, "Presale", digital),
                        product("Minimal Sneaker", "Clean everyday design with soft cushioning",
                                "Modern sneaker with breathable upper and flexible sole.",
                                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("359.00"), 80, 41, categoryFashion, "Group Buy", nike),
                        product("Ceramic Coffee Set", "Warm-toned tableware for calm mornings",
                                "Handcrafted feel with durable glazed finish for daily use.",
                                "https://images.unsplash.com/photo-1517705008128-361805f42e86?auto=format&fit=crop&w=900&q=80",
                                new BigDecimal("229.00"), 65, 12, categoryHome, "Bundle", lifestyle)
                ));
            }

            productMapper.findAll().forEach(product -> {
                boolean changed = false;
                if (product.getSkuCode() == null) {
                    product.setSkuCode("SKU-" + product.getId());
                    changed = true;
                }
                if (product.getSpec() == null) {
                    product.setSpec("默认规格");
                    changed = true;
                }
                if (product.getPromotionTag() == null) {
                    product.setPromotionTag("首页推荐");
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
                    productMapper.save(product);
                }
            });

            if (productSkuMapper.count() == 0) {
                productMapper.findAll().forEach(product -> productSkuMapper.saveAll(List.of(
                        sku(product, product.getSkuCode() + "-标准版", product.getSpec(), product.getPrice(), product.getStock()),
                        sku(product, product.getSkuCode() + "-PLUS", product.getSpec() + " 升级版",
                                product.getPrice().add(new BigDecimal("80.00")), Math.max(5, product.getStock() / 2))
                )));
            }

            if (productDetailBlockMapper.count() == 0) {
                productMapper.findAll().forEach(product -> productDetailBlockMapper.saveAll(List.of(
                        detail(product, "TEXT", product.getDescription(), 1),
                        detail(product, "TEXT", "订单中心提供质保、维修等售后服务。", 2)
                )));
            }

            if (couponMapper.count() == 0) {
                couponMapper.saveAll(List.of(
                        coupon("新客30元立减券", new BigDecimal("199.00"), new BigDecimal("30.00"), 100),
                        coupon("满699减80元券", new BigDecimal("699.00"), new BigDecimal("80.00"), 50),
                        coupon("会员专享120元券", new BigDecimal("1299.00"), new BigDecimal("120.00"), 30)
                ));
            }

            if (memberMessageMapper.count() == 0) {
                UserAccount user = userMapper.findByUsername("demo").orElseThrow();
                memberMessageMapper.saveAll(List.of(
                        message(user, "新客礼包已到账", "优惠券、积分和会员权益已发放到演示账户。"),
                        message(user, "订单发货提醒", "付款后您可以查看物流详情和售后入口。")
                ));
            }

            if (marketingActivityMapper.count() == 0) {
                marketingActivityMapper.saveAll(List.of(
                        activity("618满减主会场", "FULL_REDUCTION", "满699减80，可叠加使用会员积分。", "APPROVED"),
                        activity("数码限时秒杀", "SECKILL", "限量秒杀，独立库存池，用户限购。", "RUNNING"),
                        activity("拼团拉新活动", "GROUP_BUY", "两人成团，拼团失败自动退款。", "PENDING_REVIEW")
                ));
            }

            if (systemConfigItemMapper.count() == 0) {
                systemConfigItemMapper.saveAll(List.of(
                        config("home_popup", "新用户优惠券弹窗", "首页弹窗活动配置"),
                        config("channel_wechat", "enabled", "微信模拟支付开关"),
                        config("content_audit", "manual+keyword", "内容审核策略配置")
                ));
            }

            if (platformRiskItemMapper.count() == 0) {
                platformRiskItemMapper.saveAll(List.of(
                        risk("merchant-demo", "PRICE_ABNORMAL", "商品价格低于平台风险阈值。"),
                        risk("review-10001", "CONTENT_AUDIT", "评论命中敏感词，需要人工审核。")
                ));
            }

            if (seckillEventMapper.count() == 0) {
                Product product = productMapper.findByActiveTrueOrderByIdDesc().getFirst();
                SeckillEvent event = new SeckillEvent();
                event.setProduct(product);
                event.setSeckillPrice(product.getPrice().multiply(new BigDecimal("0.80")));
                event.setStock(Math.min(10, product.getStock()));
                event.setSold(0);
                event.setActive(true);
                event.setStartAt(LocalDateTime.now().minusHours(1));
                event.setEndAt(LocalDateTime.now().plusDays(1));
                seckillEventMapper.save(event);
            }

            if (dictionaryItemMapper.count() == 0) {
                dictionaryItemMapper.saveAll(List.of(
                        dictionary("ORDER_STATUS", "PAID", "Paid"),
                        dictionary("PAY_CHANNEL", "ALIPAY", "Alipay Mock"),
                        dictionary("PAY_CHANNEL", "WECHAT", "WeChat Mock")
                ));
            }

            if (announcementMapper.count() == 0) {
                announcementMapper.saveAll(List.of(
                        announcement("Platform Maintenance", "Demo announcement for configuration center.", false),
                        announcement("Coupon Popup", "Claim coupons before checkout.", true)
                ));
            }



            if (roleDefinitionMapper.count() == 0) {
                roleDefinitionMapper.saveAll(List.of(
                        role("ADMIN", "Platform Admin", "all", "all"),
                        role("MERCHANT", "Merchant", "products,orders,marketing,reports", "create,update,ship,audit"),
                        role("USER", "Customer", "home,cart,orders,member", "favorite,checkout,pay")
                ));
            }

            if (permissionItemMapper.count() == 0) {
                permissionItemMapper.saveAll(List.of(
                        permission("product:create", "Create Product", "BUTTON"),
                        permission("order:ship", "Ship Order", "BUTTON"),
                        permission("config:manage", "Manage Config", "MENU")
                ));
            }

            if (contentAuditItemMapper.count() == 0) {
                contentAuditItemMapper.saveAll(List.of(
                        audit("REVIEW", "review-10001", "This review waits for manual approval."),
                        audit("PRODUCT_DETAIL", "product-demo", "Product rich text content waits for platform audit.")
                ));
            }

            if (promotionRuleMapper.count() == 0) {
                promotionRuleMapper.saveAll(List.of(
                        promotion("Full 699 Save 80", "FULL_REDUCTION", new BigDecimal("699.00"), new BigDecimal("80.00"),
                                "Applies in checkout when threshold is met."),
                        promotion("Presale Deposit", "PRESALE", new BigDecimal("100.00"), BigDecimal.ZERO,
                                "Deposit order first, final payment reminder later.")
                ));
            }

            if (marketingFlowRecordMapper.count() == 0) {
                marketingFlowRecordMapper.saveAll(List.of(
                        flow("GROUP_BUY", "Sneaker 2-person group", new BigDecimal("299.00"), "RUNNING",
                                "Create group, join group, full group succeeds."),
                        flow("SECKILL", "Digital flash sale", new BigDecimal("599.00"), "RUNNING",
                                "Redis pre-deduct stock and async order creation in microservice mode.")
                ));
            }
        };
    }

    private UserAccount user(String username, String password, String email, String displayName,
                             UserRole role, int points, String level, Merchant merchant) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setPoints(points);
        user.setMemberLevel(level);
        user.setMerchant(merchant);
        return user;
    }

    private void ensureUser(UserMapper userMapper, String username, String password, String email,
                            String displayName, UserRole role, int points, String level, Merchant merchant) {
        userMapper.findByUsername(username).ifPresentOrElse(existing -> {
            boolean changed = false;
            if (existing.getRole() != role) {
                existing.setRole(role);
                changed = true;
            }
            if (existing.getDisplayName() == null || existing.getDisplayName().isBlank()) {
                existing.setDisplayName(displayName);
                changed = true;
            }
            if (existing.getEmail() == null || existing.getEmail().isBlank()) {
                existing.setEmail(email);
                changed = true;
            }
            if (existing.getPoints() == null) {
                existing.setPoints(points);
                changed = true;
            }
            if (existing.getMemberLevel() == null || existing.getMemberLevel().isBlank()) {
                existing.setMemberLevel(level);
                changed = true;
            }
            if (!passwordEncoder.matches(password, existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(password));
                changed = true;
            }
            if (changed) {
                userMapper.save(existing);
            }
        }, () -> userMapper.save(user(username, password, email, displayName, role, points, level, merchant)));
    }

    private void relaxLegacyUserRoleConstraint(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.execute("alter table users alter column role varchar(32) not null");
        } catch (Exception ignored) {
            // Non-H2 databases or fresh schemas may not need this compatibility migration.
        }
    }

    private Category category(String name, int sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setSortOrder(sortOrder);
        return category;
    }

    private Product product(String name, String subtitle, String description, String imageUrl,
                            BigDecimal price, int stock, int sales, Category category, String promotionTag, Merchant merchant) {
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
        product.setMerchant(merchant);
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
