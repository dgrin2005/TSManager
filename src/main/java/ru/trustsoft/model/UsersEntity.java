package ru.trustsoft.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users", schema = "tsmngr")
public class UsersEntity {
    private int userid;
    private String username;
    private String userpassword;
    private String description;
    private boolean islocked;

    public UsersEntity() {
    }

    public UsersEntity(String username, String userpassword, String description, boolean islocked) {
        this.username = username;
        this.userpassword = userpassword;
        this.description = description;
        this.islocked = islocked;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "userid", nullable = false)
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "username", nullable = false, length = -1)
    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    @Basic
    @Column(name = "userpassword", nullable = false, length = -1)
    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userPassword) {
        this.userpassword = userPassword;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "islocked", nullable = false)
    public boolean getIslocked() {
        return islocked;
    }

    public void setIslocked(boolean islocked) {
        this.islocked = islocked;
    }

    private Set<BasesofusersEntity> basesOfUsers = new HashSet<>();

/*
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<BasesofusersEntity> getBasesOfUsers() {
        return this.basesOfUsers;
    }

    public void setBasesofusers(Set<BasesofusersEntity> basesOfUsers) {
        this.basesOfUsers = basesOfUsers;
    }

    public void addBaseOfUser(BasesofusersEntity baseOfUser) {
        baseOfUser.setUser(this);
        getBasesOfUsers().add(baseOfUser);
    }

    public void removeBaseOfUser(BasesofusersEntity baseOfUser) {
        getBasesOfUsers().remove(baseOfUser);
    }
*/

    private UsersofcontragentsEntity userofcontragent;

    @OneToOne
    @JoinColumn(name = "userid", referencedColumnName = "contragentid")
    public UsersofcontragentsEntity getUserofcontragent() {
        return this.userofcontragent;
    }

    public void setUserofcontragent(UsersofcontragentsEntity userofcontragent) {
        this.userofcontragent = userofcontragent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return userid == that.userid &&
                islocked == that.islocked &&
                Objects.equals(username, that.username) &&
                Objects.equals(userpassword, that.userpassword) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userid, username, userpassword, description, islocked);
    }

    @Override
    public String toString() {
        return "UsersEntity{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", description='" + description + '\'' +
                ", islocked=" + islocked +
                '}';
    }
}
