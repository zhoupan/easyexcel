/*
 * Copyright (c) 2021 GetWrath.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.alibaba.excel;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.alibaba.excel.support.DataResultSupport;
import com.alibaba.excel.support.LoggerSupport;
import com.alibaba.excel.support.usecase.DateRangeFilter;
import com.alibaba.excel.support.usecase.EnvUsecase;
import com.alibaba.excel.support.usecase.impl.FakeHumidityProvider;
import com.alibaba.excel.support.usecase.impl.FakeTemperatureProvider;
import com.alibaba.excel.util.DateUtils;
import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
  public final String device = "31706835";

  @Test
  public void useAppContext() {
    // https://github.com/zhoupan/poi-on-android
    // poishadow-all.jar
    System.setProperty(
        "org.apache.poi.javax.xml.stream.XMLInputFactory",
        "com.fasterxml.aalto.stax.InputFactoryImpl");
    System.setProperty(
        "org.apache.poi.javax.xml.stream.XMLOutputFactory",
        "com.fasterxml.aalto.stax.OutputFactoryImpl");
    System.setProperty(
        "org.apache.poi.javax.xml.stream.XMLEventFactory",
        "com.fasterxml.aalto.stax.EventFactoryImpl");

    // Context of the app under test.
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    assertEquals("com.alibaba.excel", appContext.getPackageName());
    File filesDir = appContext.getFilesDir();
    this.testExportToExcelFile(filesDir, 10, TimeUnit.MINUTES, 1);
    this.testExportToExcelFile(filesDir, 1, TimeUnit.MINUTES, 1);
    this.testExportToExcelFile(filesDir, 10, TimeUnit.MINUTES, 12);
    this.testExportToExcelFile(filesDir, 1, TimeUnit.MINUTES, 12);
  }

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
    String filename =
        LoggerSupport.messageFormat(
            "Months({})-{}({})-({}-{}).xlsx",
            months,
            timeUnit,
            period,
            DateUtils.format(from, format),
            DateUtils.format(to, format));
    File file = new File(dir, filename);
    DataResultSupport<File> result =
        usecase.exportToExcelFile(file, DateRangeFilter.create(filter, TimeUnit.DAYS, 1));
    Assert.assertTrue(result.getMessage(), result.isSuccess());
  }
}
