/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Finance;

import java.time.LocalDateTime;
/**
 *
 * @author Srija
 */
public class Payment {
    private double amount;
    private LocalDateTime timestamp;
    private String note;

    public Payment(double amount, String note){
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.note = note;
    }

    public double getAmount(){ return amount; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    public String getNote(){ return note; }
}
