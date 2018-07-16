import java.io.*;
import java.net.*;
import java.util.*; 
import java.nio.charset.StandardCharsets;


abstract class UDP {
   static final String crlf = "\r\n\r\n";
   static final int headerSize = 8;

   public static int checkSum(byte[] d) {
      int s = 0;
      for (int i = 0; i < d.length; i++) s += d[i];
      return s;
   } // checkSum()

}


class UDPClient extends UDP {


   public static void sendRequest() {

   } // sendRequest()


   public static void main(String args[]) throws Exception {
      String randoms = "wxyz";
      BufferedReader inFromUser =  new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      BufferedWriter writer = new BufferedWriter(new FileWriter("Outing.txt"));
      InetAddress IPAddress = InetAddress.getByName("Tux056"); 
        
      byte[] sendData = new byte[256];
      String str = "";
        
      sendData = getHTTPRequest().getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 10014);
      clientSocket.send(sendPacket);
      byte[] receiveData = new byte[256];

      while (true) {    

         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         clientSocket.receive(receivePacket);
            
         String incomingString = new String(receivePacket.getData(), StandardCharsets.UTF_8);

         if (incomingString.contains(randoms)){
            System.out.println("Found last packet--breaking");
            break;
         }

         int serverCheckSum = extractCheckSum(incomingString);

         String message = deleteHeader(incomingString);

         System.out.println(message);

         int clientCheckSum = checkSum(incomingString.getBytes());

       //  System.out.println("server check sum is [" + serverCheckSum + "] client check sum is [" + clientCheckSum + "]");
         str.concat(incomingString);
         writer.write(incomingString);

            
      } // while true

      writer.close();
      clientSocket.close();  
   } // main()


  // public static void extractData

   
   public static int extractCheckSum(String s ) {
      String fDim = "Checksum:";

     if (!s.contains(fDim) || !s.contains(crlf)) return - 1;

      String l = s.split(fDim)[1];
      String r = l.split(crlf)[0];
      return Integer.parseInt(r);
   } // extractCheckSum()


    //delete the header of the packet to print
   public static String deleteHeader(String packet){
      if(!packet.contains(crlf)) return packet;
      String [] dat = packet.split(crlf);
      return dat[1];
   } // deleteHeader()
	

	//HTTP get request
   public static String getHTTPRequest() throws IOException {
      BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
      System.out.println("What file do you want to view? (exclude .html)");
      String input = in.readLine();
      return "GET " + input + ".html/1.0";
   } // getHTTPRequest()
    
    
} // UDPClient




class UDPServer extends UDP {

   public static int getProbability() throws IOException {
      BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
      System.out.println("What probabilty would you like the Gremlin to use?");
      String probs = in.readLine();
      return Integer.parseInt(probs);
   } // getProbability()


   public static void main(String args[]) throws Exception {

      String randoms = "\r\n\r\nwxyz";
      int prob = getProbability();
      System.out.println("Server Initialized, waiting for messages...");
      DatagramSocket serverSocket = new DatagramSocket(10014);
        
   	//variables for sending and receiving data
      byte[] receiveData = new byte[256];
      byte[] sendData  = new byte[256];
   	
      while (true) {

         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         System.out.println("Server");
         int port = receivePacket.getPort();
         System.out.println("The port number is " + port + "\n");
         InetAddress addr = receivePacket.getAddress();
            
         String request = new String(receivePacket.getData());
         String[] spReq = request.split("  ");
         int checkSum = checkSum(receiveData);
            
         RandomAccessFile data = new RandomAccessFile("TestFile.html", "r");

         long fSize = 0;
         int bytesRead = 0;
         int packetNumber = 0;
      
            // Create and send packet
         while (bytesRead != -1) {
                
            String pseudoHeader = "";
            String dataToSend = "";
            byte[] header;
            byte[] packet = new byte[256];
            byte[] tHeader = new byte[27];
                
            pseudoHeader = makePacketHeader(packetNumber, checkSum);
            header = pseudoHeader.getBytes();
           // packet = padPacketWithSpaces(header);

            String fakeheader = defaultHeader();
            bytesRead = data.read(packet, header.length, (packet.length - tHeader.length));
            System.out.println("packet[" + new String(packet) + "]");

            int getCheckSum = checkSum(packet);
            System.out.println("THE PACKET L is" + getCheckSum);

            dataToSend = calculateCheckSum(packet);


            // if last packet
            if (bytesRead == -1) {
               header[header.length - 1] = 0;
               dataToSend = calculateCheckSum(header);
               sendData = dataToSend.getBytes();

               byte [] nullarray = randoms.getBytes();
               DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
               DatagramPacket nullpacket = new DatagramPacket(nullarray, nullarray.length, addr, port);
               serverSocket.send(sendPacket);
               serverSocket.send(nullpacket);
               break;
            	//sending packets to client
            } 
            else {
               sendData = dataToSend.getBytes();
               checkSum = checkSum(sendData);
               sendData = Gremlin(sendData, prob);
               DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
               serverSocket.send(sendPacket);
            }
                
            packetNumber++;
                
                
         } // while bytesRead
                        
      } // while

   } // main()


   public static String defaultHeader() {
      return "Packet 00\nChecksum:0000\r\n\r\n";
   } // pseudoHeaderCreator()


   public static String makePacketHeader(int packetNum, int checkSum) {
      return "Packet " + packetNum + "\nChecksum:" + checkSum + crlf;
   }

    
    //gets the checksum to check for bit errors
   public static String calculateCheckSum(byte[] packetInfo) {
      int fullCheckSum = checkSum(packetInfo);
      String message = new String(packetInfo);
      String checkSum = Integer.toString(fullCheckSum);
      //message += "insert check sum";
      return message;
   }
    
    
    
   public static byte[] padPacketWithSpaces(byte[] header) {
      byte[] paddedHeader = new byte[123];
      for (int i = 0; i < paddedHeader.length; i++) {
         if (i < header.length) paddedHeader[i] = header[i];
         else paddedHeader[i] = 32;
      }
      return paddedHeader;
   }
    


    
    
   public static String packetHeader(long n) {
      return "HTTP/1.0 200 Document Follows\r\n" +
            "Content-Type: text/plain\r\n" +
            "Content-Length: " + n + crlf + "Data";
   } // packetHeader()
    

    //44 is a random number to corrupt the bit
   public static byte[] Gremlin(byte[] input, int prob) {
      Random randForGremln = new Random(); // see if we are going to corrupt
      Random rands = new Random(); // pick how many bits to corrupt
      int gremResult = randForGremln.nextInt(100);
      int rand = randForGremln.nextInt(100);
      int firstByte = rands.nextInt(32);
      int secondByte = rands.nextInt(32);
      int thirdByte = rands.nextInt(32);
      if(gremResult <= prob){
         if(rand <= 50){
         //change one byte
            System.out.println("First BYTE");
            input[firstByte] = '?';
         }
         if (rand <= 80 && rand > 50){
         //change two bytes
            System.out.println("Second BYTE");
            input[firstByte] = '?';
            input[secondByte] = '?';
         }
         if (rand <= 100 && rand > 80){
         //change three bytes
            System.out.println("Third BYTE");
            input[firstByte] = '?';
            input[secondByte] = '?';
            input[thirdByte] = '?';
         }
      }
      return input;
   
   }
   

} // UDPServer



