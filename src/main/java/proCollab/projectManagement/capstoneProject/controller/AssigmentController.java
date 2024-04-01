package proCollab.projectManagement.capstoneProject.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Role;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.Teams;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.TeamRepository;
import proCollab.projectManagement.capstoneProject.service.CompanyService;
import proCollab.projectManagement.capstoneProject.service.EmailService;
import proCollab.projectManagement.capstoneProject.service.ProjectService;
import proCollab.projectManagement.capstoneProject.service.TaskService;
import proCollab.projectManagement.capstoneProject.service.UserService;

@Controller
@RequestMapping("/assignment")
public class AssigmentController {
    private UserService userService;
    private TaskService taskService;
    private EmailService emailService;
    private CompanyService companyService;
    private ProjectService projectService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    public AssigmentController(UserService userService, TaskService taskService, EmailService emailService,
            CompanyService companyService, ProjectService projectService) {
        this.userService = userService;
        this.taskService = taskService;
        this.emailService = emailService;
        this.companyService = companyService;
        this.projectService = projectService;
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

    @GetMapping("/project/{projectId}/task/{taskId}/assign/{userId}")
    public String assignToUser(@PathVariable long projectId, @PathVariable long taskId, @PathVariable long userId) {
        Task task = taskService.getTaskById(taskId);
        User prevUser = task.getOwner();
        User user = userService.getUserById(userId);
        List<Teams> userTeams = user.getTeams();
        for (Teams t : userTeams) {
            if (t.getProject().getId() == projectId) {
                task.setTeam(t);
            }
        }
        taskService.assignTaskToUser(task, user);
        if (prevUser != null) {
            int storyPoints = calculateStoryPoints(prevUser);
            prevUser.setAllocatedStoryPoints(storyPoints-task.getStoryPoints());
            userService.saveUser(prevUser);
        }
        emailService.sendTaskMail(user.getEmail(), task);

        return "redirect:/projects/projectTasks/" + projectId + "/taskDetails/" + taskId;

    }

    public List<Task> freeTasks(Principal principal, long userId) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<Task> task = taskService.findFreeTasks();
        List<Task> companyTask = new ArrayList<>();
        long company_id = user.getCompany().getId();
        for (Task t : task) {
            if (t.getProject() == null && (t.getCreatedUser() != null && t.getCreatedUser().getId() == user.getId())) {
                companyTask.add(t);
            } else if (companyService.isUserPresentInCompany(t.getCreatedUser(), company_id)) {
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
        User prevUser = selectedTask.getOwner();

        User selectedUser = userService.getUserById(userId);
        Teams userTeam = findTeam(selectedTask, selectedUser);
        if (userTeam != null) {
            selectedTask.setTeam(userTeam);
        }
        taskService.assignTaskToUser(selectedTask, selectedUser);
        selectedUser.setAllocatedStoryPoints(calculateStoryPoints(selectedUser));
        userService.saveUser(selectedUser);
        if (prevUser != null) {
            prevUser.setAllocatedStoryPoints(calculateStoryPoints(prevUser)-selectedTask.getStoryPoints());
            userService.saveUser(prevUser);
        }
        emailService.sendTaskMail(selectedUser.getEmail(), selectedTask);
        return "redirect:/assignment/" + userId;
    }

    public int calculateStoryPoints(User user) {
        if(user==null){
            return 0;
        }
        List<Task> task = user.getTasksOwned();
        int res = 0;
        for (Task t : task) {
            res += t.getStoryPoints();
        }
        return res;
    }

    public Teams findTeam(Task task, User user) {
        Project project = task.getProject();
        List<Project> userProjects = user.getProjects();
        for (Project p : userProjects) {
            if (p == project) {
                List<Teams> projectTeams = p.getTeams();
                List<Teams> userTeams = user.getTeams();
                for (Teams t : userTeams) {
                    if (projectTeams.contains(t)) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

    @GetMapping("unassign/{userId}/{taskId}")
    public String unassignTaskFromUser(@PathVariable Long userId, @PathVariable Long taskId) {
        Task selectedTask = taskService.getTaskById(taskId);
        User user = userService.getUserById(userId);
        taskService.unassignTask(selectedTask, user);
        user.setAllocatedStoryPoints(calculateStoryPoints(user)-selectedTask.getStoryPoints());
        userService.saveUser(user);
        return "redirect:/assignment/" + userId;
    }

    @GetMapping("/project/{projectId}/team/{teamId}/user/{userId}")
    public String assignTaskToTeamMember(@PathVariable long projectId, @PathVariable long teamId,
            @PathVariable long userId, Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<Task> freeTasks = freeTasks(principal, userId);
        Iterator<Task> iterator = freeTasks.iterator();
        while (iterator.hasNext()) {
            Task t = iterator.next();
            if (t.getProject() == null || t.getProject().getId() != projectId) {
                iterator.remove(); // Use iterator to remove the element
            }
        }

        model.addAttribute("selectedUser", userService.getUserById(userId));
        model.addAttribute("freeTasks", freeTasks);
        model.addAttribute("projectId", projectId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("userId", userId);

        return "forms/assignTeamTask";
    }

    @GetMapping("/project/{projectId}/team/{teamId}/user/{userId}/task/{taskId}")
    public String assignTaskToUser(@PathVariable long projectId, @PathVariable long teamId, @PathVariable long userId,
            @PathVariable long taskId) {
        Project project = projectService.getProjectById(projectId);
        Task task = taskService.getTaskById(taskId);
        if (task.getOwner() != null) {
            User user = task.getOwner();
            user.setAllocatedStoryPoints(calculateStoryPoints(user)-task.getStoryPoints());
            userService.saveUser(user);
        }
        User user = userService.getUserById(userId);
        Teams team = teamRepository.getById(teamId);
        task.setTeam(team);
        taskService.assignTaskToUser(task, user);
        user.setAllocatedStoryPoints(calculateStoryPoints(user));
        userService.saveUser(user);
        emailService.sendTaskMail(user.getEmail(), task);
        return "redirect:/assignment/project/{projectId}/team/{teamId}/user/{userId}";
    }

    @GetMapping("/project/unassign/project/{projectId}/team/{teamId}/user/{userId}/task/{taskId}")
    public String unAssignTaskToUser(@PathVariable long projectId, @PathVariable long teamId, @PathVariable long userId,
            @PathVariable long taskId) {
        Project project = projectService.getProjectById(projectId);
        Task task = taskService.getTaskById(taskId);
        task.setTeam(null);
        task.setAction("UnAssigned");
        User user = userService.getUserById(userId);
        taskService.unassignTask(task, user);
        user.setAllocatedStoryPoints(calculateStoryPoints(user)-task.getStoryPoints());
        userService.saveUser(user);
        return "redirect:/assignment/project/{projectId}/team/{teamId}/user/{userId}";
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
}
