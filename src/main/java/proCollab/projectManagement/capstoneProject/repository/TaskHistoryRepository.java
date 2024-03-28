package proCollab.projectManagement.capstoneProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskHistory;
import proCollab.projectManagement.capstoneProject.model.User;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskId(Long taskId);

    List<TaskHistory> findByUser(User user);

    @Transactional
    void deleteAllByTask(Task byId);

}
