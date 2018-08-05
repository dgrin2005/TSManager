package ru.trustsoft.model;

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
    private boolean adm;
    private int contragentid;
    private Contragents contragentsByContragentid;
    private Collection<Basesofusers> basesofusersById;

    public Users() {
    }

    public Users(String username, String userpassword, String description, boolean locked, boolean adm, Contragents contragentsByContragentid) {
        this.username = username;
        this.userpassword = userpassword;
        this.description = description;
        this.locked = locked;
        this.adm = adm;
        this.contragentsByContragentid = contragentsByContragentid;
        this.contragentid = contragentsByContragentid.getId();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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
    @Column(name = "adm", nullable = false)
    public boolean isAdm() {
        return adm;
    }

    public void setAdm(boolean adm) {
        this.adm = adm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return id == users.id &&
                locked == users.locked &&
                adm == users.adm &&
                Objects.equals(username, users.username) &&
                Objects.equals(userpassword, users.userpassword) &&
                Objects.equals(description, users.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, userpassword, description, locked, adm);
    }

    @Basic
    @Column(name = "contragentid", nullable = false, length = -1)
    public int getContragentid() {
        return contragentid;
    }

    public void setContragentid(int contragentid) {
        this.contragentid = contragentid;
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
