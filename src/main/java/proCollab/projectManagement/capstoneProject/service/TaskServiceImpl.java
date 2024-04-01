package proCollab.projectManagement.capstoneProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskHistory;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.TaskHistoryRepository;
import proCollab.projectManagement.capstoneProject.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private TaskHistoryService taskHistoryService;
    private TaskHistoryRepository taskHistoryRepository;
    private UserService userService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskHistoryService taskHistoryService,
            TaskHistoryRepository taskHistoryRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskHistoryService = taskHistoryService;
        this.userService = userService;
    }

    @Override
    public void createTask(Task task) {
        task.setAction("Unassigned");
        taskRepository.save(task);
        LocalDateTime date = LocalDateTime.now();
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setAction("Not Assigned");
        taskHistory.setTimestamp(date);
        taskHistory.setUser(task.getOwner());
        updateUserStoryPoints(task.getOwner());
        taskHistoryRepository.save(taskHistory);
    }

    @Override
    public void updateTask(Long id, Task updatedTask) {
        Task task = taskRepository.getOne(id);
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setDate(updatedTask.getDate());
        task.setStoryPoints(updatedTask.getStoryPoints());
        taskRepository.save(task);
        LocalDateTime date = LocalDateTime.now();
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setAction(task.getOwner() != null ? "Assigned" : "Not Assigned");
        taskHistory.setTimestamp(date);
        taskHistory.setUser(task.getOwner());
        taskHistoryRepository.save(taskHistory);
        updateUserStoryPoints(task.getOwner());
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.getById(id);
        taskHistoryRepository.deleteAllByTask(task);
        taskRepository.deleteById(id);
        updateUserStoryPoints(task.getOwner());
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findByOwnerOrderByDateDesc(User user) {
        return taskRepository.findByOwnerOrderByDateDesc(user);
    }

    @Override
    public void setTaskCompleted(Long id) {
        Task task = taskRepository.getOne(id);
        task.setCompleted(true);
        taskRepository.save(task);
    }

    @Override
    public void setTaskNotCompleted(Long id) {
        Task task = taskRepository.getOne(id);
        task.setCompleted(false);
        taskRepository.save(task);
    }

    @Override
    public List<Task> findFreeTasks() {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getOwner() == null && !task.isCompleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void assignTaskToUser(Task task, User user) {
        userService.saveUser(user);
        User prevUser = task.getOwner();
        if (prevUser != null) {
            updateUserStoryPoints(prevUser);
        }
        task.setOwner(user);
        task.setAction("Assigned");
        taskRepository.save(task);
        LocalDateTime date = LocalDateTime.now();
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setAction("Assigned");
        taskHistory.setTimestamp(date);
        taskHistory.setUser(task.getOwner());
        taskHistoryRepository.save(taskHistory);
        updateUserStoryPoints(user);
    }

    @Override
    public void unassignTask(Task task, User user) {
        task.setOwner(null);
        task.setAction("Unassigned");
        task.setTeam(null);
        taskRepository.save(task);
        LocalDateTime date = LocalDateTime.now();
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setAction("Unassigned From");
        taskHistory.setTimestamp(date);
        taskHistory.setUser(task.getOwner());
        taskHistoryRepository.save(taskHistory);
        updateUserStoryPoints(user);
    }

    public long countTasks() {
        return taskRepository.count();
    }

    @Override
    public long countCompletedTasks() {
        return taskRepository.countByIsCompleted(true);
    }

    @Override
    @Transactional
    public void deleteAllTaskFromProject(Project project) {
        taskRepository.deleteAllByProject(project);
    }

    private void updateUserStoryPoints(User user) {
        if (user != null) {
            int storyPoints = calculateStoryPoints(user.getTasksOwned());
            user.setAllocatedStoryPoints(storyPoints);
            userService.saveUser(user);
        }
    }

    private int calculateStoryPoints(List<Task> tasks) {
        return tasks.stream().mapToInt(Task::getStoryPoints).sum();
    }
}