/**
 * TerminalServerManager
 *    TerminalSession.java
 *
 *  @author Dmitry Grinshteyn
 *  @version 1.1 dated 2018-08-30
 */

package ru.trustsoft.model;

public class TerminalSession {

    private String username;
    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TerminalSession{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
