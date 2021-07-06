package com.alibaba.excel.support.usecase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.excel.util.CoreUtils;
import com.alibaba.excel.util.DateUtils;

/**
 * 时间范围过滤条件.
 * 
 * @author zhoupan
 *
 */
public class DateRangeFilter {

    /** The from. */
    public Date from;

    /** The to. */
    public Date to = new Date();

    /**
     * To.
     *
     * @param date
     *            the date
     * @return the date range filter
     */
    public DateRangeFilter to(Date date) {
        if (this.to == null) {
            this.to = new Date();
        }
        this.to.setTime(date.getTime());
        return this;
    }

    /**
     * From.
     *
     * @param date
     *            the date
     * @return the date range filter
     */
    public DateRangeFilter from(Date date) {
        if (this.from == null) {
            this.from = new Date();
        }
        this.from.setTime(date.getTime());
        return this;
    }

    /**
     * To now.
     *
     * @return the date range filter
     */
    public DateRangeFilter toNow() {
        return to(new Date());
    }

    /**
     * From.
     *
     * @param period
     *            the period
     * @param unit
     *            the unit
     * @return the date range filter
     */
    public DateRangeFilter from(int period, TimeUnit unit) {
        this.from = DateUtils.addMilliseconds(this.to, -period);
        return this;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    /**
     * Equals.
     *
     * @param obj
     *            the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DateRangeFilter other = (DateRangeFilter)obj;
        if (from == null) {
            if (other.from != null) {
                return false;
            }
        } else if (!from.equals(other.from)) {
            return false;
        }
        if (to == null) {
            if (other.to != null) {
                return false;
            }
        } else if (!to.equals(other.to)) {
            return false;
        }
        return true;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "DateRangeFilter [from=" + DateUtils.format(from) + ", to=" + DateUtils.format(to) + "]";
    }

    /**
     * 按指定的时间范围分断.
     * 例如:12个月范围(2020-01-01~2021-01-01)按月分断,2020-01-01~2020-02-01,2020-02-01~2020-03-01.....2020-12-01~2021-01-01
     *
     * @param from
     *            the from
     * @param to
     *            the to
     * @param timeUnit
     *            the time unit
     * @param peroid
     *            the peroid
     * @return the list
     */
    public static List<DateRangeFilter> create(Date from, Date to, TimeUnit timeUnit, int peroid) {
        CoreUtils.checkNotNull(from, "form not allow null");
        CoreUtils.checkNotNull(to, "to not allow null");
        CoreUtils.checkNotNull(timeUnit, "timeUnit not allow null");
        CoreUtils.checkState(peroid > 0, "peroid must greater than 0");
        List<DateRangeFilter> items = new ArrayList<DateRangeFilter>();
        Date at = from;
        while (at.before(to)) {
            Date next = DateUtils.addMilliseconds(at, (int)timeUnit.toMillis(peroid));
            items.add(new DateRangeFilter().from(at).to(next));
            at = next;
        }
        return items;
    }

    /**
     * Creates the.
     *
     * @param filter
     *            the filter
     * @param timeUnit
     *            the time unit
     * @param peroid
     *            the peroid
     * @return the list
     */
    public static List<DateRangeFilter> create(DateRangeFilter filter, TimeUnit timeUnit, int peroid) {
        return create(filter.from, filter.to, timeUnit, peroid);
    }
}
