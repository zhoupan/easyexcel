package com.alibaba.excel.support.usecase;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.excel.support.DataResultSupport;
import com.alibaba.excel.support.LoggerSupport;
import com.alibaba.excel.support.usecase.impl.FakeHumidityProvider;
import com.alibaba.excel.support.usecase.impl.FakeTemperatureProvider;
import com.alibaba.excel.util.DateUtils;

/**
 * The Class EnvUsecaseTest.
 */
public class EnvUsecaseExtraTest {

    /**
     * Test export to excel file.
     *
     * @param dir
     *            the dir
     * @param period
     *            the period
     * @param timeUnit
     *            the time unit
     * @param months
     *            the months
     */
    public void testExportToExcelFile(File dir, int period, TimeUnit timeUnit, int months) {
        FakeTemperatureProvider fakeTemperatureProvider = new FakeTemperatureProvider();
        fakeTemperatureProvider.period = period;
        fakeTemperatureProvider.timeUnit = timeUnit;
        FakeHumidityProvider fakeHumidityProvider = new FakeHumidityProvider();
        fakeHumidityProvider.period = period;
        fakeHumidityProvider.timeUnit = timeUnit;

        // 指定查询条件
        Date to = DateUtils.toDate(2021, 7, 1);
        Date from = DateUtils.addMonths(to, -months);
        DateRangeFilter filter = new DateRangeFilter();
        filter.from(from).to(to);
        EnvUsecase usecase = new EnvUsecase(fakeTemperatureProvider, fakeHumidityProvider);
        String format = "yyyyMMddHHmm";
        String filename = LoggerSupport.messageFormat("Months({})-{}({})-({}-{}).xlsx", months, timeUnit, period,
            DateUtils.format(from, format), DateUtils.format(to, format));
        File file = new File(dir, filename);
        DataResultSupport<File> result =
            usecase.exportToExcelFile(file, DateRangeFilter.create(filter, TimeUnit.DAYS, 1));
        Assert.assertTrue(result.getMessage(), result.isSuccess());
    }

    /**
     * Test export to excel for one month each 10 minutes.
     */
    @Test
    public void testExportToExcelForOneMonthEach10Minutes() {
        File data = new File("data");
        data.mkdirs();
        this.testExportToExcelFile(data, 10, TimeUnit.MINUTES, 1);
    }

    /**
     * Test export to excel for one month each 1 minute.
     */
    @Test
    public void testExportToExcelForOneMonthEach1Minute() {
        File data = new File("data");
        data.mkdirs();
        this.testExportToExcelFile(data, 1, TimeUnit.MINUTES, 1);
    }

    /**
     * Test export to excel for 12 months each 10 minutes.
     */
    @Test
    public void testExportToExcelFor12MonthsEach10Minutes() {
        File data = new File("data");
        data.mkdirs();
        this.testExportToExcelFile(data, 10, TimeUnit.MINUTES, 12);
    }

    /**
     * Test export to excel for 12 months each 1 minute.
     */
    @Test
    public void testExportToExcelFor12MonthsEach1Minute() {
        File data = new File("data");
        data.mkdirs();
        this.testExportToExcelFile(data, 1, TimeUnit.MINUTES, 12);
    }
}
