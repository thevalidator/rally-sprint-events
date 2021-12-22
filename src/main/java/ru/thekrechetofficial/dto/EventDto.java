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
public class EventDto {
    
    private String name;
    private String date;
    private String rulesLink;
    private Long catSetId;
    //private String type;
    private Integer specialSectorAmount;
    private Long trackId;
    private Integer maxCrew;
    private Integer configAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRulesLink() {
        return rulesLink;
    }

    public void setRulesLink(String rulesLink) {
        this.rulesLink = rulesLink;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    public Integer getSpecialSectorAmount() {
        return specialSectorAmount;
    }

    public void setSpecialSectorAmount(Integer specialSectorAmount) {
        this.specialSectorAmount = specialSectorAmount;
    }

    public Long getCatSetId() {
        return catSetId;
    }

    public void setCatSetId(Long catSetId) {
        this.catSetId = catSetId;
    }
    
    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public Integer getMaxCrew() {
        return maxCrew;
    }

    public void setMaxCrew(Integer maxCrew) {
        this.maxCrew = maxCrew;
    }

    public Integer getConfigAmount() {
        return configAmount;
    }

    public void setConfigAmount(Integer configAmount) {
        this.configAmount = configAmount;
    }

}
