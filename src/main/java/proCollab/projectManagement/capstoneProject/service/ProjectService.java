package proCollab.projectManagement.capstoneProject.service;

import java.util.List;

import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.User;

public interface ProjectService {

    Project createProject(Project project);

    Project updateProject(Project project);

    void deleteProject(Long projectId);

    Project getProjectById(Long projectId);

    List<Project> getAllProjects();

    void addEmployeeToProject(Long projectId, User user);

    void removeEmployeeFromProject(Long projectId, User user);

    List<User> getAllProjectEmployees(Long projectId);

    List<Project> findByCompanyId(Long companyId);

    boolean isUserPresentInProject(Project project, User user);

    long countCompletedProjects(User user);

}
