package com.alibaba.excel.support.usecase;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 温度数据.
 * 
 * @author zhoupan
 *
 */
@ColumnWidth(20)
public class Temperature {
    /**
     * 物联网设备(android)本地数据库的主键(可选)
     */
    @ExcelProperty("序号")
    private Long id;
    /**
     * 物联网设备的序列号.
     */
    @ExcelProperty("设备")
    private String device;
    @ExcelProperty("温度")
    private BigDecimal value;
    @ExcelProperty("时间")
    private Date at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    @Override
    public String toString() {
        return "Temperature [id=" + id + ", device=" + device + ", value=" + value + ", at=" + at + "]";
    }

}
