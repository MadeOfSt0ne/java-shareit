package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @JoinColumn(name = "owner_id")
    @ManyToOne
    private User owner;
    @Column(name = "request_id")
    private long request;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Comment> comments;
}
