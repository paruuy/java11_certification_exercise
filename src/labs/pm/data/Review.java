package labs.pm.data;

import java.io.Serializable;

public class Review implements Comparable<Review>, Serializable{
    
    private Raiting raiting;
    private String comments;
    

    public Review(Raiting raiting, String comments){
        this.raiting = raiting;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Review [ raiting=" + raiting + " comments=" +comments + "]";
    }

    public Raiting getRaiting() {
        return raiting;
    }
    public String getComments() {
        return comments;
    }

    @Override
    public int compareTo(Review other) {
        return other.raiting.ordinal() - this.raiting.ordinal();
    }
}
