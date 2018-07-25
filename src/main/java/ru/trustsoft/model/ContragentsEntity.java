package ru.trustsoft.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "contragents", schema = "tsmngr")
public class ContragentsEntity {
    private int contragentid;
    private String contragentname;
    private String description;

    public ContragentsEntity() {
    }

    public ContragentsEntity(String contragentname, String description) {
        this.contragentname = contragentname;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "contragentid", nullable = false)
    public int getContragentid() {
        return contragentid;
    }

    public void setContragentid(int contragentId) {
        this.contragentid = contragentId;
    }

    @Basic
    @Column(name = "contragentname", nullable = false, length = -1)
    public String getContragentname() {
        return contragentname;
    }

    public void setContragentname(String contragentname) {
        this.contragentname = contragentname;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Set<UsersofcontragentsEntity> usersofcontragents = new HashSet<>();

/*
    @OneToMany(mappedBy = "contragent", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<UsersofcontragentsEntity> getUsersOfContragents() {
        return this.usersOfContragents;
    }

    public void setUsersOfContragents(Set<UsersofcontragentsEntity> usersOfContragents) {
        this.usersOfContragents = usersOfContragents;
    }

    public void addUserOfContragent(UsersofcontragentsEntity userOfContragent) {
        userOfContragent.setContragent(this);
        getUsersOfContragents().add(userOfContragent);
    }

    public void removeUserOfContragent(UsersofcontragentsEntity userOfContragent) {
        getUsersOfContragents().remove(userOfContragent);
    }
*/


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContragentsEntity that = (ContragentsEntity) o;
        return contragentid == that.contragentid &&
                Objects.equals(contragentname, that.contragentname) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(contragentid, contragentname, description);
    }

    @Override
    public String toString() {
        return "ContragentsEntity{" +
                "contragentid=" + contragentid +
                ", contragentname='" + contragentname + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
