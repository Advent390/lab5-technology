package ua.edu.nuos.lab5technology.data;

/** Параметр продукції з одиницею вимірювання. */
public record Parameter(int id, String name, String unit) {
    @Override
    public String toString() {
        return unit == null || unit.isBlank() ? name : name + ", " + unit;
    }
}
