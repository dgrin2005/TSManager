package ru.trustsoft;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Contragents {
    private int id;
    private String contragentname;
    private String description;
    private Collection<Bases> basesById;
    private Collection<Users> usersById;

    @Id
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
        Contragents that = (Contragents) o;
        return id == that.id &&
                Objects.equals(contragentname, that.contragentname) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, contragentname, description);
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