/**
 * TerminalServerManager
 *    ReconActParameters.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.model;


import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
public class ReconActParameters {

    @Id
    private int id;

    private String startDate;

    private String endDate;

    private String email;

    public ReconActParameters() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ReconActParameters{" +
                "id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
