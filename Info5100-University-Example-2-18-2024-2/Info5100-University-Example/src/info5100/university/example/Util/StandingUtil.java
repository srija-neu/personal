/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Util;

/**
 *
 * @author Srija
 */
public class StandingUtil {
    public static String academicStanding(double termGpa, double overallGpa){
        if (overallGpa < 3.0) return "Academic Probation";
        if (termGpa < 3.0) return "Academic Warning";
        return "Good Standing";
    }
}
