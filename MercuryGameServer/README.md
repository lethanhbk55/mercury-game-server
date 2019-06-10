## Giới thiệu
MercuryGameServer được thiết kế tương tự như SmartFoxServer hay ElectroServer, cho phép bạn tự do thiết kế các `plugins` cắm vào MercuryGameServer để chạy thông qua việc cài đặt các file thư viện (.jar) và định nghĩa class handle thông qua `mercury.plugin.xml`

MGS là 1 extension run dưới Mario, và nó cũng thiết kế tương tự như Mario cho phép các load `plugins` đặt dưới thư mục `Mario/extensions/MercuryGameServer/plugins`

Start MercuryGameServer chính là start Mario, khi phần mở rộng MercuryGameServer được khởi tạo, bên trong nó sẽ cài đặt việc load các plugin nằm dưới thư mục `MercuryGameServer/plugins`

- plugins
    - [extension_name] (Eg: MercurySimmsExtension)
        - lib (Chứa file các file jar của phần mở rộng)
        - conf (Thư mục do plugin tự định nghĩa)
        - resources (Thưc mục do plugin tự định nghĩa)
        - `mercury.plugin.xml` (File định nghĩa zone, handle class xem file [sample](samp/mercury.plugin.xml))

MercuryGameServer hỗ trợ client kết nối qua TCP, UDT, Websocket và, hỗ trợ call server to server qua RabbitMQ gateway, thông số port, khai báo datasource trong [extension.xml](extension.xml)

## Cơ chế Zone - Room trong Mercury Game Server

* Mỗi một plugin chạy dưới MGS tương ứng với 1 zone, khi client login vào Game Server nó phải mang theo tham số `zoneName`. 

Định nghĩa zone trong `mercury.plugin.xml`

```xml
<zone>
    <name>TestZone</name>
    <handle>com.mercury.extension.test.TestZonePlugin</handle>
    ...
</zone>
```

`TestZone` là zoneName client sẽ login vào game server
`com.mercury.extension.test.TestZonePlugin` là class sẽ handle việc user login, logout, disconnect vào zone.

Client send login (Xem [TestZoneMGSClient](/MGSApi/src/test/java/com/mercury/client/test/TestZoneMGSClient.java))
```java
MGSClient client = new MSGClient();
LoginMessage loginMessage = new LoginMessage(username, password, params);
loginMessage.setZoneName("TestZone");
client.send(loginMessage);
```
Server handle event login

```java
@Override
public void onUserLogin(LoginContext context) throws MGSException {
    //Tiền xử lý việc user login, có thể check username, password tại đây
}

@Override
public void userLoggedIn(User user) {
    //User login thành công
    getLogger().debug("user logged in: {}", user.getUsername());
}
```
## ZonePlugin và RoomPlugin trong MGS
Trong khi phát triển app hay game, chúng ta luôn có nhiều nghiệp vụ cần chia tách, có thể chia cho nhiều team, và để dễ quản lý theo từng miền nghiệp vụ. Do đó MGS cung cấp cơ chế cho plugin có thể khai báo nhiều Plugin con để chia tách miền nghiệp vụ

* ZonePlugin khai báo ngay trong `lifecycles` của `mercury.plugin.xml`
* RoomPlugin là plugin xử lý gắn liền với room, class được khai báo khi tạo room

Đăng ký và tạo HelloPlugin

```xml
<plugin>
    <name>helloPlugin</name>
    <handle>com.mercury.extension.test.plugin.HelloPlugin</handle>
    <variables>
        <variable name="number" type="integer">77</variable>
        <variable name="district" type="string">District 3</variable>
    </variables>
</plugin>
```
```java
public class HelloPlugin extends MGSPlugin {

	@Override
	public void request(User user, PuObject params) {
		String command = params.getString("command");
		switch (command) {
		case "hello":
			String name = params.getString("name");
			PuObject message = new PuObject();
			message.setString("command", command);
			message.setString("say", "hello " + name);
			getPluginApi().sendMessageToUser(user.getUsername(), message);
			break;

		default:
			break;
		}
	}

	@Override
	public void init(PuObjectRO initParams) {
		getLogger().debug("init hello plugin with params: {}", initParams);
	}
}
```
Client side send hello command (Xem [TestZoneMGSClient](/MGSApi/src/test/java/com/mercury/client/test/TestZoneMGSClient.java))
```java
PuObject params = new PuObject();
params.setString("command", "hello");
params.setString("name", "baymet");
this.send(new ZonePluginMessage("helloPlugin", params));
```
Response nhận được type `ExtensionResponse`
```
if (response instanceof ExtensionResponse) {
    getLogger().debug("data: {}", ((ExtensionResponse) response).getData());
```