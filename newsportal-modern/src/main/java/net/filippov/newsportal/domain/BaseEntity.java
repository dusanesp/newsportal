package net.filippov.newsportal.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

/**
 * Base class for all entity-classes. Defines the primary id and version.
 * 
 * @author Oleg Filippov
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1520556867799623763L;

    /**
     * Primary key of the persistent object
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changed to IDENTITY for better compatibility
    @Column(nullable = false, updatable = false)
    private Long id;

    /**
     * Version of the persistent object
     */
    @Version
    @Column(nullable = false, insertable = false, columnDefinition = "INT DEFAULT 0")
    Integer version;

    /**
     * Get the primary key of the persistent object
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
