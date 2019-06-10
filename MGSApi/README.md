# Định nghĩa Message trong MGS
1. LoginMessage

`JsonArray = [1, zoneName:String, username:String, password:String, JsonObject:params]`

Trong đó tham số đầu tiên có giá trị 1 = là message type login

2. LogoutMessage

	`JsonArray = [2, zoneName:String]`
3. JoinRoomMessage

	`JSonArray = [3, zoneName:String, roomId:Integer, password:String]`
4. LeaveRoomMessage

	`JsonArray = [4,zoneName:String, roomId:Int]`
5. RoomPluginMessage

	`JsonArray = [5, zoneName:String, roomId:Int, params:JsonObject]`
6. ZonePluginMessage

	`JsonArray = [6, zoneName:String, pluginName:String, params:JsonObject]`
7. PingMessage

	`JsonArray = [7, zoneName:String, id:Int, timstamp:Long]`

    Trong đó id:Int là id của message, do client tự đánh dấu
    timestamp:LOng là unix timestamp lúc gửi

# Định nghĩa các loại response

1. LoginResponse

	`JsonArray = [messageId:Int  1, success:bool, errorCode:int, username:String, zoneName:String, message:String]`
2. LogoutResponse

	`JsonArray = [messageId:Int 2, success:bool, reason:Int]`
3. JoinRoomResponse

	`JsonArray = [messageId:Int 3, success:bool, errorCode:Int, roomId:int, message:String]`
4. LeaveRoomResponse

	`JsonArray = [messageId:Int  4, success:bool, errorCode:Int, roomId:Int, reason:Int]`
5. ExtensionResponse

	`JsonArray = [messageId:Int  5, data:JsonObject]`
6. PingResponse

    `JsonArray = [messageId:Int  6, id:Int, timestamp:Long]`

