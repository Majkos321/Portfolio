import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Serwer {

    ConcurrentHashMap<Integer, Uzytkownik> Register_Users = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, ObjectOutputStream> Online_Users = new ConcurrentHashMap<>();
    public ArrayList<Rang> konwersacje = new ArrayList<>();
     String line;
     Integer ID;
     Integer IDK;


    public static void main(String[] args) throws Exception {//przy serwer socket moze wystapic IOEXCEPTION

        Serwer serwer = new Serwer();
        //serwer.ControList(serwer.konwersacje);

        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server czeka na połaczenie....");

        while (true) {
            Socket socket = server.accept(); //gdy natrafiamy na ta linike Program sie czeka na sygnał od jakiegos innego programu ,procesu
             // new Socket("localhost",5000);C
            System.out.println("Klient sie połączył");
            Thread t= new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serwer.Customer_Service(socket);
                    } catch (Exception e) {
                            throw new RuntimeException(e);
                    }
                }
            });
            t.start();
        }
    }

    void savelogs(Integer ID, String massage, Integer KlientID) throws Exception {

        String file = this.everwrite(ID, KlientID);
        if (file == null) {
            throw new IOException();
        }
        try (FileWriter FW = new FileWriter(file, true);//Potem tworzywmy indywidual chaty do kazdego uzytkwonika
             BufferedWriter out = new BufferedWriter(FW)) {

            out.write("ID" + ID + ": " + massage);
            out.newLine();
        } catch (IOException e) {
            System.out.println("Błąd " + e.getMessage());
        }
    }

    String everwrite(Integer ID1, Integer ID2) throws ChatInitializationException {//logika tworzenia nowych kownersacji

        int id_min = ID1;
        int id_max = ID2;

        if (id_min > id_max) {
            int temp = id_max;
            id_max = id_min;
            id_min = temp;
        }

        if (konwersacje.isEmpty()) {
            Rang chat1 = new Rang(id_min, id_max);
            chat1.setName(id_min + "Chat" + id_max);
            konwersacje.add(chat1);
            System.out.println("To pierwsza wiadomosc do tego uzytkownika");
            try (FileWriter Fw = new FileWriter(id_min + "Chat" + id_max)) {
                return id_min + "Chat" + id_max;
            } catch (IOException e) {
                System.out.println("Nie udalo sie utworzyc pliku jestesmy w Serwer");
            }
        } else {

            for (Rang g : konwersacje) {
                if (g.name.equals(id_min + "Chat" + id_max)) {
                    return id_min + "Chat" + id_max;
                }
            }
            System.out.println("To pierwsza wiadomosc do tego uzytkownika");
            try (FileWriter Fw = new FileWriter(id_min + "Chat" + id_max, true)) {
                Rang r = new Rang(id_min, id_max);
                r.setName(id_min + "Chat" + id_max);
                konwersacje.add(r);
                return id_min + "Chat" + id_max;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            /**
             * mamy tak 1.Brak czego kolwiek napewno musimy utworzyc pod warunkiem ze to nie jest ta sama osoba
             * 2.teraz tak jesli cos mamy i nie sa to te dwa same ID to mozemy miec nazwe niestety moga bc one tworzone roznie ale no
             * jak znjadziemy to zwracamy nazwe ktora jest  bo napewno tak sie nazywa
             */


        }
        return null;
    }

    public Boolean ifChat(Integer ID_1, Integer ID_2) {

        Integer id_min = ID_1;
        Integer id_max = ID_2;
        if (id_min > id_max) {
            int temp = id_max;
            id_max = id_min;
            id_min = temp;
        }
        for (Rang g : konwersacje) {
            if (g.name.equals(id_min + "Chat" + id_max)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> FindChat(Integer ID_1, Integer ID_2) {

        String name_Chat = ID_1 + "Chat" + ID_2;
        ArrayList<String> chat = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(name_Chat))) {
            String line;
            while ((line = bf.readLine()) != null) {

                chat.add(line);

            }
            return chat;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void ControList(ArrayList<Rang> list) {

        if (list.isEmpty()) {
            System.out.println("Lista jest pusta");
        }
        for (Rang r : list) {
            System.out.println(r.name);
        }
    }


    private void Customer_Service(Socket socket ) throws Exception {

        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            /**
             * czemu nie uporzadkuje globalnie ktory jest mniejszy
             * a ktory wiekszy ID popniewaz moze to potem mi sie pogubic
             * narazie latwo mi sledziec co od kogo przyszlo ale nie zawsze
             * musi tak byc !!!
             */
            while (true) {
                try {
                    Wiadomosc wiad = (Wiadomosc) in.readObject();

                    String localline = wiad.mess; //trzeba uwaza
                    Integer localID = wiad.IDS;
                    Integer localIDK = wiad.klient;
                    Integer localoperation = wiad.operacja;

                    if (localID.equals(localIDK)) {
                        throw new ChatInitializationException("BŁĄD: Użytkownik " + localID + " próbował pisać do samego siebie!");
                    }

                    if (localline.equals("END") & localoperation == 0) {
                        System.out.println("");
                        socket.close();
                    }

                    if (localoperation == 1) {
                        this.savelogs(localID, localline, localIDK);//wysylanie info o chacie
                        if (localline.equals("END")) {
                            Server_log("Proces Serwera został zakończony");
                            socket.close();
                        }
                        if (localline != null) {


                            if (Online_Users.get(localIDK) != null) {
                                ObjectOutputStream messeg = Online_Users.get(localIDK);
                                messeg.writeObject(localline);
                                messeg.flush();
                            }

                            Server_log(localline);

                            System.out.println("Obsłuzono kliencta czekam na nastepnego ");
                        }
                    } else if (localoperation == 2) {
                        if (localID > localIDK) {
                            int temp = localID;
                            localID = localIDK;
                            localIDK = temp;
                        }
                        ArrayList<String> histry_chat = new ArrayList<>();
                        if (this.ifChat(localID, localIDK)) {
                            histry_chat = this.FindChat(localID, localIDK);
                        } else {
                            histry_chat.add("Przykro mi konwersacja nie istnieje");
                        }
                        out.writeObject(histry_chat);
                        out.flush();
                        out.close();

                    } else if (localoperation == -1) {//wylogowywanie
                        boolean logg_in = true;

                        Uzytkownik user = Register_Users.get(localID);//tutaj sprawdzamy  czy w ogole istnieje

                        if (user == null) {
                            logg_in = false;
                            out.writeObject("Wybrany użytkownik nie istnieje");
                            out.flush();
                            return;
                        }

                        ObjectOutputStream u = Online_Users.get(localID);//tutaj obslugujemy aktualny stan
                        if (u == null) {
                            logg_in = false;
                            out.writeObject("Jesteś już wylogowany!!!");
                            out.flush();
                            return;
                        }

                        if (logg_in) {
                            Online_Users.remove(localID);
                            out.writeObject("Wylogowanie poszło pomyślnie :) ");
                            out.flush();
                        }

                    } else if (localoperation == 0) { //logowanie


                        Uzytkownik user = Register_Users.get(localID);

                        if (user == null) {
                            out.writeObject("Tworzymy uzytkowniak");
                            out.flush();
                            Register_Users.put(localID, new Uzytkownik(localID, wiad.user.Name));
                            Online_Users.put(localID, out);
                        } else {
                            out.writeObject("Witaj ponownie:" + wiad.user.Name);
                            out.flush();
                            Online_Users.put(localID, out);
                        }
                    } else {
                        throw new NotFoundOperationException("Uzytkownik wybral zla operacje");
                    }

                } catch (EOFException e) {
                    System.err.println(e);//
                }catch (SocketException e){
                    socket.close();
                }
            }

        }catch(IOException e){
            System.err.println("Nie udalo sie otworzyc strumienia");
        }
    }




    static void Server_log(String log){
        System.out.println(log);
    }

}




// Wyjątki
class ChatInitializationException extends Exception {
    public ChatInitializationException(String message) {
        super(message);
    }
}

class NotFoundOperationException extends Exception {
    public NotFoundOperationException(String message) {
        super(message);
    }
}

