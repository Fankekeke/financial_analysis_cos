package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ProductBrowseRecord;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.entity.WealthManagementProduct;
import com.fank.f1k2.business.dao.WealthManagementProductMapper;
import com.fank.f1k2.business.service.IProductBrowseRecordService;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.business.service.IWealthManagementProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WealthManagementProductServiceImpl extends ServiceImpl<WealthManagementProductMapper, WealthManagementProduct> implements IWealthManagementProductService {

    private final IProductBrowseRecordService productBrowseRecordService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取理财产品管理
     *
     * @param page      分页对象
     * @param queryFrom 理财产品管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<WealthManagementProduct> page, WealthManagementProduct queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }

    public List<WealthManagementProduct> recommendWealthManagementProduct(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));

        Long targetUserId = userId.longValue();

        Map<Long, Set<Integer>> userProductMap = buildUserProductMatrix();

        if (!userProductMap.containsKey(targetUserId) || userProductMap.get(targetUserId).isEmpty()) {
            return getPopularProducts(10);
        }

        Map<Long, Double> userSimilarityMap = calculateUserSimilarities(targetUserId, userProductMap);

        List<Long> similarUsers = findTopNSimilarUsers(userSimilarityMap, 5);

        Set<Integer> targetUserProducts = userProductMap.getOrDefault(targetUserId, Collections.emptySet());

        Map<Integer, Double> productScoreMap = calculateProductScores(similarUsers, targetUserProducts, userProductMap, userSimilarityMap);

        List<Integer> recommendedProductIds = productScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedProductIds.isEmpty()) {
            return getPopularProducts(10);
        }

        List<WealthManagementProduct> products = new ArrayList<>(this.listByIds(recommendedProductIds));

        Map<Integer, Integer> idOrderMap = new HashMap<>();
        for (int i = 0; i < recommendedProductIds.size(); i++) {
            idOrderMap.put(recommendedProductIds.get(i), i);
        }

        products.sort((p1, p2) -> Integer.compare(
                idOrderMap.getOrDefault(p1.getId(), Integer.MAX_VALUE),
                idOrderMap.getOrDefault(p2.getId(), Integer.MAX_VALUE)
        ));

        return products;
    }

    private Map<Long, Set<Integer>> buildUserProductMatrix() {
        List<ProductBrowseRecord> allRecords = productBrowseRecordService.list();

        Map<Long, Set<Integer>> userProductMap = new HashMap<>();
        for (ProductBrowseRecord record : allRecords) {
            Long userId = record.getUserId();
            Integer productId = record.getProductId();

            if (userId != null && productId != null) {
                userProductMap.computeIfAbsent(userId, k -> new HashSet<>()).add(productId);
            }
        }

        return userProductMap;
    }

    private Map<Long, Double> calculateUserSimilarities(Long targetUserId, Map<Long, Set<Integer>> userProductMap) {
        Map<Long, Double> similarityMap = new HashMap<>();

        Set<Integer> targetUserProducts = userProductMap.get(targetUserId);
        if (targetUserProducts == null || targetUserProducts.isEmpty()) {
            return similarityMap;
        }

        for (Map.Entry<Long, Set<Integer>> entry : userProductMap.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(targetUserId)) {
                continue;
            }

            Set<Integer> otherUserProducts = entry.getValue();
            double similarity = calculateJaccardSimilarity(targetUserProducts, otherUserProducts);

            if (similarity > 0) {
                similarityMap.put(otherUserId, similarity);
            }
        }

        return similarityMap;
    }

    private double calculateJaccardSimilarity(Set<Integer> user1Products, Set<Integer> user2Products) {
        if (user1Products.isEmpty() && user2Products.isEmpty()) {
            return 0.0;
        }

        Set<Integer> intersection = new HashSet<>(user1Products);
        intersection.retainAll(user2Products);

        Set<Integer> union = new HashSet<>(user1Products);
        union.addAll(user2Products);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    private List<Long> findTopNSimilarUsers(Map<Long, Double> userSimilarityMap, int n) {
        return userSimilarityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Map<Integer, Double> calculateProductScores(List<Long> similarUsers,
                                                        Set<Integer> targetUserProducts,
                                                        Map<Long, Set<Integer>> userProductMap,
                                                        Map<Long, Double> userSimilarityMap) {
        Map<Integer, Double> productScoreMap = new HashMap<>();

        for (Long similarUserId : similarUsers) {
            Set<Integer> similarUserProducts = userProductMap.get(similarUserId);
            if (similarUserProducts == null) {
                continue;
            }

            Double similarity = userSimilarityMap.getOrDefault(similarUserId, 0.0);

            for (Integer productId : similarUserProducts) {
                if (!targetUserProducts.contains(productId)) {
                    productScoreMap.merge(productId, similarity, Double::sum);
                }
            }
        }

        return productScoreMap;
    }

    private List<WealthManagementProduct> getPopularProducts(int limit) {
        LambdaQueryWrapper<ProductBrowseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ProductBrowseRecord::getProductId)
                .groupBy(ProductBrowseRecord::getProductId)
                .orderByDesc(ProductBrowseRecord::getProductId)
                .last("LIMIT " + limit);

        List<ProductBrowseRecord> records = productBrowseRecordService.list(wrapper);

        if (records.isEmpty()) {
            LambdaQueryWrapper<WealthManagementProduct> productWrapper = new LambdaQueryWrapper<>();
            productWrapper.eq(WealthManagementProduct::getProductStatus, "2")
                    .orderByDesc(WealthManagementProduct::getSortOrder)
                    .last("LIMIT " + limit);
            return this.list(productWrapper);
        }

        List<Integer> productIds = records.stream()
                .map(ProductBrowseRecord::getProductId)
                .distinct()
                .collect(Collectors.toList());

        return new ArrayList<>(this.listByIds(productIds));
    }
}
