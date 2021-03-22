package test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestestGerais implements Comparator<String>{

        public int compare(String s1, String s2) {
          return s2.length() - s1.length();
        }
      public static void main(String[] args) {

          String input = "Hola. Luna! Sansa, test!.";
          Map<String, String> conversion = new HashMap<>();
          conversion.put(".", ". ");
          conversion.put(",", ", ");
          conversion.put("!", "!  ");

          List<String> forbidden = Arrays.asList(".", "!", ",");

          String[] list = input.split(" ");
          for (String string : list) {
            
          }
          }
      }
      
}
