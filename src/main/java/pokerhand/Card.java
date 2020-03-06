package pokerhand;

public class Card {
    String value;
    String suit;
    int numberValue;

    Card(String card) {
        this.value = card.substring(0, 1);
        this.suit = card.substring(1, 2);
        this.numberValue = CardFaceToValue.getNumberValue(value);
    }

    public String toString() {
        return this.value + this.suit;
    }
}