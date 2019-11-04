package uk.gov.hmcts.ccd.definition.store.repository.entity;

import com.google.common.collect.Lists;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Table(name = "webhook")
@Entity
@TypeDefs({
    @TypeDef(
        name = "int-array",
        typeClass = IntArrayType.class
    )
})
public class WebhookEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url")
    private String url;

    @Type( type = "int-array" )
    @Column(
        name = "timeouts",
        columnDefinition = "integer[]"
    )
    private Integer[] timeouts;

//    @ElementCollection(fetch = EAGER)
//    @CollectionTable(name = "webhook_timeout", joinColumns = @JoinColumn(name = "webhook_id"))
//    @OrderColumn(name = "index")
//    @Column(name = "timeout")
//    @Fetch(value = FetchMode.SUBSELECT)
//    private final List<Integer> timeouts = new ArrayList<>();
//
//    public void addTimeout(@NotNull final Integer timeout) {
//        timeouts.add(timeout);
//    }
//
//    public List<Integer> getTimeouts() {
//        return timeouts;
//    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimeouts(@NotNull final List<Integer> ints) {
        this.timeouts = ints.toArray(new Integer[0]);
    }

    public List<Integer> getTimeouts() {
        return Lists.newArrayList(timeouts);
    }

//    public void addTimeout(@NotNull final Integer timeout) {
//        List<Integer> list = Ints.asList(timeouts);
//        list.add(timeout);
//        this.timeouts = Ints.toArray(list);
//    }
//
//    public List<Integer> getTimeouts() {
//        return Ints.asList(timeouts);
//    }


}
