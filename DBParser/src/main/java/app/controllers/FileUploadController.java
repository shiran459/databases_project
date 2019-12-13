package app.controllers;

import app.lib.ServerLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.storage.StorageService;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This controller handles uploading of files.
 */
@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/articles/upload")
    public String handleFileUpload(@RequestParam("articleToUpload") MultipartFile file, @RequestParam("Title") String title,
                                   RedirectAttributes redirectAttributes) {

        try{
            Path path = storageService.store(file);
            ServerLib.uploadArticle(title, path);
            Files.delete(path);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");

            return "redirect:/articles/upload/success";
        } catch (Exception e){
            return "error";
        }

    }

}