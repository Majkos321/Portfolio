package ProgFunkcyjne.InterfaceFunkcuyjny.Projekt;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MyConsumer<T, R> {

    public void processConsumer(Consumer<T> consumer, T arg) {
        consumer.accept(arg);
        System.out.println("Consumer zakończył pracę.");
    }

    public void processSupplier(Supplier<T> supplier) {
        T result = supplier.get();
        System.out.println("Wartość z Suppliera to: " + result);
    }

    public void processFunction(Function<T, R> function, T arg) {
        R result = function.apply(arg);
        System.out.println("Wartość z Function to: " + result);
    }
}