package ua.edu.nuos.lab5technology.data;

/** Група продукції (телефони, телевізори тощо). */
public record ProductGroup(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
