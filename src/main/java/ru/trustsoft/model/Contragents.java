package ru.trustsoft.model;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Component
public class Contragents {
    private int id;
    private String contragentname;
    private String description;
    private String inn;
    private Collection<Bases> basesById;
    private Collection<Users> usersById;

    public Contragents() {
    }

    public Contragents(String contragentname, String description, String inn) {
        this.contragentname = contragentname;
        this.description = description;
        this.inn = inn;
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
    @Column(name = "contragentname", nullable = false, length = -1)
    public String getContragentname() {
        return contragentname;
    }

    public void setContragentname(String contragentname) {
        this.contragentname = contragentname;
    }

    @Basic
    @Column(name = "description", length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "inn", nullable = false, length = -1)
    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contragents that = (Contragents) o;
        return id == that.id &&
                Objects.equals(contragentname, that.contragentname) &&
                Objects.equals(inn, that.inn) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, contragentname, description, inn);
    }

    @OneToMany(mappedBy = "contragentsByContragentid")
    public Collection<Bases> getBasesById() {
        return basesById;
    }

    public void setBasesById(Collection<Bases> basesById) {
        this.basesById = basesById;
    }

    @OneToMany(mappedBy = "contragentsByContragentid")
    public Collection<Users> getUsersById() {
        return usersById;
    }

    public void setUsersById(Collection<Users> usersById) {
        this.usersById = usersById;
    }
}
