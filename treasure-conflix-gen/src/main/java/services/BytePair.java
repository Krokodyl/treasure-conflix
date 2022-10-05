package services;

import java.util.Objects;

public class BytePair {
    
    byte a;
    byte b;

    public BytePair(byte a, byte b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BytePair bytePair = (BytePair) o;
        return a == bytePair.a && b == bytePair.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
