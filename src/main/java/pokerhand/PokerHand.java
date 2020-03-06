package pokerhand;

import java.util.*;

/**
 * Make it possible to compare two hands of Poker
 */
public class PokerHand {

    private List<Card> sortedCards = new ArrayList<>();
    private HandType handType;

    private int kindValueInFourOfAKind;
    private int kindValueInThreeOfAKind;
    private Integer highPairValue;
    private Integer lowPairValue;

    /**
     * @param hand
     *            Hand is a string containing the 5 cards separated by spaces. <br/>
     *            Ex: 2S 5D 2D 7H TS
     */
    public PokerHand(String hand) {
        if (hand == null || hand.length() != 14) {
            throw new IllegalArgumentException("Invalid Hand");
        }
        String[] stringCards = hand.split(" ");
        if (stringCards.length != 5) {
            throw new IllegalArgumentException("Must be 5 cards");
        }
        TreeMap<Integer, ArrayList<Card>> temporarySortedCards = new TreeMap<>();
        for (int i = 0; i < stringCards.length; i++) {
            Card newCard = new Card(stringCards[i]);
            int value = newCard.numberValue;
            ArrayList<Card> cardList = temporarySortedCards.get(value);
            if (cardList == null) {
                cardList = new ArrayList<>(0);
            }
            cardList.add(newCard);
            temporarySortedCards.put(value, cardList);
        }
        for (Integer key : temporarySortedCards.keySet()) {
            ArrayList<Card> cardList = temporarySortedCards.get(key);
            for (Card card : cardList) {
                this.sortedCards.add(card);
            }
        }
        this.handType = retrieveHandType();
    }

    private HandType retrieveHandType() {
        HashMap<String, Integer> kindCountMap = new HashMap<>();
        HashMap<String, ArrayList<Card>> kindMap = new HashMap<>();
        String previousSuit = "";
        int suitCounter = 0;
        //
        int previousValue = -1;
        int straightCounter = 1;
        for (Card card : sortedCards) {
            if (previousSuit.isEmpty()) {
                previousSuit = card.suit;
            }
            if (card.suit.equals(previousSuit)) {
                suitCounter++;
            }
            if (previousValue == -1) {
                previousValue = card.numberValue;
            } else {
                if (card.numberValue == (previousValue + 1)) {
                    previousValue = card.numberValue;
                    straightCounter++;
                }
            }
            Integer kindCount = kindCountMap.get(card.value);
            if (kindCount == null) {
                kindCount = 0;
            }
            ArrayList<Card> kindList = kindMap.get(card.value);
            if (kindList == null) {
                kindList = new ArrayList<>(0);
            }
            kindList.add(card);
            kindCount++;
            kindCountMap.put(card.value, kindCount);
            kindMap.put(card.value, kindList);
        }
        boolean isFlush = suitCounter == 5;
        boolean isStraight = straightCounter == 5;
        if (isFlush) {
            if (isStraight) {
                return HandType.STRAIGHT_FLUSH;
            } else {
                return HandType.FLUSH;
            }
        }
        if (isStraight) {
            return HandType.STRAIGHT;
        }
        boolean hasThree = false;
        int pairCount = 0;
        Set<Integer> pairKind = new TreeSet<>();
        for (String kind : kindCountMap.keySet()) {
            Integer value = kindCountMap.get(kind);
            int kindValue = CardFaceToValue.getNumberValue(kind);
            if (value == 4) {
                this.kindValueInFourOfAKind = kindValue;
                return HandType.FOUR_OF_A_KIND;
            }
            if (value == 3) {
                this.kindValueInThreeOfAKind = kindValue;
                hasThree = true;
            }
            if (value == 2) {
                pairKind.add(kindValue);
                pairCount++;
            }
        }
        if (hasThree && pairCount == 1) {
            return HandType.FULL_HOUSE;
        }
        if (hasThree) {
            return HandType.THREE_OF_A_KIND;
        }
        if (pairCount == 2) {
            int pos = 0;
            for (Integer kindValue : pairKind) {
                if (pos == 0) {
                    this.highPairValue = kindValue;
                } else {
                    this.lowPairValue = kindValue;
                }
                pos++;
            }
            return HandType.TWO_PAIR;
        }
        if (pairCount == 1) {
            for (Integer kindValue : pairKind) {
                this.highPairValue = kindValue;
            }
            this.lowPairValue = -1;
            return HandType.ONE_PAIR;
        }
        return HandType.HIGH_CARD;
    }

    private Result compareHighCardValue(PokerHand hand) {
        for (int i = sortedCards.size() - 1; i >= 0; i--) {
            Card ourCard = this.sortedCards.get(i);
            Card theirCard = hand.sortedCards.get(i);
            if (ourCard.numberValue > theirCard.numberValue) {
                return Result.WIN;
            } else if (ourCard.numberValue < theirCard.numberValue) {
                return Result.LOSS;
            }
        }
        return Result.TIE;

    }

    public Result compareWith(PokerHand hand) {
        Integer ourRanking = this.getHandType().getRanking();
        Integer theirRanking = hand.getHandType().getRanking();
        int compared = ourRanking.compareTo(theirRanking);
        if (compared > 0) {
            return Result.WIN;
        } else if (compared < 0) {
            return Result.LOSS;
        } else {
            // handle ties
            if (HandType.STRAIGHT_FLUSH.equals(this.handType) || HandType.FLUSH.equals(this.handType)
                    || HandType.STRAIGHT.equals(this.handType) || HandType.HIGH_CARD.equals(this.handType)) {
                return compareHighCardValue(hand);
            }
            if (HandType.FOUR_OF_A_KIND.equals(this.handType)) {
                int ourValue = this.kindValueInFourOfAKind;
                int theirValue = hand.kindValueInFourOfAKind;
                if (ourValue > theirValue) {
                    return Result.WIN;
                } else if (ourValue < theirValue) {
                    return Result.LOSS;
                } else {
                    return compareHighCardValue(hand);
                }
            }
            if (HandType.FULL_HOUSE.equals(this.handType) || HandType.THREE_OF_A_KIND.equals(this.handType)) {
                int ourValue = this.kindValueInThreeOfAKind;
                int theirValue = hand.kindValueInThreeOfAKind;
                if (ourValue > theirValue) {
                    return Result.WIN;
                } else if (ourValue < theirValue) {
                    return Result.LOSS;
                } else {
                    return compareHighCardValue(hand);
                }
            }
            if (HandType.TWO_PAIR.equals(this.handType)) {
                int compare = this.highPairValue.compareTo(hand.highPairValue);
                if (compare > 0) {
                    return Result.WIN;
                } else if (compare < 0) {
                    return Result.LOSS;
                } else {
                    // compare low pair
                    compare = this.lowPairValue.compareTo(hand.lowPairValue);
                    if (compare > 0) {
                        return Result.WIN;
                    } else if (compare < 0) {
                        return Result.LOSS;
                    } else {
                        return compareHighCardValue(hand);
                    }
                }
            }
            if (HandType.ONE_PAIR.equals(this.handType)) {
                int compare = this.highPairValue.compareTo(hand.highPairValue);
                if (compare > 0) {
                    return Result.WIN;
                } else if (compare < 0) {
                    return Result.LOSS;
                } else {
                    return compareHighCardValue(hand);
                }
            }
            return Result.TIE;
        }
    }

    protected HandType getHandType() {
        return handType;
    }
}