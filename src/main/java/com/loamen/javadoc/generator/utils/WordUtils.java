package com.loamen.javadoc.generator.utils;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;

import com.loamen.javadoc.generator.entity.ExceptionComment;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import com.loamen.javadoc.generator.entity.ClassComment;
import com.loamen.javadoc.generator.entity.FieldComment;
import com.loamen.javadoc.generator.entity.MethodComment;

/**
 * The type Word export.
 *
 * @author Don
 */
public class WordUtils {
    private String fontSimSun = "宋体";
    /**
     * 小四
     */
    private int fontSize12 = 12;
    /**
     * 默认五号字体
     */
    private int fontSizeDefault = 0;
    private String fontYaHei = "微软雅黑";
    private BigInteger numId;

    /**
     * Export.
     *
     * @param result the result
     * @param path   the path
     * @throws Exception the exception
     */
    public void export(List<ClassComment> result, String path) throws Exception {
        XWPFDocument xwpfDocument = this.newWord();
        numId = getNumId(xwpfDocument);           //set up the numbering scheme

        for (ClassComment classComment : result) {
            //属性
            generateFields(classComment, xwpfDocument);

            //方法
            generateMethods(classComment, xwpfDocument);

            this.newBlankLine(xwpfDocument);

        }
        this.writeFile(xwpfDocument, path);
    }

    /**
     * 生成属性
     *
     * @param classComment 类注释
     * @param xwpfDocument doc
     */
    private void generateFields(ClassComment classComment, XWPFDocument xwpfDocument) {
        // 字段
        if (classComment.getFields() != null && !classComment.getFields().isEmpty()) {
            this.newParagraph(xwpfDocument, classComment.getSimpleClassName(), fontSize12, fontSimSun, numId, BigInteger.ZERO);
            this.newParagraph(xwpfDocument, classComment.getClassComment(), fontSize12, fontSimSun, true);

            this.newParagraphCaption(xwpfDocument, classComment.getClassName() + "类主要属性", fontSize12, fontSimSun).setAlignment(ParagraphAlignment.CENTER);

            XWPFTable table = this.newTable(xwpfDocument, classComment.getFields().size() + 1, 4);
            this.setTableRowText(table.getRow(0), 0, 3, fontSizeDefault, fontSimSun, "序号", "定义", "默认值", "说明");
            this.setCellWidth(table.getRow(0), 0, 3, "10%", "45%", "22.5%", "22.5%");

            for (int i = 0, j = 1; i < classComment.getFields().size(); i++, j++) {
                FieldComment field = classComment.getFields().get(i);
                this.setTableRowText(table.getRow(j), 0, 3, fontSizeDefault, fontSimSun, String.valueOf(i + 1)
                        , field.getModifiers() + " " + field.getSimpleClassName() + " " + field.getFieldName()
                        , field.getDefaultValue() == null ? "" : field.getDefaultValue().toString()
                        , field.getFieldComment());
            }
            this.newBlankLine(xwpfDocument);
        }
    }

