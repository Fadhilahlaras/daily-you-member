package coba.daily.you.controller.report;


import coba.daily.you.configuration.exception.ReportException;
import coba.daily.you.model.entity.Input;
import coba.daily.you.report.CustomJRDataSource;
import coba.daily.you.repository.InputRepository;
import io.swagger.models.auth.In;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : backend
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 05/05/18
 * Time: 08.54
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ReportController {
    @Autowired
    InputRepository inputRepository;

    @Autowired
    private ModelMapper modelMapper;

    final static Logger logger = Logger.getLogger(ReportController.class);
    final static String pdfSource = "src/main/resources/static/assets/jasper/report.pdf";

    private void generateReport(List<Input> inputs) throws JRException{
        logger.info("[!] Start generate report");
        // Path to our template goes here
        JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/static/assets/jasper/report.jrxml");
        // load data to datasource
        CustomJRDataSource<Input> dataSource = new CustomJRDataSource<Input>().using(inputs);
        // Map datasource to template
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String,Object>(), dataSource);
        // Export to pdf
        JasperExportManager.exportReportToPdfFile(jasperPrint,pdfSource);
    }

    @RequestMapping(value = "/getReport", method = RequestMethod.GET)
    public HttpEntity<byte[]> getReport() throws JRException,IOException {
        // Stub data

        List<Input> inputs = inputRepository.findAll();

        System.out.println(inputs);
        generateReport(inputs);
        logger.info("[+] Generated report successfully");

        // Force download
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition",
                "attachment; filename=" + "intan.pdf");
        byte[] documentBody = Files.readAllBytes(new File(pdfSource).toPath());
        header.setContentLength(documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);
    }

    private byte[] generateReportXlsx(List<Input> inputs) throws JRException, ReportException{
        logger.info("[!] Start generate report");
        // Path to our template goes here
        JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/static/assets/jasper/report.jrxml");
        // load data to datasource
        CustomJRDataSource<Input> dataSource = new CustomJRDataSource<Input>().using(inputs);
        // Map datasource to template
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String,Object>(), dataSource);
        // Export to pdf

        return getReportXlx(jasperPrint);
    }

    private byte[] getReportXlx(JasperPrint jasperPrint) throws ReportException{
        final JRXlsxExporter xlsxExporter = new JRXlsxExporter();
        final byte[] rawBytes;

        try(final ByteArrayOutputStream xlsReport = new ByteArrayOutputStream()){
            xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));
            xlsxExporter.exportReport();

            rawBytes = xlsReport.toByteArray();
        } catch (JRException | IOException e) {
            throw new ReportException(e);
        }

        return rawBytes;
    }


    @RequestMapping(value = "/getReportExcel", method = RequestMethod.GET)
    public HttpEntity<byte[]> getReportExcel() throws JRException, IOException, ReportException {
        // Stub data

        List<Input> inputs = inputRepository.findAll();
        System.out.println(inputs);
        logger.info("[+] Generated report successfully");
        byte[] data=generateReportXlsx(inputs);
        // Force download
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=inputReport.xlsx");
        header.setContentLength(data.length);
//        byte[] documentBody = Files.readAllBytes(new File(pdfSource).toPath());

        header.setContentLength(data.length);
        return new HttpEntity<byte[]>(data, header);
    }
}
