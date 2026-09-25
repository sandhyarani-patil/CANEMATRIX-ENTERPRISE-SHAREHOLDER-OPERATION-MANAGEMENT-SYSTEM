package com.example.farmer.canematrix.dto;

import com.example.farmer.canematrix.entity.SugarcaneSupply;
import java.time.LocalDate;
import java.util.List;

public class FactorySummaryDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalTrips;
    private double totalTonnes;
    private double totalFactoryPayout;
    private List<SugarcaneSupply> supplies;

    public FactorySummaryDTO(LocalDate startDate, LocalDate endDate, int totalTrips, double totalTonnes, double totalFactoryPayout, List<SugarcaneSupply> supplies) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalTrips = totalTrips;
        this.totalTonnes = totalTonnes;
        this.totalFactoryPayout = totalFactoryPayout;
        this.supplies = supplies;
    }

    // Getters and Setters
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getTotalTrips() { return totalTrips; }
    public void setTotalTrips(int totalTrips) { this.totalTrips = totalTrips; }

    public double getTotalTonnes() { return totalTonnes; }
    public void setTotalTonnes(double totalTonnes) { this.totalTonnes = totalTonnes; }

    public double getTotalFactoryPayout() { return totalFactoryPayout; }
    public void setTotalFactoryPayout(double totalFactoryPayout) { this.totalFactoryPayout = totalFactoryPayout; }

    public List<SugarcaneSupply> getSupplies() { return supplies; }
    public void setSupplies(List<SugarcaneSupply> supplies) { this.supplies = supplies; }
}