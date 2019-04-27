package org.tree.commons.support.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tree.commons.utils.MapUtils;
import org.tree.commons.utils.StringUtils;

import java.util.*;

/**
 * @author er_dong_chen
 * @date 2019/3/5
 */
public class UnionSearch {
    private Logger logger = LoggerFactory.getLogger(UnionSearch.class);

    private final String select = "SELECT %s FROM %s WHERE %s ";
    private final String selectDistinct = "SELECT DISTINCTROW %s FROM %s WHERE %s ";

    private String columns = "*";
    private Set<String> tables = new HashSet<>();
    private Criteria criteria;
    private String orderBy;
    private String groupBy;
    private String limit;
    private boolean distinct;
    private long totalCount;

    private UnionSearchMapper mapper;

    public UnionSearch(UnionSearchMapper mapper) {
        this.mapper = mapper;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public UnionSearch distinct() {
        distinct = true;
        return this;
    }

    public UnionSearch selectColumns(Args<?>... args) {
        columns = StringUtils.join(",", args);
        for (Args<?> arg : args)
            tables.add(arg.getTableName());
        return this;
    }

    public Criteria getCriteria() {
        if (criteria == null)
            criteria = new Criteria(tables);
        return criteria;
    }

    public UnionSearch orderBy(Searchable column, boolean asc) {
        orderBy = String.format("ORDER BY %s %s ", column.getName(), asc ? "ASC" : "DESC");
        return this;
    }

    public UnionSearch orderByCount(Searchable column, boolean asc) {
        orderBy = String.format("ORDER BY count(%s) %s ", column.getName(), asc ? "ASC" : "DESC");
        return this;
    }

    public UnionSearch groupBy(Searchable column) {
        groupBy = String.format("group by %s ", column.getName());
        return this;
    }

    public UnionSearch limit(int start, int length) {
        limit = String.format("LIMIT %s,%s ", start, length);
        return this;
    }

    public UnionSearch addColumnAlias(String column, String alias) {
        String definition = String.format("%s %s", column, alias);
        columns = columns.contains(column) ?
                columns.replace(column, definition) : String.format("%s,%s", columns, definition);
        return this;
    }

    public UnionSearch addColumnAlias(Searchable column, String alias) {
        return addColumnAlias(column.getName(), alias);
    }

    public final static String count(Searchable column) {
        return String.format("count(%s)", column.getName());
    }

    @Override
    public String toString() {
        StringBuilder queryString = new StringBuilder();
        queryString.append(String.format(distinct ? selectDistinct : select,
                String.join(",", columns),
                String.join(",", tables),
                criteria == null ? "1=1" : criteria.toString()));
        if (groupBy != null)
            queryString.append(groupBy);
        if (orderBy != null)
            queryString.append(orderBy);
        if (limit != null)
            queryString.append(limit);
        return queryString.toString();
    }

    private String queryTotalCount() {
        StringBuilder queryString = new StringBuilder();
        queryString.append(String.format(distinct ? selectDistinct : select,
                "count(*)",
                String.join(",", tables),
                criteria.toString()));
        return queryString.toString();
    }

    public List<Map<?, ?>> query() {
        String sql = toString();
        logger.info(String.format("连表查询：%s", sql));
        List<Map<?, ?>> map = mapper.query(sql);
        totalCount = limit == null ? map.size() : mapper.count(queryTotalCount());
        return map;
    }

    public <T> List<T> query(Class<T> dto) {
        List<Map<?, ?>> result = query();
        List<T> result2 = new ArrayList<>(result.size());
        result.forEach(map -> result2.add(MapUtils.toObject(map, dto)));
        return result2;
    }

    /****************************** 内部类：仿 Mybatis::Example *******************************/

    public static class Criteria {
        enum DEAL {
            AND, OR
        }

        private Set<String> tables;

        private StringBuilder sb = new StringBuilder();

        private int flag;

        public Criteria(Set<String> tables) {
            this.tables = tables;
        }

        public Criteria and(Searchable column, String definition) {
            sb.append(combine(DEAL.AND, false, column, definition));
            return this;
        }

        public Criteria and(Searchable column, String symbol, Searchable column2) {
            sb.append(combine(DEAL.AND, false, column, symbol, column2));
            return this;
        }

        public Criteria or(Searchable column, String definition) {
            sb.append(combine(DEAL.OR, false, column, definition));
            return this;
        }

        public Criteria or(Searchable column, String symbol, Searchable column2) {
            sb.append(combine(DEAL.OR, false, column, symbol, column2));
            return this;
        }

        public Criteria andBracketStart(Searchable column, String symbol, Searchable column2) {
            flag++;
            sb.append(combine(DEAL.AND, true, column, symbol, column2));
            return this;
        }

        public Criteria andBracketStart(Searchable column, String definition) {
            flag++;
            sb.append(combine(DEAL.AND, true, column, definition));
            return this;
        }

        public Criteria orBracketStart(Searchable column, String symbol, Searchable column2) {
            flag++;
            sb.append(combine(DEAL.OR, true, column, symbol, column2));
            return this;
        }

        public Criteria orBracketStart(Searchable column, String definition) {
            flag++;
            sb.append(combine(DEAL.OR, true, column, definition));
            return this;
        }

        public Criteria bracketEnd() {
            flag--;
            sb.append(" ) ");
            return this;
        }

        /* UserColumn.user_id = 1 */
        private String combine(DEAL deal, boolean bracketStart, Searchable column, String definition) {
            tables.add(column.getName().split("\\.")[0]);
            return sb.length() == 0 ?
                    String.format(" %s %s", column.getName(), definition) :
                    String.format(" %s %s%s %s ", deal, bracketStart ? "( " : "", column.getName(), definition);
        }

        /* UserColumn.user.id = IDCardColumn.user.id */
        private String combine(DEAL deal, boolean bracketStart, Searchable column, String symbol, Searchable column2) {
            tables.add(column.getName().split("\\.")[0]);
            tables.add(column2.getName().split("\\.")[0]);
            return sb.length() == 0 ?
                    String.format(" %s %s %s ", column.getName(), symbol, column2.getName()) :
                    String.format(" %s %s%s %s %s ", deal, bracketStart ? "( " : "", column.getName(), symbol, column2.getName());
        }

        @Override
        public String toString() {
            if (flag != 0)
                throw new RuntimeException(String.format("%s了 %s 个右括号！", flag > 0 ? "少" : "多", Math.abs(flag)));
            return sb.toString();
        }
    }
}
