package ProgFunkcyjne.InterfaceFunkcuyjny.Projekt;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Producer {

    final List<String> cities;
    Supplier<List<String>> citiesSupplier;
    Map<String,Integer> types;

    public Producer(List<String> cities, List<String> types, HashMap<String,Integer> productsTypes) {
        this.cities = cities;
        this.types = productsTypes;
        this.citiesSupplier = () -> new ArrayList<String>(this.cities);
    }

    Consumer<String> doYouHave = (Product) -> {

        Integer aum = types.get(Product);
        if(aum != null){
            System.out.println("Yes we have");
        }

    };

    Function<String, Integer> checkProductQuantity = (productName) -> {

        return types.get(productName);

    };

}
