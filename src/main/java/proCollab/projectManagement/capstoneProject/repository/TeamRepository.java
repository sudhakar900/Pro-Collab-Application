package proCollab.projectManagement.capstoneProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Teams;
import proCollab.projectManagement.capstoneProject.model.User;

@Repository
public interface TeamRepository extends JpaRepository<Teams, Long> {

    public List<Teams> findAllByProjectId(Long id);

    @Transactional
    void deleteAllByProject(Project project);
}
