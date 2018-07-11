import java.io.*; 
import java.net.*; 

// yoyoyooyyo
  
class UDPClient { 
   public static void main(String args[]) throws Exception 
   { 
      BufferedReader inFromUser = 
         new BufferedReader(new InputStreamReader(System.in)); 
   
      DatagramSocket clientSocket = new DatagramSocket(); 
   
      InetAddress IPAddress = InetAddress.getByName("scp.eng.auburn.edu"); 
   
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[1024]; 
   
      String sentence = inFromUser.readLine(); 
      sendData = sentence.getBytes();         
      DatagramPacket sendPacket = 
         new DatagramPacket(sendData, sendData.length, IPAddress, 10014); 
	System.out.println("Sending\n");
      clientSocket.send(sendPacket); 
	DatagramPacket receivePacket = 
         new DatagramPacket(receiveData, receiveData.length); 
		System.out.println("Receiving\n");
      clientSocket.receive(receivePacket); 
   //while !EOF, write to file
   String modifiedSentence = "test";
   while(modifiedSentence != null){
       modifiedSentence = 
          new String(receivePacket.getData()); 
   }
   
      System.out.println("FROM SERVER:" + modifiedSentence); 
      clientSocket.close(); 
   }
   
   
   public void Gremlin(int prob){
	   //based on a probablity changes some bits in the message
   }
    public void CheckSum(){
	   //checks for errors in the message that could have been modified 
   }
} 


class UDPServer { 
   public static void main(String args[]) throws Exception 
   { 
		
      DatagramSocket serverSocket = new DatagramSocket(10014); 
   
      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
   
      while(true) 
      { 
         DatagramPacket receivePacket = 
             new DatagramPacket(receiveData, receiveData.length); 
         serverSocket.receive(receivePacket); 
         String sentence = new String(receivePacket.getData()); 
      
         InetAddress IPAddress = receivePacket.getAddress(); 
      
         int port = receivePacket.getPort(); 
      
         String capitalizedSentence = sentence.toUpperCase(); 
         sendData = capitalizedSentence.getBytes(); 
		
         DatagramPacket sendPacket = 
             new DatagramPacket(sendData, sendData.length, IPAddress, 
                               port); 
      
         serverSocket.send(sendPacket); 
      } 
   } 
   public void Gremlin(){
	   
   }
   
   public void CheckSum(){
	   
   }
}