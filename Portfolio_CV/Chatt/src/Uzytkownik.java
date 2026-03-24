import java.io.ObjectOutputStream;
import java.io.Serializable;

    public class Uzytkownik implements Serializable {

        private static final long serialVersionUID = 1L;
        private Integer ID;
        String Name;
        private Integer quantity;
        private transient Integer pass; //transient powduje brak serializacji

        Uzytkownik(Integer ID, String Name) {
            this.ID = ID;
            this.Name = Name;
        }

        Integer getID(){
            return ID;
        }
        String getName(){
            return Name;
        }
        Integer getQuantity(){
            return quantity;
        }


        void zapisz(){
            //ObjectOutputStream Save =
        }

        public static void main(String[] args) {
            Uzytkownik jasiek =  new Uzytkownik(6,"Jasiek");
            Uzytkownik zdzis =  new Uzytkownik(10,"Zdzis");

//            Wiadomosc wiadomosc = new Wiadomosc(6,10,0,"Siemka",jasiek);
//            wiadomosc.wysli();
//           Wiadomosc wiadomosc1 = new Wiadomosc(10,6,0,"Siemka",zdzis);
//           wiadomosc1.wysli();
          Wiadomosc wiadomosc2 = new Wiadomosc(6,10,0,"Siemka",zdzis);
           wiadomosc2.wysli();


        }
}
