package ru.trustsoft.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bases", schema = "tsmngr")
public class BasesEntity {
    private int baseid;
    private String basename;
    private String description;

    // наличие пустого конструктора обязательно при работе с JPA
    public BasesEntity() {
    }

    public BasesEntity(String basename, String description) {
        this.basename = basename;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "baseid", nullable = false)
    public int getBaseid() {
        return baseid;
    }

    public void setBaseid(int baseid) {
        this.baseid = baseid;
    }

    @Basic
    @Column(name = "basename", nullable = false, length = -1)
    public String getBasename() {
        return basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private BasesofusersEntity baseofuser;

    @OneToOne
    @JoinColumn(name = "baseid", referencedColumnName = "userid")
    public BasesofusersEntity getBaseofuser() {
        return this.baseofuser;
    }

    public void setBaseofuser(BasesofusersEntity baseofuser) {
        this.baseofuser = baseofuser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasesEntity that = (BasesEntity) o;
        return baseid == that.baseid &&
                Objects.equals(basename, that.basename) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(baseid, basename, description);
    }

    @Override
    public String toString() {
        return "BasesEntity{" +
                "baseid=" + baseid +
                ", basename='" + basename + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
