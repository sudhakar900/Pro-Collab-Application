package proCollab.projectManagement.capstoneProject.service;

import org.springframework.beans.PropertyValues;

import proCollab.projectManagement.capstoneProject.model.Role;

import java.util.List;

public interface RoleService {
    Role createRole(Role role);

    List<Role> findAll();

    long countRole();
}
