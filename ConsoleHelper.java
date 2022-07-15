package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
   private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
   public static  void writeMessage(String message) {
       System.out.println(message);
   }
   public static String readString()  throws IOException {
       String result = null;
       while (result == null){
       try {
           result = bufferedReader.readLine();
       } catch (IOException e) {
           System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
           result = null;
       }
       }
       return result;
   }
   public static <e> int readInt(){
       int result = 0;
      NumberFormatException a = null;
       do {
           a = null;
           try {
               result = Integer.parseInt(readString());

           } catch (NumberFormatException e) {
               a = e;
               System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
               result = 0;
           }catch (IOException e){   }
       } while (a != null);
       return result;
   }
}
