package ru.trustsoft.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "usersofcontragents", schema = "tsmngr")
public class UsersofcontragentsEntity {
    private int id;
    private boolean isadmin;

    public UsersofcontragentsEntity() {
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
    @Column(name = "isadmin", nullable = false)
    public boolean getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }

    private ContragentsEntity contragent;

/*
    @ManyToOne
    @JoinColumn(name = "contragentid")
    public ContragentsEntity getContragent() {
        return this.contragent;
    }

    public void setContragent(ContragentsEntity contragent) {
        this.contragent = contragent;
    }

*/
    private UsersEntity user;

/*
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "userOfContragent")
    public UsersEntity getUser() {
        return user;
    }

    public void setUser(UsersEntity user) {
        user.setUserOfContragent(this);
        this.user = user;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersofcontragentsEntity that = (UsersofcontragentsEntity) o;
        return id == that.id &&
                contragent == that.contragent &&
                user == that.user &&
                isadmin == that.isadmin;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, contragent, user, isadmin);
    }
}
