package com.panut.centralbanking.appR2dbccore.persistence;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

public class UUIDBasedStringEntity implements Persistable<String> {
    @Id
    @Column("id")
    private String id;

    @Transient
    private boolean newEntity = false;


    public void setInsert() {
        if (id != null) throw new IllegalStateException("Id already existed for" +
                " set insert.");

        this.newEntity = true;
        id = UUID.randomUUID().toString();
    }


    @Override
    public @Nullable String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
