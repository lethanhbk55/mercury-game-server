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




