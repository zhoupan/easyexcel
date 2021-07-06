package com.alibaba.excel.support.usecase;

import java.io.File;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.DataResultSupport;
import com.alibaba.excel.support.LoggerSupport;
import com.alibaba.excel.util.CoreUtils;
import com.alibaba.excel.util.IoUtils;
import com.alibaba.excel.util.StopWatch;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;

/**
 * 环境控制器需求:导出30天的历史温度和湿度数据. 1.温度和湿度分别放在单独的sheet
 * 
 * @author zhoupan
 *
 */
public class EnvUsecase {

    private TemperatureProvider temperatureProvider;
    private HumidityProvider humidityProvider;

    public EnvUsecase(TemperatureProvider temperatureProvider, HumidityProvider humidityProvider) {
        super();
        this.temperatureProvider = temperatureProvider;
        this.humidityProvider = humidityProvider;
    }

    public DataResultSupport<File> exportToExcelFile(File excelFile, DateRangeFilter filter) {
        StopWatch sw = new StopWatch(LoggerSupport.messageFormat("exportToExcelFile:{}:{}", excelFile, filter));
        DataResultSupport<File> result = new DataResultSupport<File>();
        ExcelWriterBuilder writerBuilder = null;
        try {
            writerBuilder = EasyExcel.write().file(excelFile);

            // 温度
            sw.start("temperature.query");
            DataResultSupport<List<Temperature>> temperatureQueryResult = this.temperatureProvider.query(filter);
            sw.stop();
            CoreUtils.checkState(temperatureQueryResult.isSuccess(), temperatureQueryResult.getMessage());
            List<Temperature> temperatureItems = temperatureQueryResult.getData();
            if (temperatureItems != null) {
                LoggerSupport.info("exportToExcelFile:{}:{}条温度记录", excelFile, temperatureItems.size());
                sw.start("temperature.excel.write");
                writerBuilder.newSheet("温度", Temperature.class).doWrite(temperatureItems, false);
                sw.stop();
            }
            // 湿度
            sw.start("humidity.query");
            DataResultSupport<List<Humidity>> queryResult = this.humidityProvider.query(filter);
            sw.stop();
            CoreUtils.checkState(queryResult.isSuccess(), queryResult.getMessage());
            List<Humidity> humidityItems = queryResult.getData();
            if (humidityItems != null) {
                LoggerSupport.info("exportToExcelFile:{}:{}条湿度记录", excelFile, humidityItems.size());
                sw.start("humidity.excel.write");
                writerBuilder.newSheet("湿度", Humidity.class).doWrite(humidityItems, false);
                sw.stop();
            }
        } catch (Throwable e) {
            result.onException(e);
        } finally {
            CoreUtils.closeQuietly(writerBuilder);
        }
        LoggerSupport.info("exportToExcelFile:{}:{}", excelFile, result.getMessage());
        LoggerSupport.info("exportToExcelFile:{}:{}", excelFile, sw);
        return result;
    }

}
