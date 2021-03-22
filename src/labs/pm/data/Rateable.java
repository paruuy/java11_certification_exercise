package labs.pm.data;


//Prevent you write another abstact method (functional interface only must have one abstract method)
@FunctionalInterface
public interface Rateable<T> {
    public static final Raiting DEFAUL_RAITING = Raiting.NOT_RATED;

    // public abstract implicit
    T applyRaiting(Raiting raiting);

    public default T applyRaiting(int stars){

        return applyRaiting(Rateable.convert(stars));
    }
    public default Raiting getRaiting() {
        return DEFAUL_RAITING;
    }

    public static Raiting convert(int stars) {
        return (stars >= 0 && stars <= 5) ? Raiting.values()[stars] : DEFAUL_RAITING;
    }
}
