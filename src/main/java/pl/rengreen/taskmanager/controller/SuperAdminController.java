package pl.rengreen.taskmanager.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.service.CompanyService;
import pl.rengreen.taskmanager.service.UserService;

@Controller
public class SuperAdminController {

    private UserService userService;
    private CompanyService companyService;

    @Autowired
    public SuperAdminController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/superAdmin/assignManager")
    public String assignManager(Principal principal, Model model, SecurityContextHolderAwareRequestWrapper request) {
        boolean isAdminSigned = request.isUserInRole("ROLE_ADMIN");
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<User> allUsers = companyService.getCompanyUsers(user.getCompany().getId());
        model.addAttribute("users", allUsers);
        model.addAttribute("isAdminSigned", isAdminSigned);

        return "views/assignManager";
    }

    @GetMapping("/superAdmin/makeManager/{id}")
    public String makeManager(@PathVariable Long id) {
        User user = userService.getUserById(id);
        userService.changeRoleToAdmin(user);
        return "redirect:/superAdmin/assignManager";
    }

    @GetMapping("/superAdmin/removeManager/{id}")
    public String removeManager(@PathVariable Long id) {
        User user = userService.getUserById(id);
        userService.changeRoleToUser(user);
        return "redirect:/superAdmin/assignManager";
    }

    @PostMapping("/superAdmin/addUsers/uploadExcel")
    public String uploadExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
            Principal principal) {
        try {
            // Parse the Excel file to create users
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            List<User> users = userService.createUsersFromExcel(file, user.getCompany());
            redirectAttributes.addFlashAttribute("message", "Users created successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading file: " + e.getMessage());
        }
        return "redirect:/superAdmin/addUsers/ExcelFile";
    }

    @GetMapping("/superAdmin/addUsers/ExcelFile")
    public String addUsers() {
        return "views/addUsersExcel";
    }
}
