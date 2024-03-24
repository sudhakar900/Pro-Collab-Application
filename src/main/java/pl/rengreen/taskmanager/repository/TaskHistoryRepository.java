package pl.rengreen.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import pl.rengreen.taskmanager.model.Task;
import pl.rengreen.taskmanager.model.TaskHistory;
import pl.rengreen.taskmanager.model.User;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskId(Long taskId);

    List<TaskHistory> findByUser(User user);

    @Transactional
    void deleteAllByTask(Task byId);

}
