package me.snorochevskiy.vault.client.lib;

public class Tup2<T1,T2> {

    private T1 v1;
    private T2 v2;

    public static <T1,T2> Tup2<T1,T2> of(T1 v1, T2 v2) {
        return new Tup2<>(v1, v2);
    }

    public Tup2(T1 v1, T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public T1 _1() {
        return v1;
    }

    public T2 _2() {
        return v2;
    }
}
