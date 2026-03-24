import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Wiadomosc implements Serializable {


    Integer operacja;//1 to wiadomosc
    Integer IDS;
    String mess;
    Integer klient;
    Uzytkownik user;

    Wiadomosc(Integer IDS,Integer klient , Integer operacja,String mess,Uzytkownik user) {
        this.operacja = operacja;
        this.IDS = IDS;
        this.mess = mess;
        this.klient = klient;
        this.user = user;
    }

    public void wysli(){

        try(Socket socket = new Socket("localhost", 5000);){
            OutputStream OS = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(OS);// do wysylania objektow slozy ObjectOutputStream
            oos.writeObject(this);
            oos.flush();// flush sluzy do wypchniecia bufora

                if(operacja.equals(2)){ //jesli chcemy odczytac wiadomosci to  2
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); //czekanie na dane od serwer
                    ArrayList<String> historia = (ArrayList<String>) in.readObject();
                    System.out.println("Historia chatu z uzytkownikiem" + klient);
                    for(String h :  historia){
                        System.out.println(h);
                    }
                }

                if(operacja.equals(0)){ //odpowiada za logowanie sie
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Logowanie sie do Serwera...");
                    String output = (String)in.readObject();
                    System.out.println(output);
                    while(true){
                        String output1 = (String)in.readObject();
                        System.out.println("Nowa wiadomosc" + output1);
                        if("END".equals(output1)){
                            break;
                        }
                    }


                }
                if(operacja.equals(-1)){
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Wylogowywanie sie z Serwera...");
                    String output = (String)in.readObject();
                    System.out.println(output);
                }
        }catch(Exception e){
            System.out.println("Bład Wiadomosc");
        }
    }

}







