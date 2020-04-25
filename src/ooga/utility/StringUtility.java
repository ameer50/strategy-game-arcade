package ooga.utility;

import ooga.player.CPUPlayer;

public class StringUtility {

    public StringUtility() {
    }

    // TODO: Move this to a different class.
    public String strategyToString(CPUPlayer.StrategyType strategy) {
        String[] arr = strategy.toString().toLowerCase().split("_");
        String ret = "";
        for (String word : arr) {
            ret += capitalize(word);
        }
        return ret;
    }

    // TODO: Move this to a different class.
    public String capitalize(String word) {
        String uppercase = word.substring(0, 1).toUpperCase();
        String lowercase = word.substring(1);
        return String.format("%s%s", uppercase, lowercase);
    }
}
