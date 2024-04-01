package proCollab.projectManagement.capstoneProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskThreads;

import java.util.List;

@Repository
public interface TaskThreadsRepository extends JpaRepository<TaskThreads, Long> {
    List<TaskThreads> findByTask(Task task);
}
