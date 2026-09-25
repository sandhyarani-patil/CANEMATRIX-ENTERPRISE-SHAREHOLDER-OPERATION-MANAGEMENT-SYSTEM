package com.example.farmer.canematrix.dto;

import com.example.farmer.canematrix.entity.SugarcaneSupply;
import java.util.List;

public class VehicleSummaryDTO {
    private String tractorNumber;
    private int totalTrips;
    private double totalTonnes;
    private double totalAmountGenerated;
    private List<SugarcaneSupply> supplies;

    // Конструкटर (Constructor)
    public VehicleSummaryDTO(String tractorNumber, int totalTrips, double totalTonnes, double totalAmountGenerated, List<SugarcaneSupply> supplies) {
        this.tractorNumber = tractorNumber;
        this.totalTrips = totalTrips;
        this.totalTonnes = totalTonnes;
        this.totalAmountGenerated = totalAmountGenerated;
        this.supplies = supplies;
    }

    // Getters आणि Setters
    public String getTractorNumber() { return tractorNumber; }
    public void setTractorNumber(String tractorNumber) { this.tractorNumber = tractorNumber; }

    public int getTotalTrips() { return totalTrips; }
    public void setTotalTrips(int totalTrips) { this.totalTrips = totalTrips; }

    public double getTotalTonnes() { return totalTonnes; }
    public void setTotalTonnes(double totalTonnes) { this.totalTonnes = totalTonnes; }

    public double getTotalAmountGenerated() { return totalAmountGenerated; }
    public void setTotalAmountGenerated(double totalAmountGenerated) { this.totalAmountGenerated = totalAmountGenerated; }

    public List<SugarcaneSupply> getSupplies() { return supplies; }
    public void setSupplies(List<SugarcaneSupply> supplies) { this.supplies = supplies; }
}