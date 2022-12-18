
package chat01;

import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;



public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;

	private String port;
	private Socket clientSocket;
	private HashMap<String, Client> clientsMap;
	
	
	

	public Client() {
		super();
	}
	public Client(String userName, String password, Socket clientSocket) {
		super();
		this.userName = userName;
		this.password = password;
		this.clientSocket = clientSocket;
	}
	public Client(String userName, String password, String port, Socket clientSocket,
			HashMap<String, Client> clientsMap) {
		super();
		this.userName = userName;
		this.password = password;
		this.port = port;
		this.clientSocket = clientSocket;
		this.clientsMap = clientsMap;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the clientSocket
	 */
	public Socket getClientSocket() {
		return clientSocket;
	}
	/**
	 * @param clientSocket the clientSocket to set
	 */
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	/**
	 * @return the clientsMap
	 */
	public HashMap<String, Client> getClientsMap() {
		return clientsMap;
	}
	/**
	 * @param clientsMap the clientsMap to set
	 */
	public void setClientsMap(HashMap<String, Client> clientsMap) {
		this.clientsMap = clientsMap;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
	
	

//import javafx.beans.property.SimpleStringProperty;
//
//public class Client implements Serializable{
//
//	private static final long serialVersionUID = 1L;
//	
//	private final SimpleStringProperty userName;	
//	private final SimpleStringProperty password;
//	private final SimpleStringProperty port;
//	private final SimpleStringProperty clientSocket;
// 
//	
//	
//	
//	
//	
//	
//    public Client(String userName, String password, int port,
//			Socket clientSocket) {
//		super();
//		this.userName = userName;
//		this.password = password;
//		this.port = port;
//		this.clientSocket = clientSocket;
//	}
//    
//    
//    
//
//	public Client(String fName, String lName, String ip) {
//        this.firstName = new SimpleStringProperty(fName);
//        this.lastName = new SimpleStringProperty(lName);
//        this.ipAddress = new SimpleStringProperty(ip);
//    }
//
//    public String getFirstName() {
//        return firstName.get();
//    }
//
//    public void setFirstName(String fName) {
//        firstName.set(fName);
//    }
//
//    public String getLastName() {
//        return lastName.get();
//    }
//
//    public void setLastName(String fName) {
//        lastName.set(fName);
//    }
//
//    public String getIpAddress() {
//        return ipAddress.get();
//    }
//
//    public void setIpAddress(String fName) {
//        ipAddress.set(fName);
//    }
//
//
//
//	@Override
//	public String toString() {
//		return "Client [firstName=" + firstName + ", lastName=" + lastName + ", ipAddress=" + ipAddress + "]";
//	}
//}