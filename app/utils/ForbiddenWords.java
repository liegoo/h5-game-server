package utils;

import jws.Jws;

public class ForbiddenWords {
    private static String[] keywords = null;
    static{
        String strWords = Jws.configuration.getProperty("forbidden.keywords", "");
        keywords = strWords.split(",");
    }
    
    public static String filter(String word){
        /*if (word == null || word.equals("")){
            return word;
        }
        
        for (String keyword : keywords){
            if (word.toLowerCase().indexOf(word.toLowerCase()) != -1){
                word = word.replaceAll("(?i)" + keyword, "**");
            }
        }*/
        
        return word;
    }
}
