package proCollab.projectManagement.capstoneProject.model;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Email(message = "{user.email.not.valid}")
    @NotEmpty(message = "{user.email.not.empty}")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "{user.name.not.empty}")
    private String name;

    private long employeeId;

    private long allocatedStoryPoints;

    @NotEmpty(message = "{user.password.not.empty}")
    @Length(min = 5, message = "{user.password.length}")
    private String password;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'images/user.png'")
    private String photo;

    private String resetToken;

    private String verificationToken;

    private LocalDateTime tokenExpiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Task> tasksOwned;

    @OneToMany(mappedBy = "note_owner", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Note> notesOwned;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private List<Teams> teams = new ArrayList<>();
    public List<Teams> getTeams() {
        return teams;
    }

    public void setTeams(List<Teams> teams) {
        this.teams = teams;
    }

    public List<Task> getTasksCompleted() {
        return tasksOwned.stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksInProgress() {
        return tasksOwned.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }

    public boolean isAdmin() {
        String roleName = "ADMIN";
        return roles.stream().map(Role::getRole).anyMatch(roleName::equals);
    }

    public boolean isUser() {
        String roleName = "USER";
        return roles.stream().map(Role::getRole).anyMatch(roleName::equals);
    }

    public boolean isSuperAdmin() {
        String roleName = "SUPERADMIN";
        return roles.stream().map(Role::getRole).anyMatch(roleName::equals);
    }

    public User() {
    }

    public User(@Email @NotEmpty String email,
            @NotEmpty String name,
            @NotEmpty @Length(min = 5) String password,
            String photo, Company Company) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.photo = photo;
        this.company = company;
    }

    public User(@Email @NotEmpty String email,
            @NotEmpty String name,
            @NotEmpty @Length(min = 5) String password,
            String photo,
            List<Task> tasksOwned,
            List<Role> roles) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.photo = photo;
        this.tasksOwned = tasksOwned;
        this.roles = roles;
    }

    public User(Long id,
            @Email(message = "{user.email.not.valid}") @NotEmpty(message = "{user.email.not.empty}") String email,
            @NotEmpty(message = "{user.name.not.empty}") String name,
            @NotEmpty(message = "{user.password.not.empty}") @Length(min = 5, message = "{user.password.length}") String password,
            String photo, String resetToken, String verificationToken, LocalDateTime tokenExpiryDate, Company company,
            List<Project> projects, List<Task> tasksOwned, List<Note> notesOwned, List<Role> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.photo = photo;
        this.resetToken = resetToken;
        this.verificationToken = verificationToken;
        this.tokenExpiryDate = tokenExpiryDate;
        this.company = company;
        this.projects = projects;
        this.tasksOwned = tasksOwned;
        this.notesOwned = notesOwned;
        this.roles = roles;
    }

    public long getAllocatedStoryPoints() {
        return allocatedStoryPoints;
    }

    public void setAllocatedStoryPoints(long allocatedStoryPoints) {
        this.allocatedStoryPoints = allocatedStoryPoints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Task> getTasksOwned() {
        return tasksOwned;
    }

    public void setTasksOwned(List<Task> tasksOwned) {
        this.tasksOwned = tasksOwned;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (photo == null) {
            if (other.photo != null)
                return false;
        } else if (!photo.equals(other.photo))
            return false;
        if (resetToken == null) {
            if (other.resetToken != null)
                return false;
        } else if (!resetToken.equals(other.resetToken))
            return false;
        if (verificationToken == null) {
            if (other.verificationToken != null)
                return false;
        } else if (!verificationToken.equals(other.verificationToken))
            return false;
        if (tokenExpiryDate == null) {
            if (other.tokenExpiryDate != null)
                return false;
        } else if (!tokenExpiryDate.equals(other.tokenExpiryDate))
            return false;
        if (tasksOwned == null) {
            if (other.tasksOwned != null)
                return false;
        } else if (!tasksOwned.equals(other.tasksOwned))
            return false;
        if (notesOwned == null) {
            if (other.notesOwned != null)
                return false;
        } else if (!notesOwned.equals(other.notesOwned))
            return false;
        if (roles == null) {
            if (other.roles != null)
                return false;
        } else if (!roles.equals(other.roles))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((photo == null) ? 0 : photo.hashCode());
        result = prime * result + ((resetToken == null) ? 0 : resetToken.hashCode());
        result = prime * result + ((verificationToken == null) ? 0 : verificationToken.hashCode());
        result = prime * result + ((tokenExpiryDate == null) ? 0 : tokenExpiryDate.hashCode());
        result = prime * result + ((tasksOwned == null) ? 0 : tasksOwned.hashCode());
        result = prime * result + ((notesOwned == null) ? 0 : notesOwned.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        return result;
    }

    public List<Note> getNotesOwned() {
        return notesOwned;
    }

    public void setNotesOwned(List<Note> notesOwned) {
        this.notesOwned = notesOwned;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getTokenExpiryDate() {
        return tokenExpiryDate;
    }

    public void setTokenExpiryDate(LocalDateTime tokenExpiryDate) {
        this.tokenExpiryDate = tokenExpiryDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

}
