package pl.rengreen.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.Teams;
import pl.rengreen.taskmanager.model.User;

@Repository
public interface TeamRepository extends JpaRepository<Teams, Long> {

    public List<Teams> findAllByProjectId(Long id);

    @Transactional
    void deleteAllByProject(Project project);
}
