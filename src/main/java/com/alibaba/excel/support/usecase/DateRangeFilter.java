package com.alibaba.excel.support.usecase;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.alibaba.excel.util.DateUtils;

/**
 * 温度过滤条件.
 * 
 * @author zhoupan
 *
 */
public class DateRangeFilter {

    public Date from;
    public Date to = new Date();

    public DateRangeFilter to(Date date) {
        if (this.to == null) {
            this.to = new Date();
        }
        this.to.setTime(date.getTime());
        return this;
    }

    public DateRangeFilter from(Date date) {
        if (this.from == null) {
            this.from = new Date();
        }
        this.from.setTime(date.getTime());
        return this;
    }

    public DateRangeFilter toNow() {
        return to(new Date());
    }

    public DateRangeFilter from(int period, TimeUnit unit) {
        this.from = DateUtils.addMilliseconds(this.to, -period);
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

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

    @Override
    public String toString() {
        return "DateRangeFilter [from=" + DateUtils.format(from) + ", to=" + DateUtils.format(to) + "]";
    }

}
