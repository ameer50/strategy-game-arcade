package ooga.utility;

import ooga.player.CPUPlayer;

public class StringUtility {

    public String strategyToString(CPUPlayer.StrategyType strategy) {
        String[] arr = strategy.toString().toLowerCase().split("_");
        String ret = "";
        for (String word : arr) {
            ret += capitalize(word);
        }
        return ret;
    }

    public String capitalize(String word) {
        String uppercase = word.substring(0, 1).toUpperCase();
        String lowercase = word.substring(1);
        return String.format("%s%s", uppercase, lowercase);
    }
}
