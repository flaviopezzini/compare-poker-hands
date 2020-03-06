package pokerhand;

import java.util.HashMap;

public class CardFaceToValue {
    private static HashMap<String, Integer> faceToValue = new HashMap<>();

    static {
        faceToValue.put("A", 14);
        faceToValue.put("K", 13);
        faceToValue.put("Q", 12);
        faceToValue.put("J", 11);
        faceToValue.put("T", 10);
        faceToValue.put("9", 9);
        faceToValue.put("8", 8);
        faceToValue.put("7", 7);
        faceToValue.put("6", 6);
        faceToValue.put("5", 5);
        faceToValue.put("4", 4);
        faceToValue.put("3", 3);
        faceToValue.put("2", 2);
    }

    public static int getNumberValue(String faceValue) {
        return faceToValue.get(faceValue);
    }
}
