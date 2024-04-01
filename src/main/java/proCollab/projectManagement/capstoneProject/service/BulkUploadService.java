package proCollab.projectManagement.capstoneProject.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import proCollab.projectManagement.capstoneProject.model.Company;

public interface BulkUploadService {
    void uploadUsersFromExcel(MultipartFile file, Company company) throws IOException;

}
