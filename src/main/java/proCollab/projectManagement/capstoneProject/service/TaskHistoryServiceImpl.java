package proCollab.projectManagement.capstoneProject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskHistory;
import proCollab.projectManagement.capstoneProject.repository.TaskHistoryRepository;

@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    @Autowired
    public TaskHistoryServiceImpl(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    @Override
    public TaskHistory save(TaskHistory taskHistory) {
        return taskHistoryRepository.save(taskHistory);
    }

    @Override
    public List<TaskHistory> findByTaskId(Long taskId) {
        return taskHistoryRepository.findByTaskId(taskId);
    }

    @Override
    public void deleteAllTaskHistoryByTask(Task task) {
        taskHistoryRepository.deleteAllByTask(task);
    }

}
