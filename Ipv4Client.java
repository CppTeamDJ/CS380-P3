/************************************************************************************
 *	file: Ipv4Client.java
 *	author: Daniel Spencer, Jack Zhang
 *	class: CS 380 - computer networks
 *
 *	assignment: Project3
 *	date last modified: 10/26/2017
 *
 *	purpose:  Implementing IPv4. Will be sending packets to the server and if correct
 *              the server will respond "good".
 *
 ************************************************************************************/
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.io.BufferedReader;

public class Ipv4Client {
    public static void main(String[] args)throws IOException{
        byte[] packet ;
        int dataLength = 2;
        try (Socket socket = new Socket("18.221.102.182", 38003)) {
            System.out.println("Connected to server.");
            OutputStream out = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            for (int k = 0 ; k < 12 ; k++){
                short length = (short)(20 + dataLength);
                packet = new byte[length];
                System.out.println("Data Length: " + dataLength);
                // Version+ HLen 5 X 32 bits = 160bits = 20 bytes
                packet[0]= 0b01000101;
                // DSCP/ECN DNI
                packet[1]= 0;
                // Total Length
                packet[2]= (byte)((packet.length & 0xFF00)>>>8);
                packet[3]= (byte)(packet.length & 0xFF);
                //Identification
                packet[4]= 0;
                packet[5]= 0;
                //Flags
                packet[6] = 0x40;
                //Fragment OFFSET
                packet[7]= 0;
                //Time To Live
                packet[8]= 50;
                //Protocol
                packet[9]= 6;
                //Source Address (random numbers)
                packet[12]= 0x6A;
                packet[13]= 0X4f;
                packet[14]= 0x40;
                packet[15]= 0X5b;
                //Desination Adress
                packet[16] = (byte) 18;
                packet[17] = (byte) 221;
                packet[18] = (byte) 102;
                packet[19] = (byte) 182;
                short checksum = checksum(packet);
                packet[10]= (byte)((checksum & 0xff00)>>>8);
                packet[11]= (byte)(checksum & 0xff);

                for (int i = 0; i < packet.length; i++)
                    out.write(packet[i]);

                System.out.println( in.readLine()+"\n");
                dataLength *= 2;
            }
        }
    }
    public static short checksum(byte[] bytes) {
        int length = bytes.length;
        int index = 0;
        long sum = 0;
        while (length > 1) {
            sum += (((bytes[index]<<8) & 0xFF00) | ((bytes[index + 1]) & 0xFF));
            if ((sum & 0xFFFF0000) > 0){
                sum = sum & 0xFFFF;
                sum += 1;
            }
            index += 2;
            length -= 2;
        }
        if (length > 0) {
            sum += (bytes[index]<<8 & 0xFF00);
            if ((sum & 0xFFFF0000) > 0){
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }
        sum = sum & 0xFFFF;
        return (short)~sum;
    }
}