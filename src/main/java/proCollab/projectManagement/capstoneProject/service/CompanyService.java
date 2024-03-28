package proCollab.projectManagement.capstoneProject.service;

import java.util.List;

import proCollab.projectManagement.capstoneProject.model.Company;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.User;

public interface CompanyService {

    void addCompany(Company company);

    void removeCompany(Company company);

    void updateCompany(long id, Company company);

    Company getCompany(long id);

    List<User> getCompanyUsers(long id);

    List<Company> getAllCompanies();

    boolean isUserPresentInCompany(User user, long company_id);

    List<Task> getAllTaskByCompany(long id);
}
