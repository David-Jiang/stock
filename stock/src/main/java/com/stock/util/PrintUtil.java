package com.stock.util;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class PrintUtil {
	private static final Logger logger = LoggerFactory.getLogger("logs");
	
	public static JRXlsExporter exportExcel(final HttpServletResponse response, final String jasperName, final String fileName , final List<?> dataList,
			final Map<String, Object> param) throws Exception {
		JRXlsExporter exporter = new JRXlsExporter();
		try {
			InputStream jasperStream = PrintUtil.class.getResourceAsStream("/reports/" + jasperName + ".jasper");
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param,
					new JRBeanCollectionDataSource(dataList));

			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName,"UTF-8") + ".xls");
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");

			// 取消白色背景
			SimpleXlsxReportConfiguration conf = new SimpleXlsxReportConfiguration();
			conf.setWhitePageBackground(false);
			conf.setDetectCellType(true);
			conf.setRemoveEmptySpaceBetweenColumns(true);
			conf.setRemoveEmptySpaceBetweenRows(true);

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
			exporter.setConfiguration(conf);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return exporter;
	}
	
	public static JRXlsExporter exportSheetExcel(final HttpServletResponse response, final Map<String, String> jasperMap, final String fileName , final Map<String, List<?>> dataListMap,
			final Map<String, Object> param) throws Exception {
		JRXlsExporter exporter = new JRXlsExporter();
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			for (String jasperName : jasperMap.keySet()){
				InputStream jasperStream = PrintUtil.class.getResourceAsStream("/reports/" + jasperName + ".jasper");
				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
				JasperPrint jasperPrint = null;
				for (String dataListKey : dataListMap.keySet()) {
					if (StringUtils.equals(dataListKey, jasperName)) {
						jasperPrint = JasperFillManager.fillReport(jasperReport, param, new JRBeanCollectionDataSource(dataListMap.get(dataListKey)));
						break;
					}
				}
				jasperPrintList.add(jasperPrint); 
			}

			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName,"UTF-8") + ".xls");
			response.setContentType("application/vnd.ms-excel; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");

			// 設工作表
			SimpleXlsxReportConfiguration conf = new SimpleXlsxReportConfiguration();
			conf.setSheetNames(jasperMap.values().toArray(new String[jasperMap.values().size()]));
			conf.setOnePagePerSheet(false);
			conf.setWhitePageBackground(false);
			conf.setDetectCellType(true);

			exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
			exporter.setConfiguration(conf);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return exporter;
	}

	public static JRRtfExporter exportRtf(final HttpServletResponse response, final String jasperName, final String fileName , final List<?> dataList,
			final Map<String, Object> param) throws Exception {
		JRRtfExporter exporter = new JRRtfExporter();
		try {
			InputStream jasperStream = PrintUtil.class.getResourceAsStream("/reports/" + jasperName + ".jasper");
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param,
					new JRBeanCollectionDataSource(dataList));

			response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".rtf");
			response.setContentType("application/rtf");

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return exporter;
	}
	
	public static JRPdfExporter reportCombinator(final HttpServletResponse response, final List<String> pathList, final List<?> dataList,
			final List<Map<String, Object>> paramList, final String fileName) throws Exception {
		JRPdfExporter exporter = new JRPdfExporter();
		try {
			List<?> data;
			Map<String, Object> param;
			List<JasperPrint> jasperPrintList = new ArrayList<>();
			for (int i = 0; i < pathList.size(); i++) {
				InputStream jasperStream = PrintUtil.class.getResourceAsStream("/reports/" + pathList.get(i) + ".jasper");
				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
				data = (dataList.get(i) instanceof Collection<?>) ? (ArrayList<?>) dataList.get(i)
						: new ArrayList<>(Arrays.asList(dataList.get(i)));
				param = (paramList != null && paramList.size() > i) ? paramList.get(i) : new HashMap<String, Object>();
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param,
						new JRBeanCollectionDataSource(data));

				jasperPrintList.add(jasperPrint);
			}

			/* Setting HttpServletResponse */
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName); // fileName
																							// =
																							// mergedReport.pdf
			response.setContentType("application/pdf");

			exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return exporter;
	}
}
