package org.tree.commons.enumeration;

/**
 * @author er_dong_chen
 * @date 2019/3/18
 */
public enum Times {
    HUNDRED(100),
    THOUSAND(1_000),
    MILLION(1_000_000),
    BILLION(1_000_000_000);

    private int times;

    Times(int times) {
        this.times = times;
    }

    public int intValue() {
        return times;
    }
}