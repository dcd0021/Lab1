

import java.io.*;
import java.net.*;
import java.util.Random; 


class UDPClient {
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser =  new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
       InetAddress IPAddress = InetAddress.getByName("Tux050"); 
        
        //variables
        byte[] sendData = new byte[256];
        String data = "";
        String indata = "";
        int eomessage = 1;
        int iterate = 1;
        
        
        //GET method usage to set up SendData
        sendData = getHTTPRequest().getBytes();
        
        //Get the packet ready to send
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 10014);
        
        //send the packet
        System.out.println("Sending\n");
        clientSocket.send(sendPacket);
        
        while(eomessage != 0){
            //get the datagram
            byte[] receiveData = new byte[256];
			System.out.println("At the datagram");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			System.out.println("At the datagram below");
            clientSocket.receive(receivePacket);
            System.out.println("Got a packet!!");
            
            //Get the data from the datagram
            byte [] messageData = receivePacket.getData();
            
            //If the last packet stop the loop
            int x = 0;
            while(x < messageData.length-1 && eomessage != 0){
                eomessage = messageData[x];
                x++;
            }
            
            //Put the message back together
            if (iterate > 1 && eomessage !=0){
                data = new String(receiveData);
                String getridofheader = headerdelete(data); //get rid of the header
                indata = indata.concat(getridofheader);
            }
            iterate++;
            
        }
        //Print data and write to the file
        System.out.println("The File message is: \n" + data);
        filewrite(indata);
        clientSocket.close();
        
        
    }
    //delete the header of the packet to print
    public static String headerdelete(String packet){
        String delete = packet.substring(packet.indexOf(":") + 11);
        return delete;
    }
	
	//HTTP get request
    public static String getHTTPRequest() throws IOException {
        BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
        System.out.println("What file do you want to view?\n");
        String input = in.readLine();
        return "GET " + input + ".html/1.0";
    }
    
	//Write to a file the data from the server
    public static void filewrite(String out) throws IOException{
        PrintWriter write = new PrintWriter("Out.txt");
        write.println(out);
        write.close();
        System.out.println("Written to file");
    }
    public void CheckSum() {
        //checks for errors in the message that could have been modified
    }
    
} // UDPClient




class UDPServer {
    
    public static void main(String args[]) throws Exception {
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
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
                    serverSocket.send(sendPacket);
					
					//sending packets to client
                } else {
                    
                    sendData = dataToSend.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
                    serverSocket.send(sendPacket);
                    
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
        message += "insert check sum";
        return message;
    }
    
    
    
    public static byte[] padPacketWithSpaces(byte[] header) {
        
        byte[] paddedHeader = new byte[123];
        
        for (int i = 0; i < paddedHeader.length; i++) {
            if (i < header.length) {
                paddedHeader[i] = header[i];
            } else {
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
		int firstByte = rands.nextInt(256);
		int secondByte = rands.nextInt(256);
		int thirdByte = rands.nextInt(256);
	if(gremResult > prob){
		if(rand <= 50){
			//change one byte
			input[firstByte] = 44;
		}
		if (rand <= 80 && rand > 50){
			//change two bytes
			input[firstByte] = 44;
			input[secondByte] = 44;
		}
		if (rand <= 100 && rand > 80){
			//change three bytes
			input[firstByte] = 44;
			input[secondByte] = 44;
			input[thirdByte] = 44;
		}
	}
	return input;
	//then pass to checksum
    }
    
    public static int checkSum(byte[] d) {
        int s = 0;
        for (int i = 0; i < d.length; i++) s += d[i];
        return s;
    } // checkSum()
} // UDPServer



