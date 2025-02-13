package org.example.utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONUtility {

    public static <T> List<T> readJSON(String path , Class<T> clazz){
        try {
            String content=new String(Files.readAllBytes(Paths.get(path)));
            JSONArray jarr= new JSONArray (content);
            return fromJsonArray(jarr,clazz);

        }
        catch(Exception e){
            System.out.println("File not found");
            return null;
        }
    }


    public static <T> List<T> fromJsonArray(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();

        try {
            // Iterate through the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Create a new instance of the POJO class
                T obj = clazz.getDeclaredConstructor().newInstance();

                // Iterate through the fields of the POJO class
                for ( Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true); // Allow access to private fields

                    // Check if the field exists in the JSON object
                    if (jsonObject.has(field.getName())) {
                        Object value = jsonObject.get(field.getName());

                        // Set the field value using reflection
                        field.set(obj, value);
                    }
                }

                // Add the POJO to the list
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
