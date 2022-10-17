package ch19.sec14;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Room {

    public RoomManager roomManager;
    //public String id;
    public int no;
    public String title;
    public List<String> clientsChatName;
    public List<SocketClient> clients;
    
    public Room(
            RoomManager roomManager,
            //String id,
            int no,
            String title ) {
        this.roomManager = roomManager;
        //this.id = id;
        this.no = no;
        this.title = title;
        clients = new Vector<>();
        clientsChatName = new ArrayList<>();
    }


    public void entryRoom(SocketClient client) {
    	clientsChatName.add(client.chatName);
        
        client.room = this;
		roomManager.roomRecord.put(client.chatName, this);
    }

   
    public void leaveRoom(SocketClient client) {
        //this.clients.remove(client);
        client.room = null;
        
        /*
        if(this.clients.size() < 1) {
            roomManager.destroyRoom(this);
        }
        */
    }
}
