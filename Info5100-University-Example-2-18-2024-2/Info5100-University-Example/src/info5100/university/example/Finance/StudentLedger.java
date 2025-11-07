/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Srija
 */
public class StudentLedger {
    private Map<String, Double> balances = new HashMap<>();
    private Map<String, List<Payment>> history = new HashMap<>();

    public double getBalance(String username){
        return balances.getOrDefault(username, 0.0);
    }

    public List<Payment> getHistory(String username){
        return history.computeIfAbsent(username, k -> new ArrayList<>());
    }

    public void charge(String username, double amount, String note){
        double b = getBalance(username);
        balances.put(username, b + amount);
        getHistory(username).add(new Payment(+amount, note));
    }

    public void refund(String username, double amount, String note){
        double b = getBalance(username);
        balances.put(username, b - amount);
        getHistory(username).add(new Payment(-amount, note));
    }

    public boolean pay(String username, double amount, String note){
        if (amount <= 0) return false;
        double b = getBalance(username);
        balances.put(username, b - amount);
        getHistory(username).add(new Payment(-amount, note));
        return true;
    }
}
