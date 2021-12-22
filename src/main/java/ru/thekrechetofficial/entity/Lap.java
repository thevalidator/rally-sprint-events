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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Entity
public class Lap implements Comparable<Lap>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;
    @Column(name = "lap_number")
    private int lapNumber;
    @Column(name = "lap_time", nullable = false, precision = 3)
    private long lapTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_id", nullable = false)
    private Penalty penalty;
    private int configuration;
    @Column(name = "is_confirmed")
    private boolean isConfirmed = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public int getLapNumber() {
        return lapNumber;
    }

    public void setLapNumber(int lapNumber) {
        this.lapNumber = lapNumber;
    }

    public long getLapTime() {
        return lapTime;
    }

    public void setLapTime(long lapTime) {
        this.lapTime = lapTime;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;
    }

    public int getConfiguration() {
        return configuration;
    }

    public void setConfiguration(int configuration) {
        this.configuration = configuration;
    }

    public boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Override
    public String toString() {
        return "Lap{" + "stage=" + stage.getStageName()
                + ", crew=" + crew.getPilot().getLastName()
                + ", lapNumber=" + lapNumber + ", lapTime="
                + lapTime + ", penalty=" + penalty.getPenaltyTime() + ", config=" + configuration + '}';
    }

    public String getFormattedLapTime() {
        Duration time = Duration.ofMillis(lapTime);//.ofNanos(lapTime);//.ofMillis(lapTime);

        String formattedSummedTime = String.format("%02d:%02d.%03d",
                time.toMinutesPart(),
                time.toSecondsPart(),
                time.toMillisPart());
        return formattedSummedTime;
    }

    public String getFormattedTotalLapTime() {
        long sum = lapTime + penalty.getPenaltyTime();
        Duration total = Duration.ofMillis(sum);

        String formattedSummedTime = String.format("%02d:%02d.%03d",
                total.toMinutesPart(),
                total.toSecondsPart(),
                total.toMillisPart());
        return formattedSummedTime;
    }

    @Override
    public int compareTo(Lap l) {
        if (this.getConfiguration() != l.getConfiguration()) {
            return Integer.compare(this.configuration, l.getConfiguration());
        }
        long thisTime = this.lapTime + this.penalty.getPenaltyTime();
        long anotherTime = l.getLapTime() + l.getPenalty().getPenaltyTime();
        return Long.compare(thisTime, anotherTime);
    }

}
