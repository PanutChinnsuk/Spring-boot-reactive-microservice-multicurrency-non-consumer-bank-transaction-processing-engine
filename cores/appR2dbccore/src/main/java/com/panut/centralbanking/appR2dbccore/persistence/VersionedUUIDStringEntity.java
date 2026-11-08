package com.panut.centralbanking.appR2dbccore.persistence;

import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

public class VersionedUUIDStringEntity extends UUIDBasedStringEntity {
    @Version
    @Column("version")
    private Long version;

    public Long getVersion() {
        return version;
    }

    public Long getDefaultVersionValue() {
        return 0L;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
