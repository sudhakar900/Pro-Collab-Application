package proCollab.projectManagement.capstoneProject.controller;

import java.security.Principal;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proCollab.projectManagement.capstoneProject.model.Company;
import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.service.CompanyService;
import proCollab.projectManagement.capstoneProject.service.ProjectService;
import proCollab.projectManagement.capstoneProject.service.UserService;

@Controller
@RequestMapping("/projects")
public class ProjectOperationsController {

    private final ProjectService projectService;
    private final UserService userService;
    private final CompanyService companyService;

    @Autowired
    public ProjectOperationsController(ProjectService projectService, UserService userService,
            CompanyService companyService) {
        this.projectService = projectService;
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/createProject")
    public String createProject() {
        return "forms/CreateProject";
    }

    @GetMapping("/projectList")
    public String showProjects(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<Project> projects = projectService.findByCompanyId(user.getCompany().getId());
        List<Project> userProjects = userService.getUserProjects(user);
        model.addAttribute("projects", projects);
        model.addAttribute("userProjects", userProjects);
        return "views/projects";
    }

    @PostMapping("/markComplete/{projectId}")
    public String markComplete(@PathVariable("projectId") long projectId) {
        Project project = projectService.getProjectById(projectId);

        // Toggle project completion status
        boolean isCompleted = project.isCompleted();
        project.setCompleted(!isCompleted);
        projectService.updateProject(project); // Assuming you have an update method in service

        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/{projectId}")
    public String showProjectDetails(Model model, @PathVariable Long projectId, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        Project project = projectService.getProjectById(projectId);
        List<User> availableUsers = companyService.getCompanyUsers(user.getCompany().getId());
        List<User> projectEmployees = projectService.getAllProjectEmployees(projectId);
        for (User employee : projectEmployees) {
            if (availableUsers.contains(employee)) {
                availableUsers.remove(employee);
            }
        }
        model.addAttribute("project", project);
        model.addAttribute("availableUsers", availableUsers);
        model.addAttribute("projectEmployees", projectEmployees);
        model.addAttribute("projectId", projectId); // Pass the projectId to the view
        return "views/projectDetails";
    }

    @GetMapping("/employees/{projectId}")
    public String viewProjectEmployees(@PathVariable long projectId, Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        Project project = projectService.getProjectById(projectId);
        List<User> availableUsers = companyService.getCompanyUsers(user.getCompany().getId());
        List<User> projectEmployees = projectService.getAllProjectEmployees(projectId);
        for (User employee : projectEmployees) {
            if (availableUsers.contains(employee)) {
                availableUsers.remove(employee);
            }
        }
        model.addAttribute("project", project);
        model.addAttribute("availableUsers", availableUsers);
        return "views/projectEmployees";
    }

    @GetMapping("/tasks/{projectId}")
    public String viewProjectTasks(@PathVariable long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        return "views/projectTasks";
    }

    @GetMapping("/yourProjects")
    public String getYourProjects(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<Project> usersProjectList = user.getProjects();
        model.addAttribute("projects", usersProjectList);
        return "views/yourProjects";
    }

    @PostMapping("/mapUsersToProject")
    public String mapUsersToProject(@RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            RedirectAttributes redirectAttributes) {
        try {
            Project project = projectService.getProjectById(projectId); // Assuming you have a method to get project by
                                                                        // ID
            if (project == null) {
                return "redirect:/projects/employees/" + projectId;
            }

            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                String email = row.getCell(2).getStringCellValue();

                User user = userService.getUserByEmail(email);
                if (user != null && !projectService.isUserPresentInProject(project, user)) {
                    projectService.addEmployeeToProject(projectId, user);
                }
            }

            workbook.close();
            return "redirect:/projects/employees/" + projectId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }
}
