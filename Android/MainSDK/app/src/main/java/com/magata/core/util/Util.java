package com.magata.core.util;

import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Util {
    private static String TAG = "Util";

    public static int getRContent(String packageName, String className, String resouceName) {
        try {
            Class<?> resource = Class.forName(packageName + ".R");
            Log.d(TAG, "getRContent: " + packageName);

            Class<?>[] classes = resource.getClasses();
            for (Class<?> c : classes) {
                int i = c.getModifiers();
                String name = c.getName();
                Log.d(TAG, "getRContent: " + name);
                String s = Modifier.toString(i);
                if (s.contains("static") && name.contains(className)) {
                    Field intField = c.getField(resouceName);
                    return intField.getInt(c);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
            Log.d(TAG, "getRContent: 没找到" + resouceName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
