package ru.ilonich.igps.model;

import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.data.domain.Persistable;
import ru.ilonich.igps.model.enumerations.ItemType;

import javax.persistence.*;

@MappedSuperclass
@Access(AccessType.FIELD)
public class ItemsBaseEntity implements HasId {
    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "items_seq", sequenceName = "items_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @Access(value = AccessType.PROPERTY)
    protected Integer id;

    @Column(name = "name", nullable = false)
    @NotEmpty
    @Length(min = 2, max = 60)
    @SafeHtml
    protected String name;

    @Column(name = "description", nullable = false)
    @NotEmpty
    @SafeHtml
    protected String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    protected ItemType itemType;

    @Column(name = "published", updatable = false, insertable = false)
    protected long published;

    public ItemsBaseEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public long getPublished() {
        return published;
    }

    public void setPublished(long published) {
        this.published = published;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return (getId() == null);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }

        ItemsBaseEntity that = (ItemsBaseEntity) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId() : 0;
    }
}
