import java.io.*; 
import java.net.*; 


  
class UDPClient { 
   public static void main(String args[]) throws Exception 
   { 
      BufferedReader inFromUser = 
         new BufferedReader(new InputStreamReader(System.in)); 
   
      DatagramSocket clientSocket = new DatagramSocket(); 
   
      InetAddress IPAddress = InetAddress.getByName("131.204.14.5"); 
   
      byte[] sendData = new byte[256]; 
      
	  int end;
      //String sentence = inFromUser.readLine(); 
      sendData = getHTTPRequest().getBytes();         
      DatagramPacket sendPacket = 
         new DatagramPacket(sendData, sendData.length, IPAddress, 10014); 
	System.out.println("Sending\n");
      clientSocket.send(sendPacket); 
	
		System.out.println("Receiving\n");
 
	  
   //while !EOF, write to file
   String modifiedSentence = "test";
   int x = 10;
   while(x > 0){
	   System.out.println("Received\n");
	    byte[] receiveData = new byte[256];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
      x--;
	  clientSocket.receive(receivePacket); 
   }
    //modifiedSentence = new String(receivePacket.getData()); 
      //System.out.println("FROM SERVER:" + modifiedSentence); 
      clientSocket.close(); 
   }
   public static String getHTTPRequest() throws IOException {
	   BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
	   System.out.println("What file do you want to view?\n");
	   String input = in.readLine();
	   return "GET" + input + ".html/1.0";
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
   
      byte[] receiveData = new byte[256]; 
      byte[] sendData  = new byte[256]; 
   
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