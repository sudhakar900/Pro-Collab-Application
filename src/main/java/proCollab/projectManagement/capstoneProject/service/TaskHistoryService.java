package proCollab.projectManagement.capstoneProject.service;

import java.util.List;

import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskHistory;

public interface TaskHistoryService {
    TaskHistory save(TaskHistory taskHistory);

    List<TaskHistory> findByTaskId(Long taskId);

    void deleteAllTaskHistoryByTask(Task task);
}
