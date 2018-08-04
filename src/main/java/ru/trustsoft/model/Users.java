package ru.trustsoft;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Users {
    private int id;
    private String username;
    private String userpassword;
    private String description;
    private boolean locked;
    private boolean admin;
    private Byte islocked;
    private Collection<Basesofusers> basesofusersById;
    private Contragents contragentsByContragentid;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username", nullable = false, length = -1)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "userpassword", nullable = false, length = -1)
    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
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
    @Column(name = "locked", nullable = false)
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Basic
    @Column(name = "admin", nullable = false)
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Basic
    @Column(name = "islocked", nullable = true)
    public Byte getIslocked() {
        return islocked;
    }

    public void setIslocked(Byte islocked) {
        this.islocked = islocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return id == users.id &&
                locked == users.locked &&
                admin == users.admin &&
                Objects.equals(username, users.username) &&
                Objects.equals(userpassword, users.userpassword) &&
                Objects.equals(description, users.description) &&
                Objects.equals(islocked, users.islocked);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, userpassword, description, locked, admin, islocked);
    }

    @OneToMany(mappedBy = "usersByUserid")
    public Collection<Basesofusers> getBasesofusersById() {
        return basesofusersById;
    }

    public void setBasesofusersById(Collection<Basesofusers> basesofusersById) {
        this.basesofusersById = basesofusersById;
    }

    @ManyToOne
    @JoinColumn(name = "contragentid", referencedColumnName = "id", nullable = false)
    public Contragents getContragentsByContragentid() {
        return contragentsByContragentid;
    }

    public void setContragentsByContragentid(Contragents contragentsByContragentid) {
        this.contragentsByContragentid = contragentsByContragentid;
    }
}
