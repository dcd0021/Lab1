import java.io.*;
import java.net.*;
import java.util.*; 
import java.nio.charset.StandardCharsets;


abstract class UDP { 
   static final String crlf = "\r\n\r\n";
   static final String checkSumKeyWord = "Checksum:";

   public static int checkSum(byte[] d) {
      int s = 0;
      for (int i = 0; i < d.length; i++) s += d[i];
      return s;
   } // checkSum()

} // UDP


class UDPClient extends UDP {


   public static void main(String args[]) throws Exception {
      DatagramSocket clientSocket = new DatagramSocket();
      BufferedWriter writer = new BufferedWriter(new FileWriter("Outing.txt"));
      int probabilty = getProbability();

      sendRequest(clientSocket);
      byte[] receiveData = new byte[256];

      while (true) {    
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         clientSocket.receive(receivePacket);
         String incomingString = new String(receivePacket.getData(), StandardCharsets.UTF_8);

         int packetNumber = extractPacketNumber(incomingString);
         int serverCheckSum = extractCheckSum(incomingString);
         String message = deleteHeader(incomingString);

         if (serverCheckSum == 0) break;

         message = Gremlin(message, probabilty);
         int clientCheckSum = checkSum(message.getBytes());
         System.out.print(message);

         if (serverCheckSum != clientCheckSum) 
            System.out.print("\n\n! Detected Packet:" + packetNumber + " is Corrupted !\n\n");
         
         writer.write(incomingString);   
      } // while true

      System.out.println("\n\n");
      writer.close();
      clientSocket.close();  
   } // main()


   public static void sendRequest(DatagramSocket clientSocket) throws Exception {
      InetAddress IPAddress = InetAddress.getByName("Tux058"); 
      byte[] sendData = new byte[256];
      sendData = getHTTPRequest().getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 10014);
      clientSocket.send(sendPacket);
   } // sendRequest()


   public static String getHTTPRequest() throws IOException {
      BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
      System.out.println("What file do you want to view? (exclude .html)");
      String input = in.readLine();
      return "GET " + input + ".html/1.0";
   } // getHTTPRequest()


   public static int extractPacketNumber(String s) {
      String fDim = "Packet:";
      String sDim = "\n" + checkSumKeyWord;
      if (!s.contains(fDim) || !s.contains(sDim)) return - 1;
      return Integer.parseInt(s.split(fDim)[1].split(sDim)[0]);
   } // extractPacketNumber()


   public static int extractCheckSum(String s) {
      if (!s.contains(checkSumKeyWord) || !s.contains(crlf)) return - 1;
      return Integer.parseInt(s.split(checkSumKeyWord)[1].split(crlf)[0]);
   } // extractCheckSum()


   public static String deleteHeader(String packet){
      if(!packet.contains(crlf)) return packet;
      return packet.split(crlf)[1];
   } // deleteHeader()


   public static int getProbability() throws IOException {
      BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
      System.out.println("What probabilty would you like the Gremlin to use?");
      String probs = in.readLine();
      return Integer.parseInt(probs);
   } // getProbability()


   public static String Gremlin(String in, int prob) {
      byte [] input = in.getBytes();
      Random rGen = new Random();

      int innerProbability = rGen.nextInt(100);
      if (prob < innerProbability) return in;

      int curruptBits = rGen.nextInt(3);
      while (curruptBits-- > 0) input[rGen.nextInt(input.length - 1)] = '?';

      return new String(input, StandardCharsets.UTF_8);
   } // Gremlin()
	
    
} // UDPClient


class UDPServer extends UDP {

   public static void main(String args[]) throws Exception {
      DatagramSocket serverSocket = new DatagramSocket(10014);
      byte[] receiveData = new byte[256];
      byte[] sendData  = new byte[256];
   	
      while (true) {
         System.out.println("Server Initialized, waiting for messages...");
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         int port = receivePacket.getPort();
         System.out.println("Server - The port number is " + port);
         InetAddress addr = receivePacket.getAddress();

         String request = new String(receivePacket.getData());
         RandomAccessFile data = new RandomAccessFile(extractFileName(request), "r");
      	
         int numberOfBits = 0, sequenceNum = 0;      
         while (numberOfBits != -1) {
                
            byte[] packet = new byte [256];
            numberOfBits = data.read(packet, 0, (packet.length - defaultHeader().getBytes().length));

            // set as null char 
            if (numberOfBits == -1) packet = new byte [packet.length];
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(makePacketHeader(sequenceNum, checkSum(packet)).getBytes());
            outputStream.write(packet);
            sendData = outputStream.toByteArray();

            System.out.println("\n\npacket:"+ sequenceNum + "\ndata:[" + new String(sendData, StandardCharsets.UTF_8) + "]");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
            serverSocket.send(sendPacket);

            sequenceNum++;
         } // while numberOfBits
            
      } // while true

   } // main()


   public static String extractFileName(String s) {
      return s.split("GET ")[1].split(".html")[0] + ".html";
   } // extractFileName()


   public static String makePacketHeader(int packetNum, int checkSum) {
      return "Packet:" + packetNum + "\n" + checkSumKeyWord + checkSum + crlf;
   } // makePacketHeader()


   public static String defaultHeader() {
      return "Packet:0000\n" + checkSumKeyWord + "0000" + crlf;
   } // defaultHeader()
    
    
   public static String packetHeader(long n) {
      return "HTTP/1.0 200 Document Follows\r\n" +
            "Content-Type: text/plain\r\n" +
            "Content-Length: " + n + crlf;
   } // packetHeader()
    

} // UDPServer

