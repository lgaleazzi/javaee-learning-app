package com.learning.app.review.model;

import org.mockito.ArgumentMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;

public class ReviewArgumentMatcher extends ArgumentMatcher<Review>
{
    private Review expectedReview;

    public ReviewArgumentMatcher(Review expectedReview)
    {
        this.expectedReview = expectedReview;
    }

    public static Review reviewEquivalent(Review expectedReview)
    {
        return argThat(new ReviewArgumentMatcher(expectedReview));
    }

    @Override
    public boolean matches(Object object)
    {
        if(!(object instanceof Review))
        {
            return false;
        }
        Review actualReview = (Review) object;

        assertThat(actualReview.getId(), is(equalTo(expectedReview.getId())));
        assertThat(actualReview.getRating(), is(equalTo(expectedReview.getRating())));
        assertThat(actualReview.getComment(), is(equalTo(expectedReview.getComment())));
        assertThat(actualReview.getUser(), is(equalTo(expectedReview.getUser())));
        assertThat(actualReview.getCourse(), is(equalTo(expectedReview.getCourse())));

        return true;
    }
}
