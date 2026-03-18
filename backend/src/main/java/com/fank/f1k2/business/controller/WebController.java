package com.fank.f1k2.business.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.*;
import com.fank.f1k2.business.service.*;
import com.fank.f1k2.common.utils.R;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/web")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebController {

    private final IUserInfoService userInfoService;

    private final IAccountsService accountsService;

    private final IFixedAssetsService fixedAssetsService;

    private final IDebtsService debtsService;

    private final ITransactionsService transactionsService;

    private final ICategoriesService categoriesService;

    private final IBudgetsService budgetsService;

    private final ISavingGoalsService savingGoalsService;

    /**
     * 查询资产总览
     * <p>
     * totalAssets: 总资产
     * totalLiabilities: 总负债
     * netAssets: 净资产
     * accountsCount: 账户数量
     * fixedAssetsCount: 固定资产数量
     * debtsCount: 负债数量
     *
     * @return 响应结果
     */
    @RequestMapping("/queryNetAssetDashboard")
    public R queryNetAssetDashboard(Integer userId) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));

        List<Accounts> accountsList = accountsService.list(Wrappers.<Accounts>lambdaQuery()
                .eq(Accounts::getUserId, userId)
                .eq(Accounts::getStatus, 1));

        List<FixedAssets> fixedAssetsList = fixedAssetsService.list(Wrappers.<FixedAssets>lambdaQuery()
                .eq(FixedAssets::getUserId, userId));

        List<Debts> debtsList = debtsService.list(Wrappers.<Debts>lambdaQuery()
                .eq(Debts::getUserId, userId));

        BigDecimal totalAssets = accountsList.stream()
                .map(Accounts::getBalance)
                .filter(balance -> balance != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFixedAssets = fixedAssetsList.stream()
                .map(FixedAssets::getCurrentValue)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAssets = totalAssets.add(totalFixedAssets);

        BigDecimal totalLiabilities = debtsList.stream()
                .map(Debts::getRemainingAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netAssets = totalAssets.subtract(totalLiabilities);

        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("totalAssets", totalAssets);
        dashboardData.put("totalLiabilities", totalLiabilities);
        dashboardData.put("netAssets", netAssets);
        dashboardData.put("accountsCount", accountsList.size());
        dashboardData.put("fixedAssetsCount", fixedAssetsList.size());
        dashboardData.put("debtsCount", debtsList.size());

        return R.ok(dashboardData);
    }

    /**
     * 多维度统计
     * <p>
     * pieChart: 饼图数据 - 支出占比
     * lineChart: 折线图数据 - 收支趋势
     * heatmap: 热力图数据 - 消费频率
     *
     * @param userId 用户 ID
     * @param months 统计月数（默认 6 个月）
     * @return 响应结果
     */
    @RequestMapping("/queryMultidimensionalStatistics")
    public R queryMultidimensionalStatistics(Integer userId, Integer months) {
        if (months == null || months <= 0) {
            months = 6;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusMonths(months);

        List<Transactions> transactionsList = transactionsService.list(Wrappers.<Transactions>lambdaQuery()
                .eq(Transactions::getUserId, userId)
                .ge(Transactions::getTransactionTime, startTime)
                .orderByAsc(Transactions::getTransactionTime));

        Map<String, Object> result = new HashMap<>();
        result.put("pieChart", buildPieChartData(userId, transactionsList));
        result.put("lineChart", buildLineChartData(userId, transactionsList, months));
        result.put("heatmap", buildHeatmapData(userId, transactionsList));

        return R.ok(result);
    }

    /**
     * 构建饼图数据（支出占比）
     */
    private List<Map<String, Object>> buildPieChartData(Integer userId, List<Transactions> transactionsList) {
        List<Categories> categoriesList = categoriesService.list(Wrappers.<Categories>lambdaQuery()
                .eq(Categories::getUserId, userId));

        Map<Integer, Categories> categoriesMap = categoriesList.stream().collect(Collectors.toMap(Categories::getId, c -> c));

        Map<String, BigDecimal> categoryAmountMap = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getCategoryId(),
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> {
                            Categories cat = categoriesMap.get(entry.getKey());
                            return cat != null ? cat.getName() : "其他";
                        },
                        Map.Entry::getValue,
                        BigDecimal::add
                ));

        List<Map<String, Object>> pieData = new ArrayList<>();
        categoryAmountMap.forEach((category, amount) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", category);
            item.put("value", amount);
            pieData.add(item);
        });

        return pieData;
    }

    /**
     * 构建折线图数据（收支趋势）
     */
    private Map<String, Object> buildLineChartData(Integer userId, List<Transactions> transactionsList, int months) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, BigDecimal> incomeMap = new HashMap<>();
        Map<String, BigDecimal> expenseMap = new HashMap<>();

        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthStart = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

            String monthKey = monthStart.format(formatter);

            BigDecimal monthIncome = transactionsList.stream()
                    .filter(t -> "income".equals(t.getTransactionType()))
                    .filter(t -> t.getTransactionTime().isAfter(monthStart.minusNanos(1)) && t.getTransactionTime().isBefore(monthEnd.plusNanos(1)))
                    .map(Transactions::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthExpense = transactionsList.stream()
                    .filter(t -> "expense".equals(t.getTransactionType()))
                    .filter(t -> t.getTransactionTime().isAfter(monthStart.minusNanos(1)) && t.getTransactionTime().isBefore(monthEnd.plusNanos(1)))
                    .map(Transactions::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            incomeMap.put(monthKey, monthIncome);
            expenseMap.put(monthKey, monthExpense);
        }

        Map<String, Object> lineChartData = new HashMap<>();
        lineChartData.put("categories", new ArrayList<>(incomeMap.keySet()));
        lineChartData.put("income", new ArrayList<>(incomeMap.values()));
        lineChartData.put("expense", new ArrayList<>(expenseMap.values()));

        return lineChartData;
    }

    /**
     * 构建热力图数据（消费频率）
     */
    private List<Map<String, Object>> buildHeatmapData(Integer userId, List<Transactions> transactionsList) {
        Map<String, Long> dateCountMap = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate().toString(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> heatmapData = new ArrayList<>();
        dateCountMap.forEach((date, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("count", count);
            item.put("value", count.intValue());
            heatmapData.add(item);
        });

        return heatmapData;
    }

    /**
     * 查询用户指定年月每日收支统计
     * <p>
     * dailyData: 每日收支明细
     * monthSummary: 月度汇总
     * expenditureTrend: 支出趋势分析
     * incomeTrend: 收入趋势分析
     * categoryRanking: 支出类别排名
     *
     * @param userId 用户 ID
     * @param year   年份
     * @param month  月份
     * @return 响应结果
     */
    @RequestMapping("/queryExpenditureIncomeByMonth")
    public R queryExpenditureIncomeByMonth(Integer userId, int year, int month) {
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusNanos(1);

        List<Transactions> transactionsList = transactionsService.list(Wrappers.<Transactions>lambdaQuery()
                .eq(Transactions::getUserId, userId)
                .ge(Transactions::getTransactionTime, startTime)
                .le(Transactions::getTransactionTime, endTime)
                .orderByAsc(Transactions::getTransactionTime));

        Map<String, Object> result = new HashMap<>();
        result.put("dailyData", buildDailyData(transactionsList));
        result.put("monthSummary", buildMonthSummary(transactionsList));
        result.put("expenditureTrend", buildExpenditureTrend(transactionsList));
        result.put("incomeTrend", buildIncomeTrend(transactionsList));
        result.put("categoryRanking", buildCategoryRanking(userId, transactionsList));

        return R.ok(result);
    }

    /**
     * 构建每日收支数据
     */
    private List<Map<String, Object>> buildDailyData(List<Transactions> transactionsList) {
        Map<LocalDate, List<Transactions>> dailyTransactions = transactionsList.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionTime().toLocalDate()));

        List<Map<String, Object>> dailyData = new ArrayList<>();
        dailyTransactions.forEach((date, transactions) -> {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());

            BigDecimal dailyIncome = transactions.stream()
                    .filter(t -> "income".equals(t.getTransactionType()))
                    .map(Transactions::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal dailyExpense = transactions.stream()
                    .filter(t -> "expense".equals(t.getTransactionType()))
                    .map(Transactions::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dayData.put("income", dailyIncome);
            dayData.put("expense", dailyExpense);
            dayData.put("netIncome", dailyIncome.subtract(dailyExpense));
            dayData.put("transactionCount", transactions.size());
            dayData.put("incomeCount", transactions.stream().filter(t -> "income".equals(t.getTransactionType())).count());
            dayData.put("expenseCount", transactions.stream().filter(t -> "expense".equals(t.getTransactionType())).count());

            dailyData.add(dayData);
        });

        dailyData.sort(Comparator.comparing(d -> d.get("date").toString()));

        return dailyData;
    }

    /**
     * 构建月度汇总数据
     */
    private Map<String, Object> buildMonthSummary(List<Transactions> transactionsList) {
        Map<String, Object> summary = new HashMap<>();

        BigDecimal totalIncome = transactionsList.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netIncome", totalIncome.subtract(totalExpense));
        summary.put("totalTransactionCount", transactionsList.size());
        summary.put("incomeCount", transactionsList.stream().filter(t -> "income".equals(t.getTransactionType())).count());
        summary.put("expenseCount", transactionsList.stream().filter(t -> "expense".equals(t.getTransactionType())).count());

        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            summary.put("expenseIncomeRatio", totalExpense.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP));
        } else {
            summary.put("expenseIncomeRatio", BigDecimal.ZERO);
        }

        summary.put("avgDailyExpense", totalExpense.divide(new BigDecimal(transactionsList.stream()
                .map(t -> t.getTransactionTime().toLocalDate())
                .distinct()
                .count()), 2, BigDecimal.ROUND_HALF_UP));

        return summary;
    }

    /**
     * 构建支出趋势分析
     */
    private Map<String, Object> buildExpenditureTrend(List<Transactions> transactionsList) {
        List<Map<String, Object>> trend = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate(),
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", entry.getKey().toString());
                    item.put("amount", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", trend.stream().map(d -> d.get("date")).collect(Collectors.toList()));
        trendData.put("amounts", trend.stream().map(d -> d.get("amount")).collect(Collectors.toList()));

        if (!trend.isEmpty()) {
            trendData.put("maxExpenseDate", trend.stream().max(Comparator.comparing(d -> (BigDecimal) d.get("amount"))).get().get("date"));
            trendData.put("minExpenseDate", trend.stream().min(Comparator.comparing(d -> (BigDecimal) d.get("amount"))).get().get("date"));
            trendData.put("avgDailyExpense", new BigDecimal(trend.size()).compareTo(BigDecimal.ZERO) > 0 ?
                    trend.stream().map(d -> (BigDecimal) d.get("amount")).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(new BigDecimal(trend.size()), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        }

        return trendData;
    }

    /**
     * 构建收入趋势分析
     */
    private Map<String, Object> buildIncomeTrend(List<Transactions> transactionsList) {
        List<Map<String, Object>> trend = transactionsList.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate(),
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", entry.getKey().toString());
                    item.put("amount", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", trend.stream().map(d -> d.get("date")).collect(Collectors.toList()));
        trendData.put("amounts", trend.stream().map(d -> d.get("amount")).collect(Collectors.toList()));

        if (!trend.isEmpty()) {
            trendData.put("maxIncomeDate", trend.stream().max(Comparator.comparing(d -> (BigDecimal) d.get("amount"))).get().get("date"));
            trendData.put("minIncomeDate", trend.stream().min(Comparator.comparing(d -> (BigDecimal) d.get("amount"))).get().get("date"));
            trendData.put("avgDailyIncome", new BigDecimal(trend.size()).compareTo(BigDecimal.ZERO) > 0 ?
                    trend.stream().map(d -> (BigDecimal) d.get("amount")).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(new BigDecimal(trend.size()), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        }

        return trendData;
    }

    /**
     * 构建支出类别排名
     */
    private List<Map<String, Object>> buildCategoryRanking(Integer userId, List<Transactions> transactionsList) {
        List<Categories> categoriesList = categoriesService.list(Wrappers.<Categories>lambdaQuery()
                .eq(Categories::getUserId, userId));

        Map<Integer, Categories> categoriesMap = categoriesList.stream()
                .collect(Collectors.toMap(Categories::getId, c -> c));

        List<Map<String, Object>> ranking = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transactions::getCategoryId,
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Categories cat = categoriesMap.get(entry.getKey());
                    item.put("categoryId", entry.getKey());
                    item.put("categoryName", cat != null ? cat.getName() : "其他");
                    item.put("icon", cat != null ? cat.getIcon() : null);
                    item.put("totalAmount", entry.getValue());
                    return item;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")))
                .collect(Collectors.toList());

        BigDecimal totalExpense = ranking.stream()
                .map(r -> (BigDecimal) r.get("totalAmount"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ranking.forEach(item -> {
            BigDecimal amount = (BigDecimal) item.get("totalAmount");
            if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                item.put("percentage", amount.divide(totalExpense, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
            } else {
                item.put("percentage", BigDecimal.ZERO);
            }
        });

        return ranking;
    }

    /**
     * 查询财务月报/年报
     * <p>
     * reportType: "month" - 月报，"year" - 年报
     * basicInfo: 基础信息
     * incomeExpenseAnalysis: 收支分析
     * categoryStatistics: 分类统计
     * accountStatistics: 账户统计
     * trendAnalysis: 趋势分析
     * financialHealth: 财务健康指标
     *
     * @param userId 用户 ID
     * @param year   年份
     * @param month  月份（可选，为空则查询年报）
     * @return 响应结果
     */
    @RequestMapping("/queryReportByDate")
    public R queryReportByDate(Integer userId, Integer year, Integer month) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        String reportType;

        if (month != null && month > 0 && month <= 12) {
            reportType = "month";
            startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
            endTime = startTime.plusMonths(1).minusNanos(1);
        } else {
            reportType = "year";
            startTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            endTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        }

        List<Transactions> transactionsList = transactionsService.list(Wrappers.<Transactions>lambdaQuery()
                .eq(Transactions::getUserId, userId)
                .ge(Transactions::getTransactionTime, startTime)
                .le(Transactions::getTransactionTime, endTime)
                .orderByAsc(Transactions::getTransactionTime));

        Map<String, Object> result = new HashMap<>();
        result.put("reportType", reportType);
        result.put("year", year);
        result.put("month", month);
        result.put("basicInfo", buildBasicInfo(transactionsList, reportType));
        result.put("incomeExpenseAnalysis", buildIncomeExpenseAnalysis(transactionsList));
        result.put("categoryStatistics", buildCategoryStatistics(userId, transactionsList));
        result.put("accountStatistics", buildAccountStatistics(userId, transactionsList));
        result.put("trendAnalysis", buildTrendAnalysis(transactionsList, reportType));
        result.put("financialHealth", buildFinancialHealth(transactionsList));

        return R.ok(result);
    }

    /**
     * 构建基础信息
     */
    private Map<String, Object> buildBasicInfo(List<Transactions> transactionsList, String reportType) {
        Map<String, Object> basicInfo = new HashMap<>();

        BigDecimal totalIncome = transactionsList.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        basicInfo.put("totalIncome", totalIncome);
        basicInfo.put("totalExpense", totalExpense);
        basicInfo.put("netIncome", totalIncome.subtract(totalExpense));
        basicInfo.put("totalTransactions", transactionsList.size());
        basicInfo.put("incomeCount", transactionsList.stream().filter(t -> "income".equals(t.getTransactionType())).count());
        basicInfo.put("expenseCount", transactionsList.stream().filter(t -> "expense".equals(t.getTransactionType())).count());
        basicInfo.put("reportPeriod", reportType.equals("month") ? "月度" : "年度");

        return basicInfo;
    }

    /**
     * 构建收支分析
     */
    private Map<String, Object> buildIncomeExpenseAnalysis(List<Transactions> transactionsList) {
        Map<String, Object> analysis = new HashMap<>();

        BigDecimal totalIncome = transactionsList.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        analysis.put("totalIncome", totalIncome);
        analysis.put("totalExpense", totalExpense);
        analysis.put("netIncome", totalIncome.subtract(totalExpense));
        analysis.put("profitMargin", totalIncome.compareTo(BigDecimal.ZERO) > 0 ?
                totalIncome.subtract(totalExpense).divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

        List<Map<String, Object>> monthlyData = transactionsList.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate().getMonthValue(),
                        TreeMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", entry.getKey());

                    List<Transactions> monthTransactions = entry.getValue();
                    BigDecimal monthIncome = monthTransactions.stream()
                            .filter(t -> "income".equals(t.getTransactionType()))
                            .map(Transactions::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal monthExpense = monthTransactions.stream()
                            .filter(t -> "expense".equals(t.getTransactionType()))
                            .map(Transactions::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    monthData.put("income", monthIncome);
                    monthData.put("expense", monthExpense);
                    monthData.put("netIncome", monthIncome.subtract(monthExpense));

                    return monthData;
                })
                .collect(Collectors.toList());

        analysis.put("monthlyBreakdown", monthlyData);

        return analysis;
    }

    /**
     * 构建分类统计
     */
    private Map<String, Object> buildCategoryStatistics(Integer userId, List<Transactions> transactionsList) {
        List<Categories> categoriesList = categoriesService.list(Wrappers.<Categories>lambdaQuery()
                .eq(Categories::getUserId, userId));

        Map<Integer, Categories> categoriesMap = categoriesList.stream()
                .collect(Collectors.toMap(Categories::getId, c -> c));

        Map<String, Object> categoryStats = new HashMap<>();

        List<Map<String, Object>> expenseCategories = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transactions::getCategoryId,
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Categories cat = categoriesMap.get(entry.getKey());
                    item.put("categoryId", entry.getKey());
                    item.put("categoryName", cat != null ? cat.getName() : "其他");
                    item.put("icon", cat != null ? cat.getIcon() : null);
                    item.put("amount", entry.getValue());
                    return item;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")))
                .collect(Collectors.toList());

        List<Map<String, Object>> incomeCategories = transactionsList.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transactions::getCategoryId,
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Categories cat = categoriesMap.get(entry.getKey());
                    item.put("categoryId", entry.getKey());
                    item.put("categoryName", cat != null ? cat.getName() : "其他");
                    item.put("icon", cat != null ? cat.getIcon() : null);
                    item.put("amount", entry.getValue());
                    return item;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")))
                .collect(Collectors.toList());

        categoryStats.put("expenseCategories", expenseCategories);
        categoryStats.put("incomeCategories", incomeCategories);

        return categoryStats;
    }

    /**
     * 构建账户统计
     */
    private Map<String, Object> buildAccountStatistics(Integer userId, List<Transactions> transactionsList) {
        List<Accounts> accountsList = accountsService.list(Wrappers.<Accounts>lambdaQuery()
                .eq(Accounts::getUserId, userId));

        Map<Integer, Accounts> accountsMap = accountsList.stream()
                .collect(Collectors.toMap(Accounts::getId, a -> a));

        List<Map<String, Object>> accountStats = transactionsList.stream()
                .collect(Collectors.groupingBy(
                        Transactions::getAccountId,
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Accounts account = accountsMap.get(entry.getKey());
                    item.put("accountId", entry.getKey());
                    item.put("accountName", account != null ? account.getAccountName() : "未知账户");
                    item.put("accountType", account != null ? account.getAccountType() : null);
                    item.put("totalAmount", entry.getValue());
                    return item;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("accountBreakdown", accountStats);
        result.put("totalAccounts", accountStats.size());

        return result;
    }

    /**
     * 构建趋势分析
     */
    private Map<String, Object> buildTrendAnalysis(List<Transactions> transactionsList, String reportType) {
        Map<String, Object> trendData = new HashMap<>();

        Map<Integer, List<Transactions>> periodTransactions;

        if ("month".equals(reportType)) {
            periodTransactions = transactionsList.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getTransactionTime().getDayOfMonth(),
                            TreeMap::new,
                            Collectors.toList()
                    ));

            List<Map<String, Object>> dailyTrend = periodTransactions.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> dayData = new HashMap<>();
                        dayData.put("day", entry.getKey());

                        List<Transactions> dayTrans = entry.getValue();
                        BigDecimal income = dayTrans.stream()
                                .filter(t -> "income".equals(t.getTransactionType()))
                                .map(Transactions::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal expense = dayTrans.stream()
                                .filter(t -> "expense".equals(t.getTransactionType()))
                                .map(Transactions::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        dayData.put("income", income);
                        dayData.put("expense", expense);
                        dayData.put("netIncome", income.subtract(expense));

                        return dayData;
                    })
                    .collect(Collectors.toList());

            trendData.put("dailyTrend", dailyTrend);

        } else {
            Map<Integer, List<Transactions>> monthlyTransactions = transactionsList.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getTransactionTime().getMonthValue(),
                            TreeMap::new,
                            Collectors.toList()
                    ));

            List<Map<String, Object>> monthlyTrend = monthlyTransactions.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> monthData = new HashMap<>();
                        monthData.put("month", entry.getKey());

                        List<Transactions> monthTrans = entry.getValue();
                        BigDecimal income = monthTrans.stream()
                                .filter(t -> "income".equals(t.getTransactionType()))
                                .map(Transactions::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal expense = monthTrans.stream()
                                .filter(t -> "expense".equals(t.getTransactionType()))
                                .map(Transactions::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        monthData.put("income", income);
                        monthData.put("expense", expense);
                        monthData.put("netIncome", income.subtract(expense));

                        return monthData;
                    })
                    .collect(Collectors.toList());

            trendData.put("monthlyTrend", monthlyTrend);
        }

        return trendData;
    }

    /**
     * 构建财务健康指标
     */
    private Map<String, Object> buildFinancialHealth(List<Transactions> transactionsList) {
        Map<String, Object> health = new HashMap<>();

        BigDecimal totalIncome = transactionsList.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netIncome = totalIncome.subtract(totalExpense);

        health.put("savingsRate", totalIncome.compareTo(BigDecimal.ZERO) > 0 ?
                netIncome.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO);

        health.put("expenseRatio", totalIncome.compareTo(BigDecimal.ZERO) > 0 ?
                totalExpense.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

        long expenseDays = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(t -> t.getTransactionTime().toLocalDate())
                .distinct()
                .count();

        health.put("activeExpenseDays", expenseDays);

        BigDecimal avgDailyExpense = expenseDays > 0 ?
                totalExpense.divide(new BigDecimal(expenseDays), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        health.put("avgDailyExpense", avgDailyExpense);

        List<BigDecimal> expenseAmounts = transactionsList.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .sorted()
                .collect(Collectors.toList());

        if (!expenseAmounts.isEmpty()) {
            int size = expenseAmounts.size();
            if (size % 2 == 0) {
                health.put("medianExpense", expenseAmounts.get(size / 2 - 1).add(expenseAmounts.get(size / 2)).divide(new BigDecimal("2"), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                health.put("medianExpense", expenseAmounts.get(size / 2));
            }
        } else {
            health.put("medianExpense", BigDecimal.ZERO);
        }

        String healthStatus = "良好";
        if (health.get("expenseRatio") instanceof BigDecimal) {
            BigDecimal expenseRatio = (BigDecimal) health.get("expenseRatio");
            if (expenseRatio.compareTo(new BigDecimal("0.8")) > 0) {
                healthStatus = "需改善";
            } else if (expenseRatio.compareTo(new BigDecimal("0.5")) < 0) {
                healthStatus = "优秀";
            }
        }
        health.put("healthStatus", healthStatus);

        return health;
    }

    /**
     * 现金流预测 - 基于历史数据和待支付账单，预测未来 30 天的可用现金余额
     * <p>
     * currentCash: 当前可用现金
     * forecastData: 每日预测数据
     * riskWarning: 风险预警信息
     * cashFlowMetrics: 现金流指标
     *
     * 1. 当前可用现金 (currentCash)
     * 统计所有正常状态账户的实时余额总和
     * 2. 每日预测数据 (dailyForecast)
     * 预测未来 30 天每天的详细数据：
     * date: 预测日期
     * dayIndex: 第几天（1-30）
     * expectedIncome: 预期收入（基于历史同期平均）
     * expectedExpense: 预期支出（基于历史同期平均）
     * budgetExpense: 当日到期的预算
     * goalSavings: 当日到期的存钱计划
     * netFlow: 净现金流（流入 - 流出）
     * predictedBalance: 预测余额（累计滚动计算）
     * isRisk: 是否出现风险（余额<0）
     * 3. 风险预警 (riskWarning)
     * 提供多级风险预警：
     * 高风险 (critical): 现金为负
     * 高风险 (high): 现金流紧张，可维持时间<3 个月
     * 中风险 (medium):
     * 预算占比过高（>80%）
     * 存钱计划压力过大（>30%）
     * 每个预警包含：
     * 风险等级
     * 风险类型
     * 预警信息
     * 相关数据指标
     * 4. 现金流指标 (cashFlowMetrics)
     * totalIncome: 历史总收入
     * totalExpense: 历史总支出
     * netCashFlow: 净现金流
     * cashFlowMargin: 现金流利润率
     * positiveCashFlowDays: 正现金流天数
     * volatility: 现金流波动率（标准差）
     * stabilityLevel: 稳定性评估（稳定/波动较大）
     *
     * @param userId 用户 ID
     * @return 响应结果
     */
    @RequestMapping("/queryCashFlowForecast")
    public R queryCashFlowForecast(Integer userId) {
        List<Accounts> accountsList = accountsService.list(Wrappers.<Accounts>lambdaQuery()
                .eq(Accounts::getUserId, userId)
                .eq(Accounts::getStatus, 1));

        BigDecimal currentCash = accountsList.stream()
                .map(Accounts::getBalance)
                .filter(balance -> balance != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime forecastEndDate = now.plusDays(30);

        List<Transactions> historicalTransactions = transactionsService.list(Wrappers.<Transactions>lambdaQuery()
                .eq(Transactions::getUserId, userId)
                .le(Transactions::getTransactionTime, now)
                .ge(Transactions::getTransactionTime, now.minusMonths(3))
                .orderByDesc(Transactions::getTransactionTime));

        List<Budgets> budgetsList = budgetsService.list(Wrappers.<Budgets>lambdaQuery()
                .eq(Budgets::getUserId, userId));

        List<SavingGoals> savingGoalsList = savingGoalsService.list(Wrappers.<SavingGoals>lambdaQuery()
                .eq(SavingGoals::getUserId, userId)
                .in(SavingGoals::getStatus, "active", "processing"));

        Map<String, Object> result = new HashMap<>();
        result.put("currentCash", currentCash);
        result.put("forecastPeriod", "未来 30 天");
        result.put("forecastStartDate", now.toLocalDate().toString());
        result.put("forecastEndDate", forecastEndDate.toLocalDate().toString());
        result.put("dailyForecast", buildDailyForecast(userId, currentCash, now, forecastEndDate, historicalTransactions, budgetsList, savingGoalsList));
        result.put("riskWarning", buildRiskWarning(currentCash, historicalTransactions, budgetsList, savingGoalsList));
        result.put("cashFlowMetrics", buildCashFlowMetrics(historicalTransactions));

        return R.ok(result);
    }

    /**
     * 构建每日现金流预测
     */
    private List<Map<String, Object>> buildDailyForecast(Integer userId, BigDecimal currentCash,
                                                         LocalDateTime startDate, LocalDateTime endDate,
                                                         List<Transactions> historicalTransactions,
                                                         List<Budgets> budgetsList,
                                                         List<SavingGoals> savingGoalsList) {
        List<Map<String, Object>> dailyForecast = new ArrayList<>();

        Map<Integer, BigDecimal> historicalAvgIncomeByDay = calculateHistoricalAverageByDayOfMonth(historicalTransactions, "income");
        Map<Integer, BigDecimal> historicalAvgExpenseByDay = calculateHistoricalAverageByDayOfMonth(historicalTransactions, "expense");

        BigDecimal runningBalance = currentCash;

        for (int day = 1; day <= 30; day++) {
            LocalDateTime forecastDate = startDate.plusDays(day);
            int dayOfMonth = forecastDate.getDayOfMonth();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", forecastDate.toLocalDate().toString());
            dayData.put("dayIndex", day);

            BigDecimal expectedIncome = historicalAvgIncomeByDay.getOrDefault(dayOfMonth, BigDecimal.ZERO);
            BigDecimal expectedExpense = historicalAvgExpenseByDay.getOrDefault(dayOfMonth, BigDecimal.ZERO);

            List<Budgets> dueBudgets = budgetsList.stream()
                    .filter(b -> isBudgetDueInDay(b, forecastDate.toLocalDate()))
                    .collect(Collectors.toList());
            BigDecimal budgetExpense = dueBudgets.stream()
                    .map(Budgets::getAmountLimit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<SavingGoals> dueGoals = savingGoalsList.stream()
                    .filter(g -> isSavingGoalDueInDay(g, forecastDate.toLocalDate()))
                    .collect(Collectors.toList());
            BigDecimal goalSavings = dueGoals.stream()
                    .map(SavingGoals::getMonthlySuggestion)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalInflow = expectedIncome;
            BigDecimal totalOutflow = expectedExpense.add(budgetExpense).add(goalSavings);

            BigDecimal netFlow = totalInflow.subtract(totalOutflow);
            runningBalance = runningBalance.add(netFlow);

            dayData.put("expectedIncome", expectedIncome);
            dayData.put("expectedExpense", expectedExpense);
            dayData.put("budgetExpense", budgetExpense);
            dayData.put("goalSavings", goalSavings);
            dayData.put("netFlow", netFlow);
            dayData.put("predictedBalance", runningBalance);
            dayData.put("isRisk", runningBalance.compareTo(BigDecimal.ZERO) < 0);

            dailyForecast.add(dayData);
        }

        return dailyForecast;
    }

    /**
     * 计算历史每日平均收支
     */
    private Map<Integer, BigDecimal> calculateHistoricalAverageByDayOfMonth(List<Transactions> transactions, String type) {
        Map<Integer, List<BigDecimal>> amountByDay = transactions.stream()
                .filter(t -> type.equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().getDayOfMonth(),
                        Collectors.mapping(Transactions::getAmount, Collectors.toList())
                ));

        return amountByDay.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(new BigDecimal(entry.getValue().size()), 2, BigDecimal.ROUND_HALF_UP)
                ));
    }

    /**
     * 判断预算是否在指定日期到期
     */
    private boolean isBudgetDueInDay(Budgets budget, LocalDate date) {
        if (budget.getPeriod() == null) {
            return false;
        }
        try {
            LocalDate budgetDate = LocalDate.parse(budget.getPeriod(), DateTimeFormatter.ofPattern("yyyy-MM"));
            return budgetDate.getMonthValue() == date.getMonthValue() &&
                    budgetDate.getYear() == date.getYear();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断存钱计划是否在指定日期到期
     */
    private boolean isSavingGoalDueInDay(SavingGoals goal, LocalDate date) {
        if (goal.getEndDate() == null) {
            return false;
        }
        return goal.getEndDate().equals(date);
    }

    /**
     * 构建风险预警信息
     */
    private List<Map<String, Object>> buildRiskWarning(BigDecimal currentCash,
                                                       List<Transactions> historicalTransactions,
                                                       List<Budgets> budgetsList,
                                                       List<SavingGoals> savingGoalsList) {
        List<Map<String, Object>> warnings = new ArrayList<>();

        BigDecimal avgMonthlyExpense = calculateAverageMonthlyExpense(historicalTransactions);
        BigDecimal runwayMonths = currentCash.compareTo(avgMonthlyExpense) > 0 ?
                currentCash.divide(avgMonthlyExpense, 1, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        if (runwayMonths.compareTo(new BigDecimal("3")) < 0) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("level", "high");
            warning.put("type", "low_runway");
            warning.put("message", "现金流紧张，当前资金仅能维持约" + runwayMonths + "个月");
            warning.put("currentCash", currentCash);
            warning.put("avgMonthlyExpense", avgMonthlyExpense);
            warning.put("runwayMonths", runwayMonths);
            warnings.add(warning);
        }

        BigDecimal totalBudget = budgetsList.stream()
                .map(Budgets::getAmountLimit)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBudget.compareTo(currentCash.multiply(new BigDecimal("0.8"))) > 0) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("level", "medium");
            warning.put("type", "high_budget_ratio");
            warning.put("message", "预算占比较高，可能存在超支风险");
            warning.put("totalBudget", totalBudget);
            warning.put("budgetRatio", totalBudget.divide(currentCash, 4, BigDecimal.ROUND_HALF_UP));
            warnings.add(warning);
        }

        BigDecimal totalGoalSavings = savingGoalsList.stream()
                .map(SavingGoals::getMonthlySuggestion)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalGoalSavings.compareTo(currentCash.multiply(new BigDecimal("0.3"))) > 0) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("level", "medium");
            warning.put("type", "high_savings_pressure");
            warning.put("message", "存钱计划压力较大，建议调整目标");
            warning.put("totalGoalSavings", totalGoalSavings);
            warning.put("savingsRatio", totalGoalSavings.divide(currentCash, 4, BigDecimal.ROUND_HALF_UP));
            warnings.add(warning);
        }

        if (currentCash.compareTo(BigDecimal.ZERO) < 0) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("level", "critical");
            warning.put("type", "negative_cash");
            warning.put("message", "当前可用现金为负，存在严重周转风险");
            warning.put("currentCash", currentCash);
            warnings.add(warning);
        }

        return warnings;
    }

    /**
     * 计算月均支出
     */
    private BigDecimal calculateAverageMonthlyExpense(List<Transactions> transactions) {
        Map<String, BigDecimal> expenseByMonth = transactions.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        if (expenseByMonth.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalExpense = expenseByMonth.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalExpense.divide(new BigDecimal(expenseByMonth.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 构建现金流指标
     */
    private Map<String, Object> buildCashFlowMetrics(List<Transactions> historicalTransactions) {
        Map<String, Object> metrics = new HashMap<>();

        BigDecimal totalIncome = historicalTransactions.stream()
                .filter(t -> "income".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = historicalTransactions.stream()
                .filter(t -> "expense".equals(t.getTransactionType()))
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netCashFlow = totalIncome.subtract(totalExpense);

        metrics.put("totalIncome", totalIncome);
        metrics.put("totalExpense", totalExpense);
        metrics.put("netCashFlow", netCashFlow);
        metrics.put("cashFlowMargin", totalIncome.compareTo(BigDecimal.ZERO) > 0 ?
                netCashFlow.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

        long positiveCashFlowDays = historicalTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate(),
                        Collectors.mapping(Transactions::getAmount, Collectors.toList())
                ))
                .entrySet().stream()
                .filter(entry -> {
                    BigDecimal dayIncome = entry.getValue().stream()
                            .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal dayExpense = entry.getValue().stream()
                            .filter(amount -> amount.compareTo(BigDecimal.ZERO) < 0)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return dayIncome.compareTo(dayExpense.abs()) > 0;
                })
                .count();

        metrics.put("positiveCashFlowDays", positiveCashFlowDays);

        BigDecimal volatility = calculateCashFlowVolatility(historicalTransactions);
        metrics.put("volatility", volatility);
        metrics.put("stabilityLevel", volatility.compareTo(new BigDecimal("0.3")) < 0 ? "稳定" : "波动较大");

        return metrics;
    }

    /**
     * 计算现金流波动率
     */
    private BigDecimal calculateCashFlowVolatility(List<Transactions> transactions) {
        Map<LocalDate, BigDecimal> dailyNetFlow = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionTime().toLocalDate(),
                        Collectors.mapping(Transactions::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        if (dailyNetFlow.size() < 2) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> netFlows = new ArrayList<>(dailyNetFlow.values());
        BigDecimal avg = netFlows.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(netFlows.size()), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal variance = netFlows.stream()
                .map(flow -> flow.subtract(avg).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(netFlows.size()), 6, BigDecimal.ROUND_HALF_UP);

        return sqrt(variance);
    }

    /**
     * 计算 BigDecimal 的平方根（使用牛顿迭代法）
     */
    private BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal x0 = BigDecimal.ZERO;
        BigDecimal x1 = value;

        for (int i = 0; i < 100; i++) {
            if (x1.equals(x0)) {
                break;
            }
            x0 = x1;
            x1 = value.divide(x0, MathContext.DECIMAL64).add(x0).divide(new BigDecimal("2"), MathContext.DECIMAL64);
        }

        return x1.setScale(6, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 模拟长期投资 - 计算不同收益率下的增长曲线
     * <p>
     * 支持定投和一次性投资两种模式
     * 提供多种收益率场景对比
     *
     * 3% - 保守型（国债、货币基金）
     * 6% - 稳健型（债券基金）
     * 8% - 平衡型（混合基金）
     * 10% - 成长型（指数基金，如纳斯达克 100）
     * 15% - 进取型（股票、科技基金）
     * 
     * @param userId              用户 ID
     * @param initialAmount       初始投资金额
     * @param monthlyContribution 每月定投金额（可选，0 表示一次性投资）
     * @param years               投资年限
     * @param rateScenarios       收益率场景列表（年化，如 8 表示 8%）
     * @return 响应结果
     */
    @RequestMapping("/querySimulatedInvestment")
    public R querySimulatedInvestment(Integer userId,
                                      BigDecimal initialAmount,
                                      BigDecimal monthlyContribution,
                                      Integer years,
                                      List<Integer> rateScenarios) {

        if (initialAmount == null || initialAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("初始投资金额必须大于 0");
        }

        if (years == null || years <= 0) {
            return R.error("投资年限必须大于 0");
        }

        if (rateScenarios == null || rateScenarios.isEmpty()) {
            rateScenarios = new ArrayList<>();
            rateScenarios.add(3);   // 保守型（国债/货币基金）
            rateScenarios.add(6);   // 稳健型（债券基金）
            rateScenarios.add(8);   // 平衡型（混合基金）
            rateScenarios.add(10);  // 成长型（指数基金）
            rateScenarios.add(15);  // 进取型（股票/科技基金）
        }

        boolean isRegularInvestment = monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0;

        Map<String, Object> result = new HashMap<>();
        result.put("investmentType", isRegularInvestment ? "定投" : "一次性投资");
        result.put("initialAmount", initialAmount);
        result.put("monthlyContribution", isRegularInvestment ? monthlyContribution : null);
        result.put("investmentYears", years);
        result.put("scenarios", buildInvestmentScenarios(initialAmount, monthlyContribution, years, rateScenarios));
        result.put("detailedCurves", buildDetailedGrowthCurves(initialAmount, monthlyContribution, years, rateScenarios));
        result.put("comparisonMetrics", buildComparisonMetrics(initialAmount, monthlyContribution, years, rateScenarios));

        return R.ok(result);
    }

    /**
     * 构建各收益率场景的投资结果
     */
    private List<Map<String, Object>> buildInvestmentScenarios(BigDecimal initialAmount,
                                                               BigDecimal monthlyContribution,
                                                               Integer years,
                                                               List<Integer> rateScenarios) {
        List<Map<String, Object>> scenarios = new ArrayList<>();

        for (Integer annualRate : rateScenarios) {
            Map<String, Object> scenario = new HashMap<>();
            BigDecimal monthlyRate = BigDecimal.valueOf(annualRate).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP)
                    .divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP);

            int totalMonths = years * 12;

            BigDecimal finalAmount;
            BigDecimal totalPrincipal;

            if (monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0) {
                finalAmount = calculateFutureValueWithRegularContribution(initialAmount, monthlyContribution, monthlyRate, totalMonths);
                totalPrincipal = initialAmount.add(monthlyContribution.multiply(new BigDecimal(totalMonths)));
            } else {
                finalAmount = calculateFutureValueLumpSum(initialAmount, monthlyRate, totalMonths);
                totalPrincipal = initialAmount;
            }

            BigDecimal totalReturn = finalAmount.subtract(totalPrincipal);
            BigDecimal returnRate = totalPrincipal.compareTo(BigDecimal.ZERO) > 0 ?
                    totalReturn.divide(totalPrincipal, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO;

            scenario.put("annualRate", annualRate);
            scenario.put("finalAmount", finalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            scenario.put("totalPrincipal", totalPrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
            scenario.put("totalReturn", totalReturn.setScale(2, BigDecimal.ROUND_HALF_UP));
            scenario.put("returnRate", returnRate.setScale(2, BigDecimal.ROUND_HALF_UP));
            scenario.put("investmentType", monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0 ? "定投" : "一次性");

            scenarios.add(scenario);
        }

        return scenarios;
    }

    /**
     * 构建详细的月度增长曲线
     */
    private List<Map<String, Object>> buildDetailedGrowthCurves(BigDecimal initialAmount,
                                                                BigDecimal monthlyContribution,
                                                                Integer years,
                                                                List<Integer> rateScenarios) {
        List<Map<String, Object>> curves = new ArrayList<>();
        int totalMonths = years * 12;

        for (int month = 0; month <= totalMonths; month++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("year", month / 12 + "." + (month % 12));

            Map<String, BigDecimal> valuesByRate = new HashMap<>();

            for (Integer annualRate : rateScenarios) {
                BigDecimal monthlyRate = BigDecimal.valueOf(annualRate).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP)
                        .divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP);

                BigDecimal value;
                if (monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0) {
                    value = calculateFutureValueWithRegularContribution(initialAmount, monthlyContribution, monthlyRate, month);
                } else {
                    value = calculateFutureValueLumpSum(initialAmount, monthlyRate, month);
                }

                valuesByRate.put(annualRate.toString(), value.setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            monthData.put("values", valuesByRate);
            curves.add(monthData);
        }

        return curves;
    }

    /**
     * 构建对比指标
     */
    private Map<String, Object> buildComparisonMetrics(BigDecimal initialAmount,
                                                       BigDecimal monthlyContribution,
                                                       Integer years,
                                                       List<Integer> rateScenarios) {
        Map<String, Object> metrics = new HashMap<>();

        BigDecimal totalPrincipal = monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0 ?
                initialAmount.add(monthlyContribution.multiply(new BigDecimal(years * 12))) : initialAmount;

        List<Map<String, Object>> rateComparison = new ArrayList<>();
        for (Integer annualRate : rateScenarios) {
            Map<String, Object> comparison = new HashMap<>();
            BigDecimal monthlyRate = BigDecimal.valueOf(annualRate).divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP)
                    .divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP);

            BigDecimal finalAmount;
            if (monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0) {
                finalAmount = calculateFutureValueWithRegularContribution(initialAmount, monthlyContribution, monthlyRate, years * 12);
            } else {
                finalAmount = calculateFutureValueLumpSum(initialAmount, monthlyRate, years * 12);
            }

            BigDecimal totalReturn = finalAmount.subtract(totalPrincipal);
            BigDecimal avgAnnualReturn = totalReturn.divide(new BigDecimal(years), 2, BigDecimal.ROUND_HALF_UP);

            comparison.put("rate", annualRate);
            comparison.put("finalAmount", finalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            comparison.put("totalReturn", totalReturn.setScale(2, BigDecimal.ROUND_HALF_UP));
            comparison.put("avgAnnualReturn", avgAnnualReturn.setScale(2, BigDecimal.ROUND_HALF_UP));
            comparison.put("multiple", finalAmount.divide(totalPrincipal, 2, BigDecimal.ROUND_HALF_UP));

            rateComparison.add(comparison);
        }

        metrics.put("totalPrincipal", totalPrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
        metrics.put("rateComparison", rateComparison);

        if (monthlyContribution != null && monthlyContribution.compareTo(BigDecimal.ZERO) > 0) {
            metrics.put("totalInvestedMonths", years * 12);
            metrics.put("monthlyContribution", monthlyContribution);
        }

        return metrics;
    }

    /**
     * 计算一次性投资的终值（复利公式）
     * FV = PV × (1 + r)^n
     */
    private BigDecimal calculateFutureValueLumpSum(BigDecimal presentValue, BigDecimal monthlyRate, int months) {
        if (months == 0) {
            return presentValue;
        }

        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyRate).pow(months);
        return presentValue.multiply(growthFactor);
    }

    /**
     * 计算定期定额投资的终值（年金终值公式）
     * FV = PV × (1 + r)^n + PMT × [((1 + r)^n - 1) / r]
     */
    private BigDecimal calculateFutureValueWithRegularContribution(BigDecimal presentValue,
                                                                   BigDecimal monthlyPayment,
                                                                   BigDecimal monthlyRate,
                                                                   int months) {
        if (months == 0) {
            return presentValue;
        }

        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyRate).pow(months);

        BigDecimal lumpSumFV = presentValue.multiply(growthFactor);

        BigDecimal annuityFV;
        if (monthlyRate.compareTo(BigDecimal.ZERO) > 0) {
            annuityFV = monthlyPayment.multiply(growthFactor.subtract(BigDecimal.ONE))
                    .divide(monthlyRate, MathContext.DECIMAL64);
        } else {
            annuityFV = monthlyPayment.multiply(new BigDecimal(months));
        }

        return lumpSumFV.add(annuityFV);
    }

}
