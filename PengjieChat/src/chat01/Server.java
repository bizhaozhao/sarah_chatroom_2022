
package chat01;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import DES.DES;


public class Server implements Runnable {

	ServerSocket serverSocket;
	private int port;
	private Observable uiComponent;
	private static ArrayList<ClientProcessor> clientList = new ArrayList<ClientProcessor>();// socket集合
	private boolean isStart = false;

//	private Map<String, ClientProcessor> clients = new HashMap<>();

	public Server(int port, Observable uiComponent) {
		super();
		this.port = port;
		this.uiComponent = uiComponent;
	}

	@Override
	public void run() {
		try {
			// new ServerSocket 传port进去 意味服务器已经启动
			serverSocket = new ServerSocket(port);
			isStart = true;
			
			System.out.println(port + " is ready, waiting for clients to connect");

			while (isStart) {
				// 阻塞性方法 只连上了一个客户端 应放到永真循环里 就可以链接多个客户端
				Socket socket = serverSocket.accept();
				ClientProcessor clientProcessor = new ClientProcessor(socket, uiComponent);
				clientList.add(clientProcessor);			
				// new Thread可以放在这里 也可以放在ClientProcessor的构造器里
				// new Thread(clientProcessor).start();
			}
		} catch (SocketException e) {
			System.out.println("Server has broken off!");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	// 内部类 内部类是解决起线程的问题 observable是解决数据更新后刷新界面问题
	private static class ClientProcessor implements Runnable {

		private Socket socket;
		private Observable uiComponent;
		private JDBC jdbc = new JDBC();
		private String senderName;
		private String receiverName;
		private String messageContent;
		private String clientName;
		private String onlineClientName;
	//	private Map<String, ClientProcessor> clients = new HashMap<>();		
		private Map<String, Socket> clients = new HashMap<>();		

		String resultStr = null;
		private DES des = new DES();

		private ClientProcessor(Socket socket, Observable uiComponent) {
			this.socket = socket;
			this.uiComponent = uiComponent;
			new Thread(this).start();
			
		}

		@Override
		public void run() {
					
			connentDBcheckUsernameAndPassword(receiveUsernameAndPasswordFromLoginPage());		
			receiveClientnameFromLoginAndBroadcast();
			uiComponent.append("Client " + clientName + " is connected, ip address is: " + socket.getInetAddress() + "=》 Port is: "
					+ socket.getPort());
			
			try {
				receiveMessageFromClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String[] receiveUsernameAndPasswordFromLoginPage() {// Client

			String userInfo = null;
			try {
				InputStream inputStream = socket.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				userInfo = bufferedReader.readLine();
				//System.out.println("before split " + userInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return userInfo.split(",");

		}

		public void connentDBcheckUsernameAndPassword(String[] userInfo) {

			PreparedStatement ptmt;
			DataOutputStream dos;

			try {
				String sql = "SELECT * FROM `usertable` WHERE username=? AND password=?";
				ptmt = jdbc.getConnection().prepareStatement(sql);
//				ptmt.setString(1, client.getUserName());
//				ptmt.setString(2, client.getPassword());										
				ptmt.setString(1, userInfo[0]);
				ptmt.setString(2, userInfo[1]);

				ResultSet rSet = ptmt.executeQuery();
				if (rSet.next()) {				
					dos = new DataOutputStream(this.socket.getOutputStream());
					resultStr = "success";
					dos.writeUTF("success");

				} else {
					dos = new DataOutputStream(this.socket.getOutputStream());
					resultStr = "failed";
					dos.writeUTF("failed");

				}
				rSet.close();
				ptmt.close();
				jdbc.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

		public void connectDBToSaveData() throws Exception {
			
			PreparedStatement ptmt;		
			String sql = "INSERT INTO `messagetable`(`sendername`, `receivername`, `content`) VALUES (?,?,?)";
			try {
				ptmt = jdbc.getConnection().prepareStatement(sql);
				ptmt.setString(1, senderName);
				ptmt.setString(2, receiverName);
				//step2: decrypt message
				messageContent = des.decrypt(messageContent);
				ptmt.setString(3, messageContent);				
				ptmt.executeUpdate();			
				ptmt.close();
				jdbc.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		}
		
		public void receiveClientnameFromLoginAndBroadcast() {
					
			try {
			
				DataInputStream dis = new DataInputStream(socket.getInputStream());
			
				clientName = dis.readUTF();
			
				System.out.println("clientName: " + clientName);
				clients.put(clientName, this.socket);
							
				Iterator<ClientProcessor> iterator = clientList.iterator();
				while(iterator.hasNext()) {
					ClientProcessor c = iterator.next();
					c.broadcastAllClients("Broadcast news:" + clientName + " is online! You can click to start private chat!");
				} 
				sendOnlineClientNameToClientside(onlineClientName);
//				Iterator<String> clientIterator = clients.keySet().iterator();
//				while (clientIterator.hasNext()) {
//					String onlineClientName = clientIterator.next();
//					try {		
//						DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());						
//						dos.writeUTF("onlineClientName:" + onlineClientName);
//						
//						System.out.println("server onlineclientname: " + onlineClientName);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}					
//				}											
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		// 服务器端接收数据的方法 且是同时接客户信息
		public void receiveMessageFromClient() throws Exception {

			try {
				//DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				while (true) {
										
					InputStream inputStream = socket.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					//step1: read before decrypt
					String strReceived = bufferedReader.readLine();					
					senderName = strReceived.split(" said: ")[0];
					receiverName = "All";
					messageContent = strReceived.split(" said: ")[1];					
					//step2: decrypt message
					messageContent = des.decrypt(messageContent);
					//step3: append to ui
					uiComponent.append(senderName + " said: "+ messageContent);
					//step4: encrypt messageContent
					messageContent = des.encrypt(messageContent);
					//step5: pack username and message for sending to all clients
					strReceived = senderName + " said: "+ messageContent;
					strReceived = strReceived.substring(2);
				
					connectDBToSaveData();

					Iterator<ClientProcessor> iterator = clientList.iterator();
					while(iterator.hasNext()) {
						ClientProcessor c = iterator.next();
						c.sendMessageToClients(strReceived);
					} 
				}
			} catch (SocketException e) {
				System.out.println("One client is offline");
				uiComponent.append(socket.getPort() + " is offline!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 服务器端发送数据的方法 且是同时发送给客户 需要遍历ClientList
		public void sendMessageToClients(String strSend) {

			try {
				DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());						
				//String utf8EncodedString = new String(strSend.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);											
				// 发送给服务器端的message,相当于写过去，方法是dos.writeUTF，且服务器端必须有接收这个内容的方法
				dos.writeUTF(strSend+"\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}		
		
		public void broadcastAllClients(String name) {

			try {
				DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());								
				dos.writeUTF(name +"\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void sendOnlineClientNameToClientside(String name) {
			
			Iterator<String> clientIterator = clients.keySet().iterator();
			while (clientIterator.hasNext()) {
				onlineClientName = clientIterator.next();
				try {		
					DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());						
					dos.writeUTF("onlineClientName:" + onlineClientName);					
					System.out.println("server onlineclientname: " + onlineClientName);
				} catch (IOException e) {
					e.printStackTrace();
				}					
			}	
		}
		
		public void checkClientIsOnlineOrNot() {
			
			
				
		}
	}
}
