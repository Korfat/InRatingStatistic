package inrating.statistic.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Post {
    private Long id;

    private String slug;

    private Integer numLikes;
    private List<Like> likes;

    private Integer numReposts;
    private List<Repost> reposts;

    private Integer numComments;
    private List<Comment> comments;

    private Integer numMentions;
    private List<Mention> mentions;

    public void setLikes(List<Like> likes) {
        this.likes = likes;
        numLikes = likes.size();
    }

    public void setReposts(List<Repost> reposts) {
        this.reposts = reposts;
        numReposts = reposts.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        numComments = comments.size();
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
        numMentions = mentions.size();
    }
}
