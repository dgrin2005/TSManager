package ru.trustsoft.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Bases {
    private int id;
    private String basename;
    private String description;
    private int contragentid;
    private Contragents contragentsByContragentid;
    private Collection<Basesofusers> basesofusersById;

    public Bases() {
    }

    public Bases(String basename, String description, Contragents contragentsByContragentid) {
        this.basename = basename;
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bases bases = (Bases) o;
        return id == bases.id &&
                Objects.equals(basename, bases.basename) &&
                Objects.equals(description, bases.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, basename, description);
    }

    @Basic
    @Column(name = "contragentid", nullable = false, length = -1)
    public int getContragentid() {
        return contragentid;
    }

    public void setContragentid(int contragentid) {
        this.contragentid = contragentid;
    }

    @ManyToOne
    @JoinColumn(name = "contragentid", referencedColumnName = "id", nullable = false)
    public Contragents getContragentsByContragentid() {
        return contragentsByContragentid;
    }

    public void setContragentsByContragentid(Contragents contragentsByContragentid) {
        this.contragentsByContragentid = contragentsByContragentid;
    }

    @OneToMany(mappedBy = "basesByBaseid")
    public Collection<Basesofusers> getBasesofusersById() {
        return basesofusersById;
    }

    public void setBasesofusersById(Collection<Basesofusers> basesofusersById) {
        this.basesofusersById = basesofusersById;
    }
}
