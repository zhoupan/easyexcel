package com.alibaba.excel.support.usecase;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.excel.support.DataResultSupport;
import com.alibaba.excel.support.LoggerSupport;
import com.alibaba.excel.support.usecase.impl.FakeTemperatureProvider;
import com.alibaba.excel.util.DateUtils;

public class TemperatureUsecaseTest {

    public void testTemperatureFilter() {
        Date to = DateUtils.toDate(2021, 7, 3);
        Date from = DateUtils.addDays(to, -2);
        DateRangeFilter filter = new DateRangeFilter();
        filter.from(from).to(to);
        LoggerSupport.info("filter:{}", filter);
    }

    public void testTemperatureProvider() {
        Date to = DateUtils.toDate(2021, 7, 3);
        Date from = DateUtils.addDays(to, -1);
        DateRangeFilter filter = new DateRangeFilter();
        filter.from(from).to(to);
        FakeTemperatureProvider fake = new FakeTemperatureProvider();
        // 1小时一条记录
        fake.period = 1;
        fake.timeUnit = TimeUnit.HOURS;
        DataResultSupport<List<Temperature>> result = fake.query(filter);
        Assert.assertTrue(result.getMessage(), result.isSuccess());
        // 1天24小时
        Assert.assertEquals(24, result.getData().size());
    }

    public void testExportToExcelFile(File dir, int period, TimeUnit timeUnit, int months) {
        FakeTemperatureProvider fake = new FakeTemperatureProvider();
        // 实现用户的需求：10分钟记录一次温度值,保存30天的数据
        fake.period = period;
        fake.timeUnit = timeUnit;
        // 指定温度查询条件
        Date to = DateUtils.toDate(2021, 7, 1);
        Date from = DateUtils.addMonths(to, -months);
        DateRangeFilter filter = new DateRangeFilter();
        filter.from(from).to(to);
        TemperatureUsecase usecase = new TemperatureUsecase(fake);
        String format = "yyyyMMddHHmm";
        String filename = LoggerSupport.messageFormat("({})Months-({}{})温度({}-{}).xlsx", months, period, timeUnit,
            DateUtils.format(from, format), DateUtils.format(to, format));
        File file = new File(dir, filename);
        DataResultSupport<File> result = usecase.exportToExcelFile(file, filter);
        Assert.assertTrue(result.getMessage(), result.isSuccess());
    }

    @Test
    public void testExportToExcel() {
        File data = new File("data");
        data.mkdirs();
        this.testExportToExcelFile(data, 10, TimeUnit.MINUTES, 1);
        this.testExportToExcelFile(data, 1, TimeUnit.MINUTES, 1);
        this.testExportToExcelFile(data, 10, TimeUnit.MINUTES, 12);
        this.testExportToExcelFile(data, 1, TimeUnit.MINUTES, 12);
    }

}
