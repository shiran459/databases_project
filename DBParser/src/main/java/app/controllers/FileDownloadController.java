package app.controllers;

import app.lib.XMLDumper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class FileDownloadController {
    @GetMapping("/db_admin/dump_db")
    public void downloadDBDump(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {


        Path file = XMLDumper.buildTables();
        if (Files.exists(file)) {
            response.setContentType("application/xml");
            response.addHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }
}