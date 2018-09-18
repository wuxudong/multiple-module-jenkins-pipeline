package com.github.wuxudong.pipeline.utils

class TernaryUtils {

    public static String getOrDefault(value, defaultValue) {
    	System.out.println("value " + value + "defaultValue " + defaultValue)
        return value ? value : defaultValue
    }

}