package ru.trustsoft;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Basesofusers {
    private int id;
    private Users usersByUserid;
    private Bases basesByBaseid;

    @Id
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
