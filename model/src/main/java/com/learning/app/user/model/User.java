package com.learning.app.user.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User implements Serializable
{
    private static final long serialVersionUID = -8323752660030036842L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 40)
    @Column
    private String name;

    @NotNull
    @Size(max = 40)
    @Column(unique = true)
    private String email;

    @NotNull
    @Column
    private String password;

    @Column(name = "created_at", columnDefinition = "DATE", updatable = false)
    private LocalDate createdAt;

    public enum Role
    {
        STANDARD, ADMIN
    }

    //User roles are stored in a separated table "USER_ROLE" with the columns "user_id" and "role"
    @CollectionTable(
            name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"})
    )
    //Fetch type EAGER loads user roles as soon as the user is loaded
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    //The roles are stored as String values in the database
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    public User()
    {
        this.createdAt = LocalDate.now();
        this.roles = Arrays.asList(Role.STANDARD);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public LocalDate getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt)
    {
        this.createdAt = createdAt;
    }

    public List<Role> getRoles()
    {
        return new ArrayList<Role>(roles);
    }

    public void setRoles(List<Role> roles)
    {
        this.roles = roles;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User)o;

        if (id != null ? !id.equals(user.id) : user.id != null)
            return false;
        if (name != null ? !name.equals(user.name) : user.name != null)
            return false;
        return email != null ? email.equals(user.email) : user.email == null;

    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
