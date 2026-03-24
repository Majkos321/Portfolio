import org.w3c.dom.ranges.Range;

import java.util.List;

public class Rang {
    Integer ID1,ID2;
    String name;

    Rang(Integer ID1,Integer ID2){
        this.ID1 = ID1;
        this.ID2 = ID2;
    }

    public Integer getID1() {
        return ID1;//
    }
    public Integer getID2() {
        return ID2;
    }

    public void setName(String name) {
        this.name = name;
    }


}
