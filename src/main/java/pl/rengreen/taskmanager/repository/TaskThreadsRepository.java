package pl.rengreen.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.rengreen.taskmanager.model.Task;
import pl.rengreen.taskmanager.model.TaskThreads;
import java.util.List;


@Repository
public interface TaskThreadsRepository extends JpaRepository<TaskThreads, Long> {
    List<TaskThreads> findByTask(Task task);
}
