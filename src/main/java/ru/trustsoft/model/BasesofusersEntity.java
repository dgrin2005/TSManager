package ru.trustsoft.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "basesofusers", schema = "tsmngr")
public class BasesofusersEntity {
    private int id;

    public BasesofusersEntity() {
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

    private UsersEntity user;

/*
    @ManyToOne
    @JoinColumn(name = "userid")
    public UsersEntity getUser() {
        return this.user;
    }

    public void setUser(UsersEntity user) {
        this.user = user;
    }
*/

    private BasesEntity base;

/*
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "baseOfUser")
    public BasesEntity getBase() {
        return base;
    }

    public void setBase(BasesEntity base) {
        base.setBaseOfUser(this);
        this.base = base;
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasesofusersEntity that = (BasesofusersEntity) o;
        return id == that.id &&
                user == that.user &&
                base == that.base;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, user, base);
    }
}
