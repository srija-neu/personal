/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.Persona;

/**
 *
 * @author kal bugrara
 */
public class StudentAccount {
    private double totalPaid;

    public StudentAccount() {
        totalPaid = 0.0;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void addPayment(double amt) {
        if (amt > 0) {
            totalPaid += amt;
        }
    }

    // Optional helper if you ever need to reset/adjust
    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }
}
