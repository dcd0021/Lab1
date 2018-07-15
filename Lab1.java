

import java.io.*;
import java.net.*;
import java.util.*; 
import java.nio.charset.StandardCharsets;



class UDPClient {
   public static void main(String args[]) throws Exception {
      String randoms = "wxyz";
      BufferedReader inFromUser =  new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      BufferedWriter writer = new BufferedWriter(new FileWriter("Outing.txt"));
      InetAddress IPAddress = InetAddress.getByName("Tux059"); 
        
        //variables
      byte[] sendData = new byte[256];
        //String data = "";
      String indata = "";
      int eomessage = 1;
      int iterate = 1;
      String str = "";
        
        //GET method usage to set up SendData
      sendData = getHTTPRequest().getBytes();
        
        //Get the packet ready to send
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 10014);
        
        //send the packet
        //System.out.println("Sending\n");
      clientSocket.send(sendPacket);
      byte[] receiveData = new byte[256];
      while(eomessage != 0){
            //get the datagram
            
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            //receive datagram
         clientSocket.receive(receivePacket);
            
      	//Get the data from the datagram
         byte [] messageData = receivePacket.getData();
         //System.out.println(messageData);
      	
      	
      
         String srtTemp = new String(messageData,StandardCharsets.UTF_8);
         str.concat(srtTemp);
         writer.write(srtTemp);
         srtTemp = headerdelete(srtTemp);
         
         System.out.println(srtTemp);
         if (srtTemp.contains(randoms)){
            System.out.println("The message is" + messageData + "WE BROKE");
            break;
         }
      	
            
      }
        //Print data and write to the file
      System.out.println(str);
      writer.close();
      
      clientSocket.close();
        
        
   }
    //delete the header of the packet to print
   public static String headerdelete(String packet){
      if(!packet.contains("\r\n\r\n")){
         return packet;
      }
      String delimiter = "\r\n\r\n";
   
      String [] dat = packet.split(delimiter);
      return dat[1];
      
   }
	
	//HTTP get request
   public static String getHTTPRequest() throws IOException {
      BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
      System.out.println("What file do you want to view?\n");
      String input = in.readLine();
      return "GET " + input + ".html/1.0";
   }
    
    
	
   //Write to a file the data from the server
   /*
   public static void filewrite(String out) throws IOException{
      PrintWriter write = new PrintWriter("Out.txt");
      write.println(out);
      write.close();
      System.out.println("Written to file");
   }
   */
   public void CheckSum() {
        //checks for errors in the message that could have been modified
   }
    
} // UDPClient




class UDPServer {
     
   public static void main(String args[]) throws Exception {
      String randoms = "\r\n\r\nwxyz";
      BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
      System.out.println("What probabilty would you like the Gremlin to use?");
      String probs = in.readLine();
      int prob = Integer.parseInt(probs);
      System.out.println("Server Initialized, waiting for messages...");
        //Set up datagram and assign socket
      DatagramSocket serverSocket = new DatagramSocket(10014);
        
   	
   	//variables for sending and receiving data
      byte[] receiveData = new byte[256];
      byte[] sendData  = new byte[256];
   	
   	
        //infinite loop that is waiting to send and receive packets
      while (true) {
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         System.out.println("Server");
         int port = receivePacket.getPort();
         System.out.println("The port number is " + port + "\n");
         InetAddress addr = receivePacket.getAddress();
            
         String request = new String(receivePacket.getData());
         String[] spReq = request.split("  ");
            
            
            // get the filename and access it
            //String fName = spReq[1];
         RandomAccessFile data = new RandomAccessFile("TestFile.html", "r");
      	
         long fSize = 0;
         int dataOffset = 0;
         int sequenceNum = 0;
      
            // Create and send packet
         while (dataOffset != -1) {
                
            String pseudoHeader = "";
            String dataToSend = "";
            byte[] header;
            byte[] packet;
                
                
            if (sequenceNum == 0) {
               pseudoHeader = packetHeader(fSize);
               header = pseudoHeader.getBytes();
               packet = padPacketWithSpaces(header);
            }
            else {
                    
               pseudoHeader = makePacketHeader(sequenceNum);
               header = pseudoHeader.getBytes();
            	//System.out.println("HEADER!!!!!");
               System.out.println(header);
               packet = padPacketWithSpaces(header);
               dataOffset = data.read(packet, header.length, (packet.length - header.length));
            }
                
            System.out.println("Processing packet " + (sequenceNum));
                
            dataToSend = calculateCheckSum(packet);
                
            System.out.println(dataToSend);
            System.out.println("Sending packet ");
                
                // if last packet
            if (dataOffset == -1) {
               header[header.length - 1] = 0;
               dataToSend = calculateCheckSum(header);
               sendData = dataToSend.getBytes();
               byte [] nullarray = randoms.getBytes();
               //sendData = Gremlin(sendData,prob);
               DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
               DatagramPacket nullpacket = new DatagramPacket(nullarray, nullarray.length, addr, port);
               serverSocket.send(sendPacket);
               serverSocket.send(nullpacket);
               System.out.println("LAST PACKET!!!!");
               break;
            	//sending packets to client
            } 
            else {
                    
               sendData = dataToSend.getBytes();
               System.out.println(sendData.length);
               sendData = Gremlin(sendData, prob);
               DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
               serverSocket.send(sendPacket);
            	//System.out.println("Sending the first packets!");
                    
            }
                
            sequenceNum++;
                
                
         } // while dataOffset
            
            
            
            
      } // while
   } // main()
    
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
         if (i < header.length) {
            paddedHeader[i] = header[i];
         } 
         else {
            paddedHeader[i] = 32;
         }
      }
      return paddedHeader;
   }
    
   public static String makePacketHeader(int packetNum) {
      return "Packet " + packetNum + "\nChecksum: 00000\r\n\r\n";
   }
    
    
   public static String packetHeader(long n) {
      return "HTTP/1.0 200 Document Follows\r\n" +
            "Content-Type: text/plain\r\n" +
            "Content-Length: " + n  + "\r\n\r\nData";
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
   
   	
           //then pass to checksum
   
   
    
   public static int checkSum(byte[] d) {
      int s = 0;
      for (int i = 0; i < d.length; i++) s += d[i];
      return s;
   } // checkSum()
} // UDPServer



