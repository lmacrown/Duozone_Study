package ch19.sec14;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RoomManager {

    public List<SocketClient> clients;    // 전체 Client
    public List<Room> rooms;        // 전체 Room
    public String roomStatus;       // 반환 Json -> String Room
    public int roomNumber=1;
    
    Map<String, Room> roomRecord = Collections.synchronizedMap(new HashMap<>());
    
    
    public RoomManager(List<SocketClient> clients) {
        this.clients = clients;
        this.rooms = new Vector<>();
        this.roomStatus = "[]";
    }

    /**
     * [ Method :: updateRoomStatus ]
     *
     * @DES :: Client에 전달할 텍스트 처리하는 함수
     * @S.E :: Json -> Text
     * */
    void updateRoomStatus() {
        // System.out.println("updateRoomStatus call");
        roomStatus = "[room status]\n";
        if(rooms.size() > 0) {
            for (Room room : rooms) {
                roomStatus += String.format("{\"id\":\"%s\",\"title\":\"%s\"},", room.no, room.title);
            }
            roomStatus = roomStatus.substring(0,roomStatus.length()-1);
        }
         System.out.println("[채팅서버] " + roomStatus);
    }
    
    

    /**
     * [ Method :: createRoom ]
     *
     * @DES :: 새로운 방생성 함수
     * @IP1 :: title {String}
     * @IP1 :: client {Client}
     * @S.E :: 모든 Client의 상태를 업데이트
     * */
    public Room loadRoom(String clientChatName) {
    	return roomRecord.get(clientChatName);
    }
    
    
    public void createRoom( String title, SocketClient client ) {
        // ### unique id ###
        //String uniqueID = UUID.randomUUID().toString();
        Room newRoom = new Room(this, /*uniqueID,*/ roomNumber, title );
        roomNumber++;
        rooms.add(newRoom);

        // #전달 - 모든 Client에게 상황보고
        //updateRoomStatus();
        //for (int i = 0; i < clients.size(); i++) { clients.get(i).sendStatus(); }

        // #입장
        //newRoom.entryRoom(client);
    }

    /**
     * [ Method :: destroyRoom ]
     *
     * @DES :: 기존방 제거하는 함수
     * @IP1 :: room {Room}
     * @S.E :: 모든 Client의 상태를 업데이트
     * */
    public void destroyRoom(Room room) {
        rooms.remove(room);

        // #전달 - 모든 Client에게 상황보고
        //updateRoomStatus();
        //for (int i = 0; i < clients.size(); i++) { clients.get(i).sendStatus(); }
    }

}
