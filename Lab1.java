


import java.io.*;
import java.net.*;



class UDPClient {
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser =  new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        
		
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
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			
			
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
				String getridofheader = headerdelete(data);
				indata = indata.concat(getridofheader);
			}
			iterate++;
			
			}
			//Print data and write to the file
			System.out.println("The File message is: \n" + data);
			filewrite(indata);
        clientSocket.close();
		
		
    }

	public static String headerdelete(String packet){
		String delete = packet.substring(packet.indexOf(":") + 11);
		return delete;
	}
    public static String getHTTPRequest() throws IOException {
        BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
        System.out.println("What file do you want to view?\n");
        String input = in.readLine();
        return "GET" + input + ".html/1.0";
    }
    
	public static void filewrite(String out) throws IOException{
		PrintWriter write = new PrintWriter("Out.txt");
		write.println(out);
		write.close();
		System.out.println("Written to file");
	}
    public void Gremlin(int prob) {
        //based on a probablity changes some bits in the message
    }
    public void CheckSum() {
        //checks for errors in the message that could have been modified 
    }

} // UDPClient




class UDPServer {

   public static void main(String args[]) throws Exception {
            
      DatagramSocket serverSocket = new DatagramSocket(10014);
            
      byte[] receiveData = new byte[256];
      byte[] sendData  = new byte[256];
            
      while (true) {
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          serverSocket.receive(receivePacket);
          String sentence = new String(receivePacket.getData());
          
          InetAddress IPAddress = receivePacket.getAddress();
          
          int port = receivePacket.getPort();
          
          String capitalizedSentence = sentence.toUpperCase();
          sendData = capitalizedSentence.getBytes();
          
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
          
          serverSocket.send(sendPacket);
      } // while 
   } // main()


    public void Gremlin() {
        
    }
    
    public void CheckSum() {
        
    }
} // UDPServer



