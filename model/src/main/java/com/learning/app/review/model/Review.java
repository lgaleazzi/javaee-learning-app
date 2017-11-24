package com.learning.app.review.model;

import com.learning.app.course.model.Course;
import com.learning.app.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "REVIEW")
public class Review implements Serializable
{
    private static final long serialVersionUID = -3960006515913472935L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Min(0)
    @Max(5)
    @Column
    private int rating;

    @Size(max = 200)
    @Column
    private String comment;

    @Column(name = "created_at", columnDefinition = "DATE", updatable = false)
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Review()
    {
        this.createdAt = LocalDate.now();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public LocalDate getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt)
    {
        this.createdAt = createdAt;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Course getCourse()
    {
        return course;
    }

    public void setCourse(Course course)
    {
        this.course = course;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Review review = (Review)o;

        return id == review.id;

    }

    @Override
    public int hashCode()
    {
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public String toString()
    {
        return "Review{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", created at=" + createdAt +
                '}'
                ;
    }
}
