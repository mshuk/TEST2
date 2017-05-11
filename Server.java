import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
    private JTextField enterField;
    private JTextArea displayArea;
    private DatagramSocket socket;
    
    //setup GUI and DatagramSocket
    public Server(){
        super( "Server");
        Container container = getContentPane();
        enterField = new JTextField("Type message here");
        enterField.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent event){
                //create and send packet
                try {
                    displayArea.append("\nSending packet containing: " +
                            event.getActionCommand() + "\n");
                    
                    //get message form textfield and convert to byte array
                    String message = event.getActionCommand();
                    System.out.println(message);
                    char[] ch = message.toCharArray();
                        for(int i=0;i<ch.length;i++){  
                            System.out.print(ch[i]);  
                        } 
                    //convert char to byte array        
                    byte data[] = new String(ch).getBytes("UTF-8");
                    
                    //create sendPacket
                    DatagramPacket sendPacket = new DatagramPacket(data,
                    data.length,InetAddress.getLocalHost(),5000);
                    System.out.println(InetAddress.getLocalHost());
                    
                    socket.send(sendPacket);//send packet
                    displayArea.append("packet sent\n");
                    displayArea.setCaretPosition(displayArea.getText().length());
                        
                }catch(IOException ioException){
                    displayMessage(ioException.toString() + "\n");
                    ioException.printStackTrace();
                }
            }//end actonPerformed
        }//end inner class
        );//end call to addActionListerner
       
        container.add(enterField, BorderLayout.NORTH);
        
        displayArea = new JTextArea();
        container.add(new JScrollPane(displayArea),BorderLayout.CENTER);
        setSize(400,300);
        setVisible(true);
        
        //create DatagramSocket for sending and receiving packets
        try {
            socket = new DatagramSocket();
        } catch (SocketException socketException){
            socketException.printStackTrace();
            System.exit(1);
        }   
    }//end Client constructor
    
    //wait for packets to arrive from Client, display packet contents
    private void waitForPackets(){
        while (true){//loop forever
            //receive packet and display contents
            try {
                //setup packet
                byte data[] = new byte[100];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);
                
                socket.receive(receivePacket);//wait for packet
                
                //display packet contents
                displayMessage("\nPacket received:" +
                        "\nFrom host: " + receivePacket.getAddress() +
                        "\nHost port: " + receivePacket.getPort() +
                        "\nLength: " + receivePacket.getLength() +
                        "\nContaining:\n\t" + new String(receivePacket.getData(),
                        0, receivePacket.getLength()));
            } catch (IOException exception){
                displayMessage(exception.toString() + "\n");
                exception.printStackTrace();
            }
        }//end while
    }//end method waitForPackets
    
    //utility method called form ohter threads to manipulate
    //displayArea in the even-dispatch thread
    private void displayMessage(final String messageToDisplay){
        //display message form even-dispatch thread of execution
        SwingUtilities.invokeLater(new Runnable(){//inner class to ensure GUI updates properly
            public void run()//updates displayArea
            {
                displayArea.append(messageToDisplay);
                displayArea.setCaretPosition(displayArea.getText().length());
            }      
        }//end inner class
        );//end call to SwingUtilities.invokeLater
    }
    
    public static void main(String args[]){
        Server application = new Server();
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.waitForPackets();
    }
}//end class Server
