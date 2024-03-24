package pl.rengreen.taskmanager.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import pl.rengreen.taskmanager.model.ChatMessage;
import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.Role;
import pl.rengreen.taskmanager.model.Task;
import pl.rengreen.taskmanager.model.TaskHistory;
import pl.rengreen.taskmanager.model.Teams;
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.repository.ChatMessageRepository;
import pl.rengreen.taskmanager.repository.ProjectRepository;
import pl.rengreen.taskmanager.repository.TaskHistoryRepository;
import pl.rengreen.taskmanager.repository.TaskRepository;
import pl.rengreen.taskmanager.repository.TeamRepository;
import pl.rengreen.taskmanager.repository.UserRepository;
import pl.rengreen.taskmanager.service.CompanyService;
import pl.rengreen.taskmanager.service.UserService;

@Controller
public class UserController {

    private UserService userService;
    private CompanyService companyService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private TaskHistoryRepository taskHistoryRepository; // Assuming you have a repository for TaskHistory entity
    @Autowired
    private TaskRepository taskRepository; // Assuming you have a repository for Task entity

    @Autowired
    private TeamRepository teamUserRepository; // Assuming you have a repository for TeamUser entity

    @Autowired
    public UserController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/users")
    public String listUsers(Principal principal, Model model, SecurityContextHolderAwareRequestWrapper request) {
        boolean isAdminSigned = request.isUserInRole("ROLE_ADMIN");
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
        model.addAttribute("isAdminSigned", isAdminSigned);
        return "views/users";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        // Fetch the user by id
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            List<ChatMessage> chatMessages = chatMessageRepository.findBySenderOrRecipient(user, user);
            chatMessageRepository.deleteAll(chatMessages);
            List<TaskHistory> taskHistories = taskHistoryRepository.findByUser(user);
            taskHistoryRepository.deleteAll(taskHistories);

            // Remove the user from associated projects
            List<Project> projects = user.getProjects();
            for (Project project : projects) {
                project.getEmployees().remove(user);
                projectRepository.save(project); // Update the project to reflect changes
            }
            List<Teams> teams = user.getTeams();
            for (Teams team : teams) {
                team.getUsers().remove(user);
                teamUserRepository.save(team);
            }
            List<Task> tasksOwnedByUser = taskRepository.findByOwnerOrderByDateDesc(user);

            // Then remove the owner reference from each task
            for (Task task : tasksOwnedByUser) {
                task.setOwner(null);
                taskRepository.save(task); // Update the task without the owner reference
            }
            // Now delete the user
            userRepository.deleteById(id);
        }

        return "redirect:/users";
    }

}
