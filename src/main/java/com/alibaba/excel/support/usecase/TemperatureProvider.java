package com.alibaba.excel.support.usecase;

import java.util.List;

import com.alibaba.excel.support.DataResultSupport;

/**
 * 温度数据提供者.
 * 
 * @author zhoupan
 *
 */
public interface TemperatureProvider {

    /**
     * 根据条件返回温度数据列表.
     * 
     * @param filter
     *            过滤条件.
     * @return
     */
    DataResultSupport<List<Temperature>> query(DateRangeFilter filter);

}