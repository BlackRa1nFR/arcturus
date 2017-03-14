package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.navigator.CanCreateRoomComposer;
import com.eu.habbo.messages.outgoing.navigator.RoomCreatedComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomOpenComposer;

public class RequestCreateRoomEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        String name = this.packet.readString();
        String description = this.packet.readString();
        String modelName = this.packet.readString();
        int categoryId = this.packet.readInt();
        int maxUsers = this.packet.readInt();
        int tradeType = this.packet.readInt();

        RoomLayout layout = Emulator.getGameEnvironment().getRoomManager().getLayout(modelName);

        if(layout == null)
        {
            Emulator.getLogging().logErrorLine("[SCRIPTER] Incorrect layout name \""+modelName+"\". " + this.client.getHabbo().getHabboInfo().getUsername());
            return;
        }

        RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId);

        if(category == null || category.getMinRank() > this.client.getHabbo().getHabboInfo().getRank())
        {
            Emulator.getLogging().logErrorLine("[SCRIPTER] Incorrect rank or non existing category ID: \""+categoryId+"\"." + this.client.getHabbo().getHabboInfo().getUsername());
            return;
        }

        if(maxUsers > 250)
            return;

        if(tradeType > 2)
            return;

        int count = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo()).size();
        int max = this.client.getHabbo().getHabboStats().hasActiveClub() ? Emulator.getConfig().getInt("hotel.max.rooms.vip") : Emulator.getConfig().getInt("hotel.max.rooms.user");
        
        if(count >= max)
        {
            this.client.sendResponse(new CanCreateRoomComposer(count, max));
            return;
        }

        final Room room = Emulator.getGameEnvironment().getRoomManager().createRoom(this.client.getHabbo(), name, description, modelName, maxUsers, categoryId);

        if(room != null)
        {
            if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
            {
                Emulator.getGameEnvironment().getRoomManager().leaveRoom(this.client.getHabbo(), this.client.getHabbo().getHabboInfo().getCurrentRoom());
            }

            this.client.getHabbo().getHabboInfo().setLoadingRoom(room.getId());
            this.client.sendResponse(new RoomCreatedComposer(room));
        }
    }
}
