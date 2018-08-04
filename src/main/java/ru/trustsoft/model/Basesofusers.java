package ru.trustsoft.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Basesofusers {
    private int id;
    private int userid;
    private Users usersByUserid;
    private int baseid;
    private Bases basesByBaseid;

    public Basesofusers() {
    }

    public Basesofusers(Users usersByUserid, Bases basesByBaseid) {
        this.usersByUserid = usersByUserid;
        this.basesByBaseid = basesByBaseid;
        userid = usersByUserid.getId();
        baseid = basesByBaseid.getId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basesofusers that = (Basesofusers) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Basic
    @Column(name = "userid", nullable = false, length = -1)
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "baseid", nullable = false, length = -1)
    public int getBaseid() {
        return baseid;
    }

    public void setBaseid(int baseid) {
        this.baseid = baseid;
    }

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", nullable = false)
    public Users getUsersByUserid() {
        return usersByUserid;
    }

    public void setUsersByUserid(Users usersByUserid) {
        this.usersByUserid = usersByUserid;
    }

    @ManyToOne
    @JoinColumn(name = "baseid", referencedColumnName = "id", nullable = false)
    public Bases getBasesByBaseid() {
        return basesByBaseid;
    }

    public void setBasesByBaseid(Bases basesByBaseid) {
        this.basesByBaseid = basesByBaseid;
    }
}
