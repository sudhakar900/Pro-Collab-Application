package pl.rengreen.taskmanager.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.Role;
import pl.rengreen.taskmanager.model.Task;
import pl.rengreen.taskmanager.model.Teams;
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.service.CompanyService;
import pl.rengreen.taskmanager.service.EmailService;
import pl.rengreen.taskmanager.service.ProjectService;
import pl.rengreen.taskmanager.service.TaskService;
import pl.rengreen.taskmanager.service.UserService;

@Controller
@RequestMapping("/assignment")
public class AssigmentController {
    private UserService userService;
    private TaskService taskService;
    private EmailService emailService;
    private CompanyService companyService;
    private ProjectService projectService;

    @Autowired
    public AssigmentController(UserService userService, TaskService taskService, EmailService emailService,
            CompanyService companyService, ProjectService projectService) {
        this.userService = userService;
        this.taskService = taskService;
        this.emailService = emailService;
        this.companyService = companyService;
        this.projectService = projectService;
    }

    public List<Task> freeTasks(Principal principal, long userId) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<Task> task = taskService.findFreeTasks();
        List<Task> companyTask = new ArrayList<>();
        long company_id = user.getCompany().getId();
        for (Task t : task) {
            if (companyService.isUserPresentInCompany(t.getCreatedUser(), company_id)) {
                Project project = t.getProject();
                User employee = userService.getUserById(userId);
                if (projectService.isUserPresentInProject(project, employee)
                        && projectService.isUserPresentInProject(project, user)) {
                    companyTask.add(t);
                }
            }
        }
        return companyTask;
    }

    @GetMapping
    public String showAssigmentForm(Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<User> allUsers = companyService.getCompanyUsers(user.getCompany().getId());
        List<User> ourUsers = new ArrayList<>();
        for (User u : allUsers) {
            for (Role role : u.getRoles()) {
                if (!role.getRole().equals("SUPERADMIN")) {
                    ourUsers.add(u);
                }
            }
        }
        model.addAttribute("users", ourUsers);
        return "forms/assignment";
    }

    @GetMapping("/project/{projectId}/task/{taskId}")
    public String assignProjectTask(@PathVariable long projectId, @PathVariable long taskId, Model model) {
        Project project = projectService.getProjectById(projectId);
        Task task = taskService.getTaskById(taskId);
        List<User> projectUsers = project.getEmployees();
        if (task.getOwner() != null) {
            projectUsers.remove(task.getOwner());
        }
        model.addAttribute("users", projectUsers);
        model.addAttribute("taskId", taskId);
        model.addAttribute("projectId", projectId);
        return "forms/taskAssignment";
    }

    @GetMapping("/project/{projectId}/task/{taskId}/assign/{userId}")
    public String assignToUser(@PathVariable long projectId, @PathVariable long taskId, @PathVariable long userId) {
        Task task = taskService.getTaskById(taskId);
        User user = userService.getUserById(userId);
        List<Teams> userTeams = user.getTeams();
        for (Teams t : userTeams) {
            if (t.getProject().getId() == projectId) {
                task.setTeam(t);
            }
        }
        taskService.assignTaskToUser(task, user);
        emailService.sendTaskMail(user.getEmail(), task);
        return "redirect:/projects/projectTasks/" + projectId + "/taskDetails/" + taskId;

    }

    @GetMapping("/{userId}")
    public String showUserAssigmentForm(@PathVariable Long userId, Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<User> allUsers = companyService.getCompanyUsers(user.getCompany().getId());
        List<Task> freeTasks = freeTasks(principal, userId);
        model.addAttribute("selectedUser", userService.getUserById(userId));
        model.addAttribute("users", allUsers);
        model.addAttribute("freeTasks", freeTasks);
        return "forms/assignTask";
    }

    @GetMapping("/assign/{userId}/{taskId}")
    public String assignTaskToUser(@PathVariable Long userId, @PathVariable Long taskId) {
        Task selectedTask = taskService.getTaskById(taskId);
        User selectedUser = userService.getUserById(userId);
        taskService.assignTaskToUser(selectedTask, selectedUser);
        emailService.sendTaskMail(selectedUser.getEmail(), selectedTask);
        return "redirect:/assignment/" + userId;
    }

    @GetMapping("unassign/{userId}/{taskId}")
    public String unassignTaskFromUser(@PathVariable Long userId, @PathVariable Long taskId) {
        Task selectedTask = taskService.getTaskById(taskId);
        taskService.unassignTask(selectedTask);
        return "redirect:/assignment/" + userId;
    }

}
