/**
 * TerminalServerManager
 *    Bases.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.model;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Component
public class Bases {
    private int id;
    private String basename;
    private String description;
    private int contragentid;
    private String path;
    private String ipaddress;
    private Contragents contragentsByContragentid;
    private Collection<Basesofusers> basesofusersById;

    public Bases() {
    }

    public Bases(String basename, String description, Contragents contragentsByContragentid, String path, String ipaddress) {
        this.basename = basename;
        this.description = description;
        this.contragentsByContragentid = contragentsByContragentid;
        this.contragentid = contragentsByContragentid.getId();
        this.path = path;
        this.ipaddress = ipaddress;
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
    @Column(name = "description", length = -1)
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
                Objects.equals(description, bases.description) &&
                Objects.equals(contragentid, bases.contragentid) &&
                Objects.equals(path, bases.path) &&
                Objects.equals(ipaddress, bases.ipaddress);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, basename, description, contragentid, path, ipaddress);
    }

    @Basic
    @Column(name = "contragentid", nullable = false, length = -1, insertable=false, updatable=false)
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

    @Basic
    @Column(name = "path", length = -1)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Basic
    @Column(name = "ipaddress", length = -1)
    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    @OneToMany(mappedBy = "basesByBaseid")
    public Collection<Basesofusers> getBasesofusersById() {
        return basesofusersById;
    }

    public void setBasesofusersById(Collection<Basesofusers> basesofusersById) {
        this.basesofusersById = basesofusersById;
    }

    @Override
    public String toString() {
        return "Bases{" +
                "id=" + id +
                ", basename='" + basename + '\'' +
                ", description='" + description + '\'' +
                ", contragentid=" + contragentid +
                ", path='" + path + '\'' +
                ", ipaddress='" + ipaddress + '\'' +
                ", contragentsByContragentid=" + contragentsByContragentid +
                ", basesofusersById=" + basesofusersById +
                '}';
    }
}
