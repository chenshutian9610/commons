package org.tree.commons.support.mapper;

import org.tree.commons.utils.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author er_dong_chen
 * @date 2019/3/5
 */
public class UnionSearch {
    private final String select = "SELECT %s FROM %s WHERE %s ";
    private final String selectDistinct = "SELECT DISTINCTROW %s FROM %s WHERE %s ";

    private String columns = "*";
    private String tables;
    private Criteria criteria;
    private String orderBy;
    private String limit;
    private boolean distinct;

    public UnionSearch distinct() {
        distinct = true;
        return this;
    }

    public UnionSearch selectColumns(Args<?>... args) {
        columns = Arrays.stream(args).map(Args::toString).collect(Collectors.joining(","));
        tables = Arrays.stream(args).map(Args::getTableName).collect(Collectors.joining(","));
        return this;
    }

    public Criteria createCriteria() {
        criteria = new Criteria();
        return criteria;
    }

    public UnionSearch orderBy(Searchable column, boolean asc) {
        orderBy = String.format("ORDER BY %s %s ", column.getName(), asc ? "ASC" : "DESC");
        return this;
    }

    public UnionSearch limit(int start, int length) {
        limit = String.format("LIMIT %s,%s ", start, length);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder queryString = new StringBuilder();
        queryString.append(String.format(distinct ? selectDistinct : select,
                String.join(",", columns),
                String.join(",", tables),
                criteria.toString()));
        if (orderBy != null)
            queryString.append(orderBy);
        if (limit != null)
            queryString.append(limit);
        return queryString.toString();
    }


    public List<Map<?, ?>> runBy(UnionSearchMapper mapper) {
        return mapper.query(toString());
    }

    public <T> List<T> runBy(UnionSearchMapper mapper, Class<T> dto) {
        List<Map<?, ?>> result = mapper.query(toString());
        List<T> result2 = new ArrayList<>(result.size());
        result.forEach(map -> result2.add(MapUtils.parse(map, dto)));
        return result2;
    }

    /****************************** 内部类：仿 Mybatis::Example *******************************/

    public static class Criteria {
        enum DEAL {
            AND, OR
        }

        private StringBuilder sb = new StringBuilder();

        private int flag;

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

        private String combine(DEAL deal, boolean bracketStart, Searchable column, String definition) {
            return sb.length() == 0 ?
                    String.format("%s %s", column.getName(), definition) :
                    String.format("%s %s%s %s ", deal, bracketStart ? "( " : "", column.getName(), definition);
        }

        private String combine(DEAL deal, boolean bracketStart, Searchable column, String symbol, Searchable column2) {
            return sb.length() == 0 ?
                    String.format("%s %s %s ", column.getName(), symbol, column2.getName()) :
                    String.format("%s %s%s %s %s ", deal, bracketStart ? "( " : "", column.getName(), symbol, column2.getName());
        }

        @Override
        public String toString() {
            if (flag != 0)
                throw new RuntimeException(String.format("%s了 %s 个右括号！", flag > 0 ? "少" : "多", Math.abs(flag)));
            return sb.toString();
        }
    }
}
