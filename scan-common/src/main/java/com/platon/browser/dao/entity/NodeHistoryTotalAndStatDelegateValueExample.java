package com.platon.browser.dao.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NodeHistoryTotalAndStatDelegateValueExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public NodeHistoryTotalAndStatDelegateValueExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andNodeIdIsNull() {
            addCriterion("node_id is null");
            return (Criteria) this;
        }

        public Criteria andNodeIdIsNotNull() {
            addCriterion("node_id is not null");
            return (Criteria) this;
        }

        public Criteria andNodeIdEqualTo(String value) {
            addCriterion("node_id =", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotEqualTo(String value) {
            addCriterion("node_id <>", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdGreaterThan(String value) {
            addCriterion("node_id >", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdGreaterThanOrEqualTo(String value) {
            addCriterion("node_id >=", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdLessThan(String value) {
            addCriterion("node_id <", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdLessThanOrEqualTo(String value) {
            addCriterion("node_id <=", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdLike(String value) {
            addCriterion("node_id like", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotLike(String value) {
            addCriterion("node_id not like", value, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdIn(List<String> values) {
            addCriterion("node_id in", values, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotIn(List<String> values) {
            addCriterion("node_id not in", values, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdBetween(String value1, String value2) {
            addCriterion("node_id between", value1, value2, "nodeId");
            return (Criteria) this;
        }

        public Criteria andNodeIdNotBetween(String value1, String value2) {
            addCriterion("node_id not between", value1, value2, "nodeId");
            return (Criteria) this;
        }

        public Criteria andDateIsNull() {
            addCriterion("`date` is null");
            return (Criteria) this;
        }

        public Criteria andDateIsNotNull() {
            addCriterion("`date` is not null");
            return (Criteria) this;
        }

        public Criteria andDateEqualTo(Date value) {
            addCriterion("`date` =", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotEqualTo(Date value) {
            addCriterion("`date` <>", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateGreaterThan(Date value) {
            addCriterion("`date` >", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateGreaterThanOrEqualTo(Date value) {
            addCriterion("`date` >=", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateLessThan(Date value) {
            addCriterion("`date` <", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateLessThanOrEqualTo(Date value) {
            addCriterion("`date` <=", value, "date");
            return (Criteria) this;
        }

        public Criteria andDateIn(List<Date> values) {
            addCriterion("`date` in", values, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotIn(List<Date> values) {
            addCriterion("`date` not in", values, "date");
            return (Criteria) this;
        }

        public Criteria andDateBetween(Date value1, Date value2) {
            addCriterion("`date` between", value1, value2, "date");
            return (Criteria) this;
        }

        public Criteria andDateNotBetween(Date value1, Date value2) {
            addCriterion("`date` not between", value1, value2, "date");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxIsNull() {
            addCriterion("stat_delegate_value_max is null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxIsNotNull() {
            addCriterion("stat_delegate_value_max is not null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_max =", value, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxNotEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_max <>", value, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxGreaterThan(BigDecimal value) {
            addCriterion("stat_delegate_value_max >", value, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_max >=", value, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxLessThan(BigDecimal value) {
            addCriterion("stat_delegate_value_max <", value, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_max <=", value, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_max in", values, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxNotIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_max not in", values, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_max between", value1, value2, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMaxNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_max not between", value1, value2, "statDelegateValueMax");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinIsNull() {
            addCriterion("stat_delegate_value_min is null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinIsNotNull() {
            addCriterion("stat_delegate_value_min is not null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_min =", value, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinNotEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_min <>", value, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinGreaterThan(BigDecimal value) {
            addCriterion("stat_delegate_value_min >", value, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_min >=", value, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinLessThan(BigDecimal value) {
            addCriterion("stat_delegate_value_min <", value, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_min <=", value, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_min in", values, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinNotIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_min not in", values, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_min between", value1, value2, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueMinNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_min not between", value1, value2, "statDelegateValueMin");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgIsNull() {
            addCriterion("stat_delegate_value_avg is null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgIsNotNull() {
            addCriterion("stat_delegate_value_avg is not null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_avg =", value, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgNotEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_avg <>", value, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgGreaterThan(BigDecimal value) {
            addCriterion("stat_delegate_value_avg >", value, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_avg >=", value, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgLessThan(BigDecimal value) {
            addCriterion("stat_delegate_value_avg <", value, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_avg <=", value, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_avg in", values, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgNotIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_avg not in", values, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_avg between", value1, value2, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueAvgNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_avg not between", value1, value2, "statDelegateValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalIsNull() {
            addCriterion("stat_delegate_value_total is null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalIsNotNull() {
            addCriterion("stat_delegate_value_total is not null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_total =", value, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalNotEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_total <>", value, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalGreaterThan(BigDecimal value) {
            addCriterion("stat_delegate_value_total >", value, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_total >=", value, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalLessThan(BigDecimal value) {
            addCriterion("stat_delegate_value_total <", value, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_delegate_value_total <=", value, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_total in", values, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalNotIn(List<BigDecimal> values) {
            addCriterion("stat_delegate_value_total not in", values, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_total between", value1, value2, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueTotalNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_delegate_value_total not between", value1, value2, "statDelegateValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountIsNull() {
            addCriterion("stat_delegate_value_count is null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountIsNotNull() {
            addCriterion("stat_delegate_value_count is not null");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountEqualTo(Integer value) {
            addCriterion("stat_delegate_value_count =", value, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountNotEqualTo(Integer value) {
            addCriterion("stat_delegate_value_count <>", value, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountGreaterThan(Integer value) {
            addCriterion("stat_delegate_value_count >", value, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("stat_delegate_value_count >=", value, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountLessThan(Integer value) {
            addCriterion("stat_delegate_value_count <", value, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountLessThanOrEqualTo(Integer value) {
            addCriterion("stat_delegate_value_count <=", value, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountIn(List<Integer> values) {
            addCriterion("stat_delegate_value_count in", values, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountNotIn(List<Integer> values) {
            addCriterion("stat_delegate_value_count not in", values, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountBetween(Integer value1, Integer value2) {
            addCriterion("stat_delegate_value_count between", value1, value2, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatDelegateValueCountNotBetween(Integer value1, Integer value2) {
            addCriterion("stat_delegate_value_count not between", value1, value2, "statDelegateValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxIsNull() {
            addCriterion("stat_staking_value_max is null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxIsNotNull() {
            addCriterion("stat_staking_value_max is not null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_max =", value, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxNotEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_max <>", value, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxGreaterThan(BigDecimal value) {
            addCriterion("stat_staking_value_max >", value, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_max >=", value, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxLessThan(BigDecimal value) {
            addCriterion("stat_staking_value_max <", value, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_max <=", value, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_max in", values, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxNotIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_max not in", values, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_max between", value1, value2, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMaxNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_max not between", value1, value2, "statStakingValueMax");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinIsNull() {
            addCriterion("stat_staking_value_min is null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinIsNotNull() {
            addCriterion("stat_staking_value_min is not null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_min =", value, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinNotEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_min <>", value, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinGreaterThan(BigDecimal value) {
            addCriterion("stat_staking_value_min >", value, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_min >=", value, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinLessThan(BigDecimal value) {
            addCriterion("stat_staking_value_min <", value, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_min <=", value, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_min in", values, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinNotIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_min not in", values, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_min between", value1, value2, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueMinNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_min not between", value1, value2, "statStakingValueMin");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgIsNull() {
            addCriterion("stat_staking_value_avg is null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgIsNotNull() {
            addCriterion("stat_staking_value_avg is not null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_avg =", value, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgNotEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_avg <>", value, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgGreaterThan(BigDecimal value) {
            addCriterion("stat_staking_value_avg >", value, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_avg >=", value, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgLessThan(BigDecimal value) {
            addCriterion("stat_staking_value_avg <", value, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_avg <=", value, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_avg in", values, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgNotIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_avg not in", values, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_avg between", value1, value2, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueAvgNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_avg not between", value1, value2, "statStakingValueAvg");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalIsNull() {
            addCriterion("stat_staking_value_total is null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalIsNotNull() {
            addCriterion("stat_staking_value_total is not null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_total =", value, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalNotEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_total <>", value, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalGreaterThan(BigDecimal value) {
            addCriterion("stat_staking_value_total >", value, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_total >=", value, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalLessThan(BigDecimal value) {
            addCriterion("stat_staking_value_total <", value, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalLessThanOrEqualTo(BigDecimal value) {
            addCriterion("stat_staking_value_total <=", value, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_total in", values, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalNotIn(List<BigDecimal> values) {
            addCriterion("stat_staking_value_total not in", values, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_total between", value1, value2, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueTotalNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("stat_staking_value_total not between", value1, value2, "statStakingValueTotal");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountIsNull() {
            addCriterion("stat_staking_value_count is null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountIsNotNull() {
            addCriterion("stat_staking_value_count is not null");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountEqualTo(Integer value) {
            addCriterion("stat_staking_value_count =", value, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountNotEqualTo(Integer value) {
            addCriterion("stat_staking_value_count <>", value, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountGreaterThan(Integer value) {
            addCriterion("stat_staking_value_count >", value, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("stat_staking_value_count >=", value, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountLessThan(Integer value) {
            addCriterion("stat_staking_value_count <", value, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountLessThanOrEqualTo(Integer value) {
            addCriterion("stat_staking_value_count <=", value, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountIn(List<Integer> values) {
            addCriterion("stat_staking_value_count in", values, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountNotIn(List<Integer> values) {
            addCriterion("stat_staking_value_count not in", values, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountBetween(Integer value1, Integer value2) {
            addCriterion("stat_staking_value_count between", value1, value2, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andStatStakingValueCountNotBetween(Integer value1, Integer value2) {
            addCriterion("stat_staking_value_count not between", value1, value2, "statStakingValueCount");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}