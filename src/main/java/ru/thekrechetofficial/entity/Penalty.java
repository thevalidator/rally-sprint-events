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

package ru.thekrechetofficial.entity;

import java.io.Serializable;
import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Entity
public class Penalty implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "penalty_time")
    private long penaltyTime = 0;
    private String description;
    @Column(name = "is_active")
    private boolean isActive;
    

    public long getPenaltyTime() {
        return penaltyTime;
    }

    public void setPenaltyTime(long penaltyTime) {
        this.penaltyTime = penaltyTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getFormattedPenaltyTime() {
        Duration time = Duration.ofMillis(penaltyTime);

        String formattedSummedTime = String.format("%02d:%02d.%03d", 
                time.toMinutesPart(),
                time.toSecondsPart(), 
                time.toMillisPart());
        return formattedSummedTime;
    }
    
}
