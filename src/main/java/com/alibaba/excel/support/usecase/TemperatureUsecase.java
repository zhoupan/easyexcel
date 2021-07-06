package com.alibaba.excel.support.usecase;

import java.io.File;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.DataResultSupport;
import com.alibaba.excel.support.LoggerSupport;
import com.alibaba.excel.util.CoreUtils;
import com.alibaba.excel.util.StopWatch;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;

/**
 * 需求:导出30天的历史温度数据.
 * 
 * @author zhoupan
 *
 */
public class TemperatureUsecase {

    private TemperatureProvider temperatureProvider;

    public TemperatureUsecase(TemperatureProvider temperatureProvider) {
        super();
        this.temperatureProvider = temperatureProvider;
    }

    public DataResultSupport<File> exportToExcelFile(File excelFile, DateRangeFilter filter) {
        StopWatch sw = new StopWatch(LoggerSupport.messageFormat("exportToExcelFile:{}:{}", excelFile, filter));
        DataResultSupport<File> result = new DataResultSupport<File>();
        try {
            ExcelWriterBuilder writerBuilder = EasyExcel.write().head(Temperature.class).file(excelFile);
            sw.start("temperatureProvider.query");
            DataResultSupport<List<Temperature>> queryResult = this.temperatureProvider.query(filter);
            sw.stop();
            CoreUtils.checkState(queryResult.isSuccess(), queryResult.getMessage());
            List<Temperature> items = queryResult.getData();
            if (items != null) {
                LoggerSupport.info("exportToExcelFile:{}:{}条记录", excelFile, items.size());
                sw.start("excel.write");
                writerBuilder.sheet("温度").doWrite(items);
                sw.stop();
            }
        } catch (Throwable e) {
            result.onException(e);
        }
        LoggerSupport.info("exportToExcelFile:{}:{}", excelFile, result.getMessage());
        LoggerSupport.info("exportToExcelFile:{}:{}", excelFile, sw);
        return result;
    }
}
