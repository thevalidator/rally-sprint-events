/*
 * The Krechet Software
 */
package ru.thekrechetofficial.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Entity
public class Category implements Serializable {
    
//    FWD("Передний привод стандарт"),
//    FWDSPORT("Передний привод спорт"),
//    
//    RWD("Задний привод стандарт"),
//    RWDSPORT("Задний привод спорт"),
//    RWDUSSR("Задний привод стандарт СССР"),
//    
//    ALLWD("Полный привод стандарт"),
//    ALLWDSPORT("Полный привод спорт"),
    
//    R14H("Ралли 1400Н"),
//    R16H("Ралли 1600Н"),
//    R20H("Ралли 2000Н"),
//    RABS("Ралли Абсолютный"),
//    RSTD("УТС РЗК для стандартных авто");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "category_name")
    private String name;
    @Column(name = "category_abbreviation")
    private String abbreviation;

    //private Company company; 

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } 
        
        Category category = (Category) obj;
        return category.getName().equals(this.getName()) 
                && category.getAbbreviation().equals(this.getAbbreviation());
        
    }
    
}
