package com.platon.browser.dao.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NodeHistoryDeleAnnualizedRateExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public NodeHistoryDeleAnnualizedRateExample() {
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

        public Criteria andDeleAnnualizedRateMaxIsNull() {
            addCriterion("dele_annualized_rate_max is null");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxIsNotNull() {
            addCriterion("dele_annualized_rate_max is not null");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_max =", value, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxNotEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_max <>", value, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxGreaterThan(BigDecimal value) {
            addCriterion("dele_annualized_rate_max >", value, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_max >=", value, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxLessThan(BigDecimal value) {
            addCriterion("dele_annualized_rate_max <", value, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxLessThanOrEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_max <=", value, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxIn(List<BigDecimal> values) {
            addCriterion("dele_annualized_rate_max in", values, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxNotIn(List<BigDecimal> values) {
            addCriterion("dele_annualized_rate_max not in", values, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("dele_annualized_rate_max between", value1, value2, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMaxNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("dele_annualized_rate_max not between", value1, value2, "deleAnnualizedRateMax");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinIsNull() {
            addCriterion("dele_annualized_rate_min is null");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinIsNotNull() {
            addCriterion("dele_annualized_rate_min is not null");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_min =", value, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinNotEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_min <>", value, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinGreaterThan(BigDecimal value) {
            addCriterion("dele_annualized_rate_min >", value, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_min >=", value, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinLessThan(BigDecimal value) {
            addCriterion("dele_annualized_rate_min <", value, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinLessThanOrEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_min <=", value, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinIn(List<BigDecimal> values) {
            addCriterion("dele_annualized_rate_min in", values, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinNotIn(List<BigDecimal> values) {
            addCriterion("dele_annualized_rate_min not in", values, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("dele_annualized_rate_min between", value1, value2, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateMinNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("dele_annualized_rate_min not between", value1, value2, "deleAnnualizedRateMin");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgIsNull() {
            addCriterion("dele_annualized_rate_avg is null");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgIsNotNull() {
            addCriterion("dele_annualized_rate_avg is not null");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_avg =", value, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgNotEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_avg <>", value, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgGreaterThan(BigDecimal value) {
            addCriterion("dele_annualized_rate_avg >", value, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_avg >=", value, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgLessThan(BigDecimal value) {
            addCriterion("dele_annualized_rate_avg <", value, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgLessThanOrEqualTo(BigDecimal value) {
            addCriterion("dele_annualized_rate_avg <=", value, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgIn(List<BigDecimal> values) {
            addCriterion("dele_annualized_rate_avg in", values, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgNotIn(List<BigDecimal> values) {
            addCriterion("dele_annualized_rate_avg not in", values, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("dele_annualized_rate_avg between", value1, value2, "deleAnnualizedRateAvg");
            return (Criteria) this;
        }

        public Criteria andDeleAnnualizedRateAvgNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("dele_annualized_rate_avg not between", value1, value2, "deleAnnualizedRateAvg");
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