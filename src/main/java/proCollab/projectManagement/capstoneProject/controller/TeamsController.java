package proCollab.projectManagement.capstoneProject.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.Teams;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.TaskRepository;
import proCollab.projectManagement.capstoneProject.repository.TeamRepository;
import proCollab.projectManagement.capstoneProject.service.ProjectService;
import proCollab.projectManagement.capstoneProject.service.TaskService;
import proCollab.projectManagement.capstoneProject.service.TeamService;
import proCollab.projectManagement.capstoneProject.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/project")
public class TeamsController {
    private final ProjectService projectService;
    private final UserService userService;
    private final TeamService teamService;
    private final TaskService taskService;

    @Autowired
    private TeamRepository repo;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    public TeamsController(ProjectService projectService, UserService userService, TeamService teamService,
            TaskService taskService) {
        this.projectService = projectService;
        this.userService = userService;
        this.teamService = teamService;
        this.taskService = taskService;
    }

    @GetMapping("/createTeam/{projectId}")
    public String createTeam(@PathVariable("projectId") long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        return "forms/createTeam";
    }

    @PostMapping("/{id}/createTeam")
    public String createTeam(@PathVariable Long id, @RequestParam("name") String name) {
        Project project = projectService.getProjectById(id);
        Teams team = new Teams();
        team.setName(name);
        team.setProject(project);
        teamService.createTeam(team);
        return "redirect:/project/" + id + "/teams";
    }

    @GetMapping("/{id}/teams")
    public String getTeams(@PathVariable("id") Long id, Model model) {
        List<Teams> teams = repo.findAllByProjectId(id);
        model.addAttribute("teams", teams);
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        return "forms/teams";
    }

    @GetMapping("/teams/{projectId}/addUser/{teamId}")
    public String addUserToTeam(@PathVariable("projectId") long projectId, @PathVariable("teamId") long teamId,
            Model model) {
        List<User> projectUsers = projectService.getAllProjectEmployees(projectId);
        List<User> teamUsers = teamService.getTeamById(teamId).get().getUsers();
        List<Teams> allTeams = repo.findAllByProjectId(projectId);

        Set<User> allTeamUsers = new HashSet<>();
        for (Teams team : allTeams) {
            allTeamUsers.addAll(team.getUsers());
        }
        projectUsers.removeAll(allTeamUsers);
        for (User user : teamUsers) {
            projectUsers.remove(user);
        }
        Project project = projectService.getProjectById(projectId);
        User user = project.getCreator();
        projectUsers.remove(user);
        model.addAttribute("availableUsers", projectUsers);
        model.addAttribute("projectId", projectId);
        model.addAttribute("teamId", teamId);
        model.addAttribute("teamUsers", teamUsers);
        return "views/addTeamMembers";
    }

    @GetMapping("/teams/{projectId}/teamTask/{teamId}")
    public String teamTask(@PathVariable("projectId") long projectId, @PathVariable("teamId") long teamId,
            Model model) {
        Project project = projectService.getProjectById(projectId);
        List<Task> projectTasks = project.getTasks();
        List<Task> teamTask = new ArrayList<>();
        Teams team = teamService.getTeamById(teamId).get();
        for (Task t : projectTasks) {
            if (t.getTeam() != null && t.getTeam() == team) {
                teamTask.add(t);
            }
        }
        model.addAttribute("project", project);
        model.addAttribute("Team", team);
        model.addAttribute("tasks", teamTask);
        model.addAttribute("teamId", teamId);
        return "views/teamTasks";
    }

    @GetMapping("/teams/{projectId}/deleteTeam/{teamId}")
    public String deleteTeam(@PathVariable("projectId") long projectId, @PathVariable("teamId") long teamId) {
        Teams team = teamService.getTeamById(teamId).get();
        List<User> users = team.getUsers();
        for (User u : users) {
            List<Teams> userTeams = u.getTeams();
            userTeams.remove(team);
            u.setTeams(userTeams);
            userService.saveUser(u);
            List<Task> projectTask = projectService.getProjectById(projectId).getTasks();
            for (Task t : projectTask) {
                if (t.getTeam() != null && t.getTeam().getId() == teamId) {
                    t.setTeam(null);
                    taskRepository.save(t);
                }
            }
        }
        repo.deleteById(teamId);
        return "redirect:/project/" + projectId + "/teams";
    }

    @PostMapping("/assignment/delete/project/{projectId}/team/{teamId}/users")
    public String deleteFromTeam(@PathVariable long projectId, @PathVariable long teamId,
            @RequestBody List<Long> userIds) {
        Project project = projectService.getProjectById(projectId);
        Teams team = teamService.getTeamById(teamId).orElseThrow(() -> new IllegalArgumentException("Team not found"));
        List<User> users = team.getUsers();
        List<Task> projectTask = project.getTasks();
        for (Long userId : userIds) {
            User user = userService.getUserById(userId);
            List<Task> taskList = user.getTasksOwned();
            for (Task t : taskList) {
                if (projectTask.contains(t)) {
                    t.setOwner(null);
                    taskService.updateTask(t.getId(), t);
                }
            }
            users.remove(user);
        }
        team.setUsers(users);
        teamService.createTeam(team);
        return "redirect:/project/teams/" + projectId + "/addUser/" + teamId;
    }

    @PostMapping("/{projectId}/teams/{teamId}/addUsers")
    public ResponseEntity<Void> addUsersToTeam(@PathVariable Long projectId, @PathVariable Long teamId,
            @RequestBody List<Long> userIds) {
        Teams team = teamService.getTeamById(teamId).get();
        for (Long userId : userIds) {
            User user = userService.getUserById(userId);
            team.getUsers().add(user);
            teamService.saveTeam(team);
        }
        return ResponseEntity.ok().build();
    }

    // Add other endpoints as needed
}