    /**
     * 生成方法
     *
     * @param classComment 类注释
     * @param xwpfDocument doc
     */
    private void generateMethods(ClassComment classComment, XWPFDocument xwpfDocument) {
        // 方法
        if (classComment.getMethods() != null && !classComment.getMethods().isEmpty()) {
            this.newParagraph(xwpfDocument, classComment.getSimpleClassName(), fontSize12, fontSimSun, numId, BigInteger.ZERO);
            this.newParagraph(xwpfDocument, classComment.getClassComment(), fontSize12, fontSimSun,true);
            for (MethodComment method : classComment.getMethods()) {
                this.newParagraph(xwpfDocument, method.getMethodName(), fontSize12, fontSimSun, numId, BigInteger.ONE);

                this.newParagraphCaption(xwpfDocument, method.getMethodName(), fontSize12, fontSimSun).setAlignment(ParagraphAlignment.CENTER);
                XWPFTable table = this.newTable(xwpfDocument, 1, 2);
                StringBuilder sb = new StringBuilder();
                sb.append(method.getModifiers()).append(" ");
                sb.append(method.getMethodName()).append("(");
                if (method.getParams() != null && !method.getParams().isEmpty()) {
                    for (int i = 0; i < method.getParams().size(); i++) {
                        FieldComment field = method.getParams().get(i);
                        sb.append(field.getSimpleClassName() + " " + field.getFieldName());
                    }
                }
                sb.append(")");
                sb.append("\n").append(method.getMethodComment());

                this.setTableRowText(table.getRow(0), 0, 1, fontSizeDefault, fontSimSun, "方法及功能", sb.toString());
                this.setCellWidth(table.getRow(0), 0, 1, "20%", "80%");
                // 参数
                Integer rowNum = 1;
                if (method.getParams() == null || method.getParams().isEmpty()) {
                    this.setTableRowText(table.createRow(), 0, 1, fontSizeDefault, fontSimSun, "参数", "无");
                } else {
                    for (int i = 0; i < method.getParams().size(); i++) {
                        FieldComment field = method.getParams().get(i);
                        this.setTableRowText(table.createRow(), 0, 1, fontSizeDefault, fontSimSun, ""
                                , field.getFieldName() + " - " + field.getFieldComment());
                        rowNum++;
                    }
                    this.mergeRow(table, 1, method.getParams().size(), 0, 0, "参数");
                }
                // 返回值
                this.setTableRowText(table.createRow(), 0, 1, fontSizeDefault, fontSimSun, "返回", method.getReturnEntity().getSimpleClassName().equals("void") ? "" : method.getReturnEntity().getSimpleClassName() + "  " + method.getReturnEntity().getFieldComment());
                // 异常
                if (method.getExceptions() != null && !method.getExceptions().isEmpty()) {
                    for (ExceptionComment exceptionComment : method.getExceptions()) {
                        this.setTableRowText(table.createRow(), 0, 1, fontSizeDefault, fontSimSun, "", exceptionComment.getSimpleClassName() + "  " + exceptionComment.getExceptionComment());
                    }

                    this.mergeRow(table, rowNum + 1, rowNum + method.getExceptions().size(), 0, 0, "抛出");
                }

                this.newBlankLine(xwpfDocument);
            }
        }
    }


    /**
     * 设置单元格宽度
     *
     * @param row
     * @param startCell   起始单元格下标，row的单元格下标从0开始
     * @param endCell     结束单元格下标
     * @param percentages 各个单元格的百分百大小，例如"25.5%"
     */
    private void setCellWidth(XWPFTableRow row, int startCell, int endCell, String... percentages) {
        if (percentages == null || percentages.length <= 0) {
            throw new IllegalArgumentException("percentages不能为空");
        }
        if ((endCell - startCell + 1) > percentages.length) {
            throw new IllegalArgumentException("percentages的元素不够");
        }
        int i = 0;
        for (XWPFTableCell cell : row.getTableCells()) {
            cell.setWidth(String.valueOf(percentages[i++]));
            cell.setWidthType(TableWidthType.PCT);
        }
    }


    /**
     * 设置单元格宽度
     *
     * @param row
     * @param startCell 起始单元格下标，row的单元格下标从0开始
     * @param endCell   结束单元格下标
     * @param sizes     各个单元格的宽度大小
     */
    @SuppressWarnings("unused")
    private void setCellWidth(XWPFTableRow row, int startCell, int endCell, int... sizes) {
        if (sizes == null || sizes.length <= 0) {
            throw new IllegalArgumentException("sizes不能为空");
        }
        if ((endCell - startCell + 1) > sizes.length) {
            throw new IllegalArgumentException("sizes的元素不够");
        }
        int i = 0;
        for (XWPFTableCell cell : row.getTableCells()) {
            cell.setWidth(String.valueOf(sizes[i++]));
            cell.setWidthType(TableWidthType.DXA);
        }
    }


