package es.rubencordero.FlyCloud.Sessions;

import java.io.Serializable;

public class SessionData implements Serializable
{
    private String username;

    SessionData(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
}
