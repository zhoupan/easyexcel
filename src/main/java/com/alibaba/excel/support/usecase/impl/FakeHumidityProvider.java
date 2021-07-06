package com.alibaba.excel.support.usecase.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.excel.support.DataResultSupport;
import com.alibaba.excel.support.usecase.DateRangeFilter;
import com.alibaba.excel.support.usecase.Humidity;
import com.alibaba.excel.support.usecase.HumidityProvider;
import com.alibaba.excel.util.CoreUtils;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.RandomUtils;

/**
 * 湿度数据提供者.
 * 
 * @author zhoupan
 *
 */
public class FakeHumidityProvider implements HumidityProvider {

    /**
     * 数据产生的频率-timeUnit
     */
    public TimeUnit timeUnit = TimeUnit.MINUTES;

    /**
     * 数据产生的频率-单位
     */
    public int period = 10;

    /**
     * 根据设置的频率,在指定的时间范围内创建模拟数据
     */
    @Override
    public DataResultSupport<List<Humidity>> query(DateRangeFilter filter) {
        DataResultSupport<List<Humidity>> result = new DataResultSupport<List<Humidity>>();
        List<Humidity> items = new ArrayList<Humidity>();
        result.setData(items);
        try {
            CoreUtils.checkNotNull(filter.from, "from date not allow null");
            CoreUtils.checkNotNull(filter.to, "to date not allow null");
            CoreUtils.checkState(filter.from.before(filter.to), "from date must before to date");
            Date at = filter.from;
            while (at.before(filter.to)) {
                at = DateUtils.addMilliseconds(at, (int)this.timeUnit.toMillis(this.period));

                Humidity item = new Humidity();
                items.add(item);

                item.setDevice("device-00001");
                item.setValue(RandomUtils.nextInt(1, 100));
                item.setAt(at);

            }
        } catch (Throwable e) {
            result.onException(e);
        }
        return result;
    }
}