    /**
     * 跨行合并单元格
     *
     * @param table
     * @param startRow  起始行下标，table的行下标从0开始
     * @param endRow    结束行下标
     * @param startCell 行内起始单元格下标，row的单元格下标从0开始
     * @param endCell   行内结束单元格下标
     * @param text      合并后的单元格文本
     */
    @SuppressWarnings("unused")
    private void mergeRow(XWPFTable table, int startRow, int endRow, int startCell, int endCell, String text) {
        List<XWPFTableRow> rows = table.getRows();
        for (int j = startRow; j <= endRow; j++) {
            List<XWPFTableCell> tableCells = rows.get(j).getTableCells();
            // 对每个单元格进行操作
            for (int i = startCell; i <= endCell; i++) {
                //对单元格进行合并的时候,要标志单元格是否为起点,或者是否为继续合并
                if (i == startCell) {
                    tableCells.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                } else {
                    //继续合并
                    tableCells.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
            }
        }
        for (int j = startRow; j <= endRow; j++) {
            List<XWPFTableCell> tableCells = rows.get(j).getTableCells();
            // 对每个单元格进行操作
            //对单元格进行合并的时候,要标志单元格是否为起点,或者是否为继续合并
            if (j == startRow) {
                tableCells.get(startCell).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                //继续合并
                tableCells.get(startCell).getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
        //为第1行1到4合并之后的单元格设置内容
        setFont(rows.get(startRow).getCell(startCell).getParagraphArray(0).createRun(), fontSizeDefault, fontSimSun).setText(text);
    }


    /**
     * 合并表格单元格，针对行内的单元格进行合并
     *
     * @param row
     * @param startCell 起始单元格下标，row的单元格下标从0开始
     * @param endCell   结束单元格下标
     * @param text      合并后的单元格文本
     */
    private void mergeCell(XWPFTableRow row, int startCell, int endCell, String text) {
        List<XWPFTableCell> tableCells = row.getTableCells();
        // 	对每个单元格进行操作
        for (int i = startCell; i <= endCell; i++) {
            //对单元格进行合并的时候,要标志单元格是否为起点,或者是否为继续合并
            if (i == startCell) {
                tableCells.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                //继续合并
                tableCells.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
        //为第1行1到4合并之后的单元格设置内容
        setFont(tableCells.get(startCell).getParagraphArray(0).createRun(), fontSize12, fontSimSun).setText(text);
    }


    /**
     * 给表格一行赋值，实际设置值是包括首尾单元格的，例如startCell=0，endCell=2，实际会设置0、1、2这三个单元格
     *
     * @param row
     * @param startCell 起始单元格下标，row的单元格下标从0开始
     * @param endCell   结束单元格下标
     * @param texts     单元格的内容，依次赋值
     */
    private void setTableRowText(XWPFTableRow row, int startCell, int endCell, String... texts) {
        setTableRowText(row, startCell, endCell, 0, null, texts);
    }

    /**
     * 表格一行赋值，实际设置值是包括首尾单元格的，例如startCell=0，endCell=2，实际会设置0、1、2这三个单元格
     *
     * @param row
     * @param startCell  起始单元格下标，row的单元格下标从0开始
     * @param endCell    结束单元格下标
     * @param fontSize   字体大小
     * @param fontFamily 字体
     * @param texts      单元格的内容，依次赋值
     */
    private void setTableRowText(XWPFTableRow row, int startCell, int endCell, int fontSize, String fontFamily, String... texts) {
        if (texts == null || texts.length <= 0) {
            throw new IllegalArgumentException("texts不能为空");
        }
        if ((endCell - startCell + 1) > texts.length) {
            throw new IllegalArgumentException("texts的元素不够");
        }
        List<XWPFTableCell> tableCells = row.getTableCells();
        // 	对每个单元格进行操作
        for (int i = startCell, j = 0; i <= endCell; i++, j++) {
            XWPFTableCell cell = tableCells.get(i);
            if (!StringUtil.isNullOrEmpty(fontFamily)) {
                setFont(cell.getParagraphArray(0).createRun(), fontSize, fontFamily).setText(texts[j]);
                if (texts[j].indexOf("\n") > -1) {
                    addBreakInCell(cell);
                }
            } else {
                cell.setText(texts[j]);
            }
        }
    }

    /**
     * 表格内换行
     *
     * @param cell 单元格
     */
    private void addBreakInCell(XWPFTableCell cell) {
        String breakChar = "\n";
        if (cell.getText() != null && cell.getText().contains(breakChar)) {
            for (XWPFParagraph p : cell.getParagraphs()) {
                // XWPFRun对象定义具有一组公共属性的文本区域
                p.setAlignment(ParagraphAlignment.LEFT);
                for (XWPFRun run : p.getRuns()) {
                    if (run.getText(0) != null && run.getText(0).contains(breakChar)) {
                        String[] lines = run.getText(0).split(breakChar);
                        if (lines.length > 0) {
                            // set first line into XWPFRun
                            run.setText(lines[0], 0);
                            for (int i = 1; i < lines.length; i++) {
                                // add break and insert new text
                                run.addBreak();//中断
//				                run.addCarriageReturn();//回车符，但是不起作用
                                run.setText(lines[i]);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 创建一个table
     *
     * @param xwpfDocument
     * @param rowNum       行数
     * @param colNum       列数
     */
    private XWPFTable newTable(XWPFDocument xwpfDocument, int rowNum, int colNum) {
        XWPFTable createTable = xwpfDocument.createTable(rowNum, colNum);
        createTable.setWidth("100%");
        createTable.setWidthType(TableWidthType.PCT);
        createTable.setCellMargins(0, 50, 0, 50);
        return createTable;
    }


    /**
     * 创建一个文本行
     */
    private XWPFParagraph newParagraph(XWPFDocument xwpfDocument, String text) {
        return newParagraph(xwpfDocument, text, fontSize12, fontSimSun, false);
    }

    /**
     * 创建一个文本行
     *
     * @param xwpfDocument xwpfDocument
     * @param text         文字
     * @param fontSize     字体大小
     * @param fontFamily   字体
     * @return
     */
    private XWPFParagraph newParagraph(XWPFDocument xwpfDocument, String text, int fontSize, String fontFamily, Boolean isAddTab) {
        return newParagraph(xwpfDocument, text, fontSize, fontFamily, BigInteger.valueOf(100), BigInteger.valueOf(100), isAddTab);
    }

    /**
     * 创建一个文本行
     *
     * @param xwpfDocument xwpfDocument
     * @param text         文字
     * @param fontSize     字体大小
     * @param fontFamily   字体
     * @return
     */
    private XWPFParagraph newParagraph(XWPFDocument xwpfDocument, String text, int fontSize, String fontFamily) {
        return newParagraph(xwpfDocument, text, fontSize, fontFamily, BigInteger.valueOf(100), BigInteger.valueOf(100), false);
    }

    /**
     * 创建一个文本行
     *
     * @param xwpfDocument xwpfDocument
     * @param text         文字
     * @param fontSize     字体大小
     * @param fontFamily   字体
     * @return
     */
    private XWPFParagraph newParagraph(XWPFDocument xwpfDocument, String text, int fontSize, String fontFamily, BigInteger numId, BigInteger numLevel) {
        return newParagraph(xwpfDocument, text, fontSize, fontFamily, numId, numLevel, false);
    }

    /**
     * 创建一个文本行
     *
     * @param xwpfDocument xwpfDocument
     * @param text         文字
     * @param fontSize     字体大小
     * @param fontFamily   字体
     * @return
     */
    private XWPFParagraph newParagraph(XWPFDocument xwpfDocument, String text, int fontSize, String fontFamily, BigInteger numId, BigInteger numLevel, Boolean isAddTab) {
        XWPFParagraph createParagraph = xwpfDocument.createParagraph();
        XWPFRun runX = setFont(createParagraph.createRun(), fontSize, fontFamily);
        if (isAddTab) {
            runX.addTab();
        }
        runX.setText(text);
        if (!numId.equals(BigInteger.valueOf(100))) {
            createParagraph.setNumID(numId);                              //make it numbered
        }
        if (!numLevel.equals(BigInteger.valueOf(100))) {
            CTDecimalNumber ctDecimalNumber = createParagraph.getCTP().getPPr().getNumPr().addNewIlvl();
            ctDecimalNumber.setVal(numLevel);                       //set the indent level
        }
        return createParagraph;
    }

    /**
     * 创建一个文本行
     *
     * @param xwpfDocument xwpfDocument
     * @param text         文字
     * @param fontSize     字体大小
     * @param fontFamily   字体
     * @return
     */
    private XWPFParagraph newParagraphCaption(XWPFDocument xwpfDocument, String text, int fontSize, String fontFamily) {
        XWPFParagraph createParagraph = xwpfDocument.createParagraph();
        XWPFRun runX = setFont(createParagraph.createRun(), fontSize, fontFamily);
        runX.setText("表");

        CTSimpleField seq = createParagraph.getCTP().addNewFldSimple();
        seq.setInstr(" SEQ 表 \\* ARABIC ");
        seq.setDirty(STOnOff.ON);

        runX = setFont(createParagraph.createRun(), fontSize, fontFamily);
        runX.setText(" " + text);
        return createParagraph;
    }

    /**
     * 设置字体
     *
     * @param runX
     * @param fontSize
     * @param fontFamily
     * @return
     */
    private XWPFRun setFont(XWPFRun runX, int fontSize, String fontFamily) {
        //设置字体大小
        if (fontSize != fontSizeDefault) {
            runX.setFontSize(fontSize);
        }
        runX.setFontFamily(fontFamily);
        return runX;
    }

    /**
     * 创建一个空行
     */
    private XWPFParagraph newBlankLine(XWPFDocument xwpfDocument) {
        return this.newParagraph(xwpfDocument, "");
    }

    /**
     * 创建一个word文档
     */
    private XWPFDocument newWord() {
        XWPFDocument xwpfDocument = new XWPFDocument();
        return xwpfDocument;
    }

    /**
     * 写文件
     */
    private void writeFile(XWPFDocument xwpfDocument, String path) throws Exception {
        xwpfDocument.write(new FileOutputStream(path));
        xwpfDocument.close();
    }

    /**
     * This method creates a numbering scheme and associates it with the working document
     *
     * @param document the document in which the numbering scheme will be used
     * @return The ID for the created numbering scheme
     */
    public BigInteger getNumId(XWPFDocument document) {

        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();      //create a numbering scheme
        cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));                  //give the scheme an ID

        /*first level*/
        CTLvl cTLvl0 = cTAbstractNum.addNewLvl();               //create the first numbering level
        cTLvl0.setIlvl(BigInteger.ZERO);                        //mark it as the top outline level
        cTLvl0.addNewNumFmt().setVal(STNumberFormat.DECIMAL);   //set the number format
        cTLvl0.addNewLvlText().setVal("%1.");                   //set the adornment; %1 is the first-level number or letter as set by number format
        cTLvl0.addNewStart().setVal(BigInteger.ONE);            //set the starting number (here, index from 1)
//        cTLvl0.addNewSuff().setVal(STLevelSuffix.SPACE);        //set space between number and text

        /*second level*/
        CTLvl cTLvl1 = cTAbstractNum.addNewLvl();               //create another numbering level
        cTLvl1.setIlvl(BigInteger.ONE);                         //specify that it's the first indent
        CTInd ctInd = cTLvl1.addNewPPr().addNewInd();           //add an indent
        ctInd.setLeft(inchesToTwips(.5));                       //set a half-inch indent
        cTLvl1.addNewNumFmt().setVal(STNumberFormat.DECIMAL);   //the rest is fairly similar
        cTLvl1.addNewLvlText().setVal("%1.%2.");                //setup to get 1.1, 1.2, ect.
        cTLvl1.addNewStart().setVal(BigInteger.ONE);
//        cTLvl1.addNewSuff().setVal(STLevelSuffix.SPACE);

        /*associate the numbering scheme with the document's numbering*/
        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
        XWPFNumbering numbering = document.createNumbering();
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        /*return an ID for the numbering*/
        return numbering.addNum(abstractNumID);
    }

    /**
     * @param inches
     * @return twentieths of a point (twips)
     */
    private BigInteger inchesToTwips(double inches) {
        return BigInteger.valueOf((long) (1440L * inches));
    }


    /**
     * creates a numbered list item at the specified depth
     *
     * @param doc           the document
     * @param numId         the numbering setup for the document
     * @param paragraphText the paragraph being created
     * @param numLevel      the indent level in the list
     */
    private void createNumberedParagraph(XWPFDocument doc, BigInteger numId, String paragraphText, BigInteger numLevel) {
        XWPFParagraph paragraph = doc.createParagraph();        //create the paragraph
        paragraph.createRun().setText(paragraphText);           //fill text
        paragraph.setNumID(numId);                              //make it numbered
        CTDecimalNumber ctDecimalNumber = paragraph.getCTP().getPPr().getNumPr().addNewIlvl();
        ctDecimalNumber.setVal(numLevel);                       //set the indent level
    }
}
