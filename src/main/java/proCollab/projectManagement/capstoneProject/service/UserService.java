package proCollab.projectManagement.capstoneProject.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import proCollab.projectManagement.capstoneProject.model.Company;
import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.User;

public interface UserService {
    User createUser(User user);

    User saveUser(User user);

    User changeRoleToAdmin(User user);

    User changeRoleToSuperAdmin(User user);

    User changeRoleToProAdmin(User user);

    User changeRoleToUser(User user);

    List<User> findAll();

    User getUserByEmail(String email);

    boolean isUserEmailPresent(String email);

    User getUserById(Long userId);

    void deleteUser(Long id);

    long countUsers();

    void updateProfilePic(User user, String picUrl);

    void updatePassword(User user, String password);

    public User getUserByVerificationToken(String token);

    public boolean isTokenExpired(User user);

    public List<Project> getUserProjects(User user);

    public List<User> getUsersByIds(List<Long> userIds);

    List<User> createUsersFromExcel(MultipartFile file, Company company) throws IOException;

}
