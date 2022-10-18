package socketproject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RoomManager {
   
    public List<Room> rooms;        
    public String roomStatus;       
    public int roomNumber=1;
    
    Map<String, Room> roomRecord = Collections.synchronizedMap(new HashMap<>());
    
    
    public RoomManager() {
        this.rooms = new Vector<>();
        this.roomStatus = "[]";
    }


    public Room loadRoom(String clientChatName) {
    	return roomRecord.get(clientChatName);
    }
    
    
    public void createRoom( String title, SocketClient client ) {
        Room newRoom = new Room(this, roomNumber, title );
        roomNumber++;
        rooms.add(newRoom);
        //for (int i = 0; i < clients.size(); i++) { clients.get(i).sendStatus(); }
    }

    public void destroyRoom(Room room) {
        rooms.remove(room);
    }

}
