package com.example.app.grid;

import com.example.app.grid.FilterModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public final class AgGridSpecifications {
    private AgGridSpecifications() {}

    public static <T> Specification<T> fromFilterModel(Map<String, FilterModel> fm) {
        if (fm == null || fm.isEmpty()) {
            return (root, q, cb) -> cb.conjunction();
        }

        return (root, q, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            for (Map.Entry<String, FilterModel> e : fm.entrySet()) {
                String field = e.getKey();
                FilterModel f = e.getValue();
                if (f == null) continue;

                // Skip unknown fields
                if (!safeHas(root, field)) continue;

                if (f.getOperator() != null && f.getCondition1() != null && f.getCondition2() != null) {
                    Predicate p1 = single(field, f.getFilterType(), f.getCondition1().getType(),
                            f.getCondition1().getFilter(), f.getCondition1().getDateFrom(), f.getCondition1().getDateTo(),
                            null, root, cb);

                    Predicate p2 = single(field, f.getFilterType(), f.getCondition2().getType(),
                            f.getCondition2().getFilter(), f.getCondition2().getDateFrom(), f.getCondition2().getDateTo(),
                            null, root, cb);

                    preds.add("OR".equalsIgnoreCase(f.getOperator()) ? cb.or(p1, p2) : cb.and(p1, p2));
                } else {
                    preds.add(single(field, f.getFilterType(), f.getType(), f.getFilter(),
                                     f.getDateFrom(), f.getDateTo(), f.getValues(), root, cb));
                }
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }

    private static boolean safeHas(Root<?> root, String field) {
        try { root.get(field); return true; } catch (IllegalArgumentException ex) { return false; }
    }

    private static <T> Predicate single(
            String field, String filterType, String type, String value,
            String dateFrom, String dateTo, List<String> setValues,
            Root<T> root, CriteriaBuilder cb
    ) {
        Path<?> path = root.get(field);
        if (filterType == null) return cb.conjunction();

        switch (filterType) {
            case "text": {
                String v = value == null ? "" : value.toLowerCase();
                switch (type == null ? "contains" : type) {
                    case "equals":      return cb.equal(cb.lower(root.get(field)), v);
                    case "startsWith":  return cb.like(cb.lower(root.get(field)), v + "%");
                    case "endsWith":    return cb.like(cb.lower(root.get(field)), "%" + v);
                    case "notEqual":    return cb.notEqual(cb.lower(root.get(field)), v);
                    case "notContains": return cb.notLike(cb.lower(root.get(field)), "%" + v + "%");
                    default:            return cb.like(cb.lower(root.get(field)), "%" + v + "%");
                }
            }
            case "number": {
                if (!(Number.class.isAssignableFrom(path.getJavaType()))) return cb.conjunction();
                BigDecimal num = toDecimal(value);
                if (num == null && !"inRange".equals(type)) return cb.conjunction();
                switch (type == null ? "equals" : type) {
                    case "greaterThan": return cb.greaterThan(root.get(field), num);
                    case "lessThan":    return cb.lessThan(root.get(field), num);
                    case "notEqual":    return cb.notEqual(root.get(field), num);
                    case "inRange": {
                        BigDecimal from = toDecimal(value);
                        BigDecimal to   = toDecimal(dateTo); // second bound piggybacking on dateTo
                        return (from != null && to != null) ? cb.between(root.get(field), from, to) : cb.conjunction();
                    }
                    default:            return cb.equal(root.get(field), num);
                }
            }
            case "boolean": {
                if (!Boolean.class.equals(path.getJavaType())) return cb.conjunction();
                boolean b = "true".equalsIgnoreCase(value) || "1".equals(value);
                return cb.equal(root.get(field), b);
            }
            case "set": {
                if (setValues == null || setValues.isEmpty()) return cb.conjunction();
                if (Boolean.class.equals(path.getJavaType())) {
                    List<Boolean> bools = new ArrayList<>();
                    for (String sv : setValues) {
                        if ("true".equalsIgnoreCase(sv) || "1".equals(sv))  bools.add(Boolean.TRUE);
                        if ("false".equalsIgnoreCase(sv) || "0".equals(sv)) bools.add(Boolean.FALSE);
                    }
                    return root.get(field).in(bools);
                }
                return root.get(field).in(setValues);
            }
            case "date": {
                LocalDate from = parseDate(dateFrom);
                LocalDate to   = parseDate(dateTo);
                Instant fromI  = (from != null) ? from.atStartOfDay().toInstant(ZoneOffset.UTC) : null;
                Instant toI    = (to != null)   ? to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC) : null; // exclusive
                switch (type == null ? "equals" : type) {
                    case "inRange":   return (fromI != null && toI != null) ? cb.between(root.get(field), fromI, toI) : cb.conjunction();
                    case "lessThan":  return (toI != null)   ? cb.lessThan(root.get(field), toI) : cb.conjunction();
                    case "greaterThan": return (fromI != null) ? cb.greaterThan(root.get(field), fromI) : cb.conjunction();
                    default:          // equals as a day range
                        if (fromI != null) {
                            Instant end = from.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
                            return cb.between(root.get(field), fromI, end);
                        }
                        return cb.conjunction();
                }
            }
            default:
                return cb.conjunction();
        }
    }

    private static BigDecimal toDecimal(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new BigDecimal(s); } catch (Exception e) { return null; }
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s);
    }
}
