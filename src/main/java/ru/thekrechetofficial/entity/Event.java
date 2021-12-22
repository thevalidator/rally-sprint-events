/*
 * The Krechet Software
 */

package ru.thekrechetofficial.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Entity
public class Event implements Comparable<Event>, Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "event_name")
    private String eventName;
    
    @Column(name = "event_date")
    private LocalDate eventDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;
    
    @Column(name = "rules_link")
    private String rulesLink;
    
    //@Column(name = "catset_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catset_id", nullable = false)
    private CategorySet categorySet;
    
    @Column(name = "max_crew")
    private int maxCrew;
    
    @Column(name = "config_amount")
    private int configAmount;
    
    @Column(name = "ss_amount")
    private int specialSectorAmount;
    
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private List<Stage> stages = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Crew> crews = new ArrayList<>();
    
    @Column(name = "has_registration")
    private boolean hasRegistration;
    
    @Column(name = "is_managed")
    private boolean isManaged;
    
    @Column(name = "is_finished")
    private boolean isFinished;
    
    @Column(name = "is_deleted")
    private boolean isDeleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getRulesLink() {
        return rulesLink;
    }

    public CategorySet getCategorySet() {
        return categorySet;
    }

    public void setCategorySet(CategorySet categorySet) {
        this.categorySet = categorySet;
    }

    public void setRulesLink(String rulesLink) {
        this.rulesLink = rulesLink;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }    

    public List<Crew> getCrews() {
        return crews;
    }

    public void setCrews(List<Crew> crews) {
        this.crews = crews;
    }

    public int getSpecialSectorAmount() {
        return specialSectorAmount;
    }

    public void setSpecialSectorAmount(int specialSectorAmount) {
        this.specialSectorAmount = specialSectorAmount;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public int getMaxCrew() {
        return maxCrew;
    }

    public void setMaxCrew(int maxCrew) {
        this.maxCrew = maxCrew;
    }

    public int getConfigAmount() {
        return configAmount;
    }

    public void setConfigAmount(int configAmount) {
        this.configAmount = configAmount;
    }

    public boolean getHasRegistration() {
        return hasRegistration;
    }

    public void setHasRegistration(boolean hasRegistration) {
        this.hasRegistration = hasRegistration;
    }

    public boolean isIsManaged() {
        return isManaged;
    }

    public void setIsManaged(boolean isManaged) {
        this.isManaged = isManaged;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getFormattedEventDate() {
      return (DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(new Locale("ru")).format(getEventDate()));
    }

    @Override
    public int compareTo(Event e) {
        int result = e.getEventDate().compareTo(this.eventDate);
        if (result == 0) {
            return e.getEventName().compareTo(this.eventName);
        }
        return result;
    }

}
