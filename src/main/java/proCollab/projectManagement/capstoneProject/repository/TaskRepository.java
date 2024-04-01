package proCollab.projectManagement.capstoneProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.User;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwnerOrderByDateDesc(User user);

    long countByIsCompleted(boolean isCompleted);

    @Transactional
    void deleteAllByProject(Project project);
}