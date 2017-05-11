import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
    private JTextArea displayArea;
    private DatagramSocket socket;
    
    //set GUI and DatagramSocket
    public Client(){
        super( "Client" );
        
        displayArea = new JTextArea();
        getContentPane().add(new JScrollPane(displayArea), BorderLayout.CENTER);
        setSize(400, 300);
        setVisible(true);
        
        //create DatagramSocket for sending and receiving packets
        try {
            socket = new DatagramSocket( 5000 );
        } catch(SocketException socketException){
            socketException.printStackTrace();
            System.exit(1);
        }
    }//end server constructor
    //wait for packets to arrive, display data and echo packet to server
    private void waitForPackets(){
        while (true){
            try {
                //setup packet
                byte data[] = new byte [100];
                DatagramPacket receivePacket = new DatagramPacket (data, data.length);
                socket.receive(receivePacket);
                
                //display information from receive packet
                displayMessage("\nPacket received:" +
                        "\nFrom host: " + receivePacket.getAddress() +
                        "\nHost port: " + receivePacket.getPort() +
                        "\nLength: " + receivePacket.getLength() +
                        "\nContaining:\n\t" + new String (receivePacket.getData(),
                        0, receivePacket.getLength()));
                
                //Create new packet and send it to Server
                sendPacketToServer(receivePacket);
                
            } catch(IOException ioException){
                displayMessage(ioException.toString() + "\n");
                ioException.printStackTrace();
            }
        }//end while
    }//end method waitForPackets
    
    //echo packet to server
    private void sendPacketToServer(DatagramPacket receivePacket) throws IOException {
        displayMessage("\n\nEcho data to Server...");
        String chData ="";
        
        String Data = new String (receivePacket.getData());
        
        //Reverse string Data
        for (int x = receivePacket.getLength(); x >= 0; x--){
            char chr = Data.charAt(x);
            chData += chr;
        }
        chData = chData.trim();
        
        //Convert string to char array
        char[] chArray = chData.toCharArray();

        //convert char to byte array
        byte data[] = new String(chArray).getBytes("UTF-8");
        
        //create packet to send
        DatagramPacket sendPacket = new DatagramPacket(
        data, data.length, receivePacket.getAddress(), receivePacket.getPort());
             
        socket.send(sendPacket);//send packet
        displayMessage ("Packet sent\n");
    }//end echo packet to server
    
    //utility method called from other threads to manipulate
    //displayArea in the event-dispatch thread
    private void displayMessage(final String messageToDisplay) {
        //display message form event-dispatch thread of execution
        SwingUtilities.invokeLater (new Runnable(){//inner class ot ensure GUI updates properly
            public void run()//update displayArea
            {
                displayArea.append(messageToDisplay);
                displayArea.setCaretPosition(displayArea.getText().length());
            }
        }//end inner class
        );//end call to SwingUtilities.invokeLater
    }
    
    public static void main(String args[]){
        Client application = new Client();
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.waitForPackets();
    }
}//end class Client
