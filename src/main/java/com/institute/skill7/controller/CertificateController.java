package com.institute.skill7.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.institute.skill7.model.Certificate;
import com.institute.skill7.repository.CertificateRepository;
@RestController
@RequestMapping("/api/certificates")

public class CertificateController {

    @Autowired
    private CertificateRepository repo;

    

    
    @PostMapping
    public Certificate addCertificate(
            @RequestParam("title") String title,
            @RequestParam("organization") String organization,
            @RequestParam("issueDate") String issueDate,
            @RequestParam(value = "expiryDate", required = false) String expiryDate,
            @RequestParam("userEmail") String userEmail,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
    	System.out.println("DATE RECEIVED: " + issueDate);
    	if (file!=null) {
    	System.out.println("FILE: " + file.getOriginalFilename());
    	}

        // 📁 folder path
    	String uploadDir = System.getProperty("user.dir") + "/uploads/";

    	File dir = new File(uploadDir);
    	if (!dir.exists()) dir.mkdirs();

    	String fileName = null;

    	if (file != null && !file.isEmpty()) {
    	    fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    	    File dest = new File(uploadDir + fileName);

    	    try {
    	        file.transferTo(dest);
    	    } catch (Exception e) {
    	        e.printStackTrace(); // 🔥 show real error
    	    }
    	}
    	

        // save DB
        Certificate cert = new Certificate();
        cert.setTitle(title);
        cert.setOrganization(organization);
        cert.setIssueDate(issueDate);
        cert.setExpiryDate(expiryDate);
        cert.setUserEmail(userEmail);
        cert.setFileName(fileName);
        
        return repo.save(cert);
    }
    
    @DeleteMapping("/{id}/{email}")
    public String deleteCertificate(@PathVariable Long id, @PathVariable String email) {
        Certificate cert = repo.findById(id).orElse(null);

        if (cert != null && cert.getUserEmail().equals(email)) {
            repo.deleteById(id);
            return "Deleted";
        }

        return "Not allowed";
    }
    @GetMapping
    public java.util.List<Certificate> getAllCertificates() {
        return repo.findAll();
    }
    @GetMapping("/{email}")
    public List<Certificate> getCertificatesByUser(@PathVariable String email) {
        return repo.findByUserEmail(email);
    }
    @GetMapping("/file/{fileName}")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> getFile(
            @PathVariable String fileName) throws Exception {

        java.nio.file.Path path = java.nio.file.Paths.get("uploads").resolve(fileName);
        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.UrlResource(path.toUri());

        return org.springframework.http.ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")   // 🔥 fix CORS
                .header("Content-Type", "application/pdf")    // or image/*
                .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}