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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.thekrechetofficial.entity.Track;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    
    @Modifying
    @Query(value = "UPDATE track "
                 + "SET is_active = ? "
                 + "WHERE id = ?", nativeQuery = true)
    void setIsActive(boolean b, long trackId);
    
    @Query(value = "SELECT t.* "
                 + "FROM track t "
                 + "WHERE t.is_active = true "
                 + "ORDER BY track_name ASC", nativeQuery = true)
    List<Track> findActiveTracks();
    
}
