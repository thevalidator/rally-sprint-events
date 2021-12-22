/*
 * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.thekrechetofficial.dto;


/**
 * @author theValidator <the.validator@yandex.ru>
 */
public class GeneralResultWithSSDto {
    private String startNumber;
    private String fullName;
    private String vehicleName;
    private String category;
    private String[] ssTimes;
    private String[] penaltyTimes;
    private String[] penaltyDescriptions;
    private String[] totalSsTimes;
    private String totalTime;

    public String getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(String startNumber) {
        this.startNumber = startNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String[] getSsTimes() {
        return ssTimes;
    }

    public void setSsTimes(String[] ssTimes) {
        this.ssTimes = ssTimes;
    }

    public String[] getPenaltyTimes() {
        return penaltyTimes;
    }

    public void setPenaltyTimes(String[] penaltyTimes) {
        this.penaltyTimes = penaltyTimes;
    }

    public String[] getTotalSsTimes() {
        return totalSsTimes;
    }

    public void setTotalSsTimes(String[] totalSsTimes) {
        this.totalSsTimes = totalSsTimes;
    }

    public String[] getPenaltyDescriptions() {
        return penaltyDescriptions;
    }

    public void setPenaltyDescriptions(String[] penaltyDescriptions) {
        this.penaltyDescriptions = penaltyDescriptions;
    }
    

}
