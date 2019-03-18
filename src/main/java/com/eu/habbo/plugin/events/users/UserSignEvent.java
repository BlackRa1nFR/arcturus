package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserSignEvent extends UserEvent
{
    /**
     * @param habbo The Habbo this event applies to.
     */

    public int sign;

    public UserSignEvent(Habbo habbo, int sign)
    {
        super(habbo);
        this.sign = sign;
    }
}
