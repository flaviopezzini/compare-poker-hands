package pokerhand;

public enum HandType {
    STRAIGHT_FLUSH(9), FOUR_OF_A_KIND(8), FULL_HOUSE(7), FLUSH(6), STRAIGHT(5), THREE_OF_A_KIND(4), TWO_PAIR(
            3), ONE_PAIR(2), HIGH_CARD(1);

    private int ranking;

    HandType(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return ranking;
    }
}