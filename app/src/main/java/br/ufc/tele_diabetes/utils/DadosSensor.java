package br.ufc.tele_diabetes.utils;

/**
 * Created by robertcabral on 8/27/17.
 */

public class DadosSensor {
    int id, value;

    public DadosSensor() {}

    public DadosSensor(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
