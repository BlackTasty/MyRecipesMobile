package com.tastyapps.myrecipesmobile.core.recipes;

public enum IngredientCategory {
    All(-1),
    Unset(0),
    Vegetables(1),
    Fruits(2),
    Nuts(3),
    Poultry(5),
    Beef(6),
    Pork(7),
    Venison(8),
    Lamb(9),
    Meat(10),
    Sausages(11),
    Fish(12),
    Pasta(13),
    Rice(14),
    Dairy(15),
    Herbs(21),
    Spices(22),
    Seeds(23),
    Liquid(30),
    Alcohol(31),
    Sauces(35),
    Creme(36),
    Bread(40),
    Flour(41),
    Powder(42),
    Oil(43),
    Other(200);

    private int numVal;

    IngredientCategory(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
