package br.ufc.tele_diabetes.utils;

/**
 * Created by robertcabral on 8/24/17.
 */

public class ItemDashboard {
    String name;
    String flag;

    public ItemDashboard(String name, String flag) {
        this.name = name;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }


    @Override
    public String toString(){
        return this.name;
    }
}
