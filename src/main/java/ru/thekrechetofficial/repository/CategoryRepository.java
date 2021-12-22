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
package ru.thekrechetofficial.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.thekrechetofficial.entity.Category;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "select exists(select 1 "
            + "from category c "
            + "where c.category_name = ? and c.category_abbreviation = ?)", nativeQuery = true)
    boolean isExists(String categoryName, String categoryAbbreviation);

    //List<Category> findAllByIsDeleted(boolean b);
    @Query(value = "select c.* from category c "
            + "inner join catset_category cc on c.id = cc.cat_id "
            + "inner join catset c2 on cc.set_id = c2.id "
            + "inner join \"event\" e on c2.id = e.catset_id "
            + "where e.id = ?;", nativeQuery = true)
    List<Category> findAllCategoryFromSetByEventId(Long eventId);

}
