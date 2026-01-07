package com.devapps.affiliate.models;

import java.util.ArrayList;

public class VerticalModel {

    String Categories;
    ArrayList<HorizontalModel> arrayList;

    public VerticalModel() {
    }

    public VerticalModel(String categories, ArrayList<HorizontalModel> arrayList) {
        Categories = categories;
        this.arrayList = arrayList;
    }

    public String getCategories() {
        return Categories;
    }

    public void setCategories(String categories) {
        Categories = categories;
    }

    public ArrayList<HorizontalModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HorizontalModel> arrayList) {
        this.arrayList = arrayList;
    }
}
