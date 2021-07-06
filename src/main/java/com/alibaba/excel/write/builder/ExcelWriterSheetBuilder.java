package com.alibaba.excel.write.builder;

import java.util.List;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.exception.ExcelGenerateException;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;

/**
 * Build sheet
 *
 * @author Jiaju Zhuang
 */
public class ExcelWriterSheetBuilder extends AbstractExcelWriterParameterBuilder<ExcelWriterSheetBuilder, WriteSheet> {
    private ExcelWriter excelWriter;
    /**
     * Sheet
     */
    private WriteSheet writeSheet;

    public ExcelWriterSheetBuilder() {
        this.writeSheet = new WriteSheet();
    }

    public ExcelWriterSheetBuilder(ExcelWriter excelWriter) {
        this.writeSheet = new WriteSheet();
        this.excelWriter = excelWriter;
    }

    public ExcelWriterSheetBuilder newSheet() {
        this.writeSheet = new WriteSheet();
        return this;
    }

    /**
     * Starting from 0
     *
     * @param sheetNo
     * @return
     */
    public ExcelWriterSheetBuilder sheetNo(Integer sheetNo) {
        writeSheet.setSheetNo(sheetNo);
        return this;
    }

    /**
     * sheet name
     *
     * @param sheetName
     * @return
     */
    public ExcelWriterSheetBuilder sheetName(String sheetName) {
        writeSheet.setSheetName(sheetName);
        return this;
    }

    public WriteSheet build() {
        return writeSheet;
    }

    /**
     * Write data and finish.
     * 
     * @param data
     * @return
     */
    public ExcelWriterSheetBuilder doWrite(List<?> data) {
        return this.doWrite(data, true);
    }

    /**
     * Write data.
     * 
     * @param data
     *            rows
     * @param isFinished
     *            if finished
     * @return
     */
    public ExcelWriterSheetBuilder doWrite(List<?> data, boolean isFinished) {
        if (excelWriter == null) {
            throw new ExcelGenerateException("Must use 'EasyExcelFactory.write().sheet()' to call this method");
        }
        excelWriter.write(data, build());
        if (isFinished) {
            excelWriter.finish();
        }
        return this;
    }

    /**
     * Write rows and not finish. Shorted method for doWrite(data, false)
     * 
     * @param data
     * @return
     */
    public ExcelWriterSheetBuilder append(List<?> data) {
        return doWrite(data, false);
    }

    /**
     * Finish this current work ,close worksheet and stream. Don't forget to call this method and release resources.
     * 
     * @return
     */
    public ExcelWriterSheetBuilder finish() {
        if (this.excelWriter != null) {
            this.excelWriter.finish();
        }
        return this;
    }

    public void doFill(Object data) {
        doFill(data, null);
    }

    public void doFill(Object data, FillConfig fillConfig) {
        if (excelWriter == null) {
            throw new ExcelGenerateException("Must use 'EasyExcelFactory.write().sheet()' to call this method");
        }
        excelWriter.fill(data, fillConfig, build());
        excelWriter.finish();
    }

    public ExcelWriterTableBuilder table() {
        return table(null);
    }

    public ExcelWriterTableBuilder table(Integer tableNo) {
        ExcelWriterTableBuilder excelWriterTableBuilder = new ExcelWriterTableBuilder(excelWriter, build());
        if (tableNo != null) {
            excelWriterTableBuilder.tableNo(tableNo);
        }
        return excelWriterTableBuilder;
    }

    @Override
    protected WriteSheet parameter() {
        return writeSheet;
    }

}
