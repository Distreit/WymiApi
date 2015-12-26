package com.hak.wymi.utility.passwordstrength;

import java.util.HashMap;
import java.util.Map;

public class PasswordStrengthResult {
    public Integer result = 0;
    public Map<String, Integer> bonuses = new HashMap<>();
    public Map<String, Integer> penalties = new HashMap<>();

    public Integer getTotal() {
        Integer result = this.result;

        for (Integer bonus : this.bonuses.values()) {
            result += bonus;
        }

        for (Integer penalty : this.penalties.values()) {
            result -= penalty;
        }

        return result;
    }

    public Integer getFinalResult() {
        return Math.max(0, Math.min(100, getTotal()));
    }
}
