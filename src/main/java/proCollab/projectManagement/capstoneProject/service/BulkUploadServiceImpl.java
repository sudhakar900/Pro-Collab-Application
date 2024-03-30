package proCollab.projectManagement.capstoneProject.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import proCollab.projectManagement.capstoneProject.model.Company;
import proCollab.projectManagement.capstoneProject.model.Role;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.CompanyRepository;
import proCollab.projectManagement.capstoneProject.repository.RoleRepository;
import proCollab.projectManagement.capstoneProject.repository.UserRepository;

@Service
public class BulkUploadServiceImpl implements BulkUploadService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private static final String USER = "USER";

    public void uploadUsersFromExcel(MultipartFile file, Company company) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() == 0) {
                    continue; // Skip header row
                }

                String email = currentRow.getCell(2).getStringCellValue();
                User existingUser = userRepository.findByEmail(email);
                if (existingUser != null) {
                    continue;
                }

                User user = new User();
                user.setEmail(email);
                user.setName(currentRow.getCell(1).getStringCellValue());
                Long employeeId = (long) (currentRow.getCell(0).getNumericCellValue());
                user.setEmployeeId(employeeId);
                String password = generateRandomPassword();
                user.setPassword(new BCryptPasswordEncoder().encode(password));

                // Company company =
                // companyRepository.findByName(currentRow.getCell(3).getStringCellValue());
                // if (company == null) {
                // continue;
                // }
                user.setCompany(company);
                user.setPhoto("images/admin.jpg");

                Role userRole = roleRepository.findByRole(USER);
                if (userRole == null) {
                    continue;
                }
                user.setRoles(Collections.singletonList(userRole));

                userRepository.save(user);
                emailService.sendInviteMail(email, employeeId, password);
            }
            workbook.close();
        }
    }

    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

}
