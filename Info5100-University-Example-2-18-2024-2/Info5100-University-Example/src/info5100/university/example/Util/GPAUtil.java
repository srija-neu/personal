/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Util;

import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author Srija
 */
public class GPAUtil {
    private static final Map<String, Double> points = new HashMap<>();

    static {
        points.put("A", 4.0);
        points.put("A-", 3.7);
        points.put("B+", 3.3);
        points.put("B", 3.0);
        points.put("B-", 2.7);
        points.put("C+", 2.3);
        points.put("C", 2.0);
        points.put("C-", 1.7);
        points.put("F", 0.0);
    }

    public static double pointsFor(String letter){
        Double v = points.get(letter);
        return v == null ? 0.0 : v;
    }
}
